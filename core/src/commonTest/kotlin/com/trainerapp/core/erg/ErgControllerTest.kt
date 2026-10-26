package com.trainerapp.core.erg

import com.trainerapp.core.domain.ControlResult
import com.trainerapp.core.domain.DisconnectReason
import com.trainerapp.core.domain.TrainerCandidate
import com.trainerapp.core.domain.TrainerClient
import com.trainerapp.core.domain.TrainerProtocol
import com.trainerapp.core.domain.TrainerState
import com.trainerapp.core.domain.TrainerTelemetry
import com.trainerapp.core.store.SettingsFile
import com.trainerapp.core.store.SettingsStore
import com.trainerapp.core.store.TrainerIdentity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/** In-memory settings file so persistence tests need no disk. */
private class MemoryFile(var content: String? = null) : SettingsFile {
    override fun readText(): String? = content
    override fun writeText(text: String) { content = text }
}

/** Scriptable TrainerClient recording every control write in order. */
private class FakeClient : TrainerClient {
    override val state = MutableStateFlow<TrainerState>(TrainerState.Idle)
    override val telemetry = MutableStateFlow(TrainerTelemetry())
    val writes = mutableListOf<Int>()
    var connectCalls = 0
    var failWrites = false
    var failConnect = false
    var nextProtocol: TrainerProtocol? = null
    var disconnectCalls = 0
    val candidate = TrainerCandidate(name = "Hammer 51967", platformId = "fake-platform-id", rssi = -55)

    override suspend fun scan(): Flow<TrainerCandidate> = flowOf(candidate)
    override suspend fun connect(candidate: TrainerCandidate) {
        connectCalls++
        state.value = if (failConnect) {
            TrainerState.Failed(com.trainerapp.core.domain.TrainerError.ConnectionFailed)
        } else {
            TrainerState.Connected(nextProtocol ?: TrainerProtocol.Ftms)
        }
    }
    override suspend fun setTargetPower(watts: Int): ControlResult {
        writes += watts
        return if (failWrites) ControlResult.Failed("simulated failure") else ControlResult.Acknowledged
    }
    override suspend fun disconnect() {
        disconnectCalls++
        state.value = TrainerState.Disconnected(DisconnectReason.USER_REQUESTED)
    }
    fun drop(reason: DisconnectReason = DisconnectReason.TIMEOUT) {
        state.value = TrainerState.Disconnected(reason)
    }
}

class ErgControllerTest {

    private fun store(content: String? = null) = SettingsStore(MemoryFile(content))

    @Test
    fun freshInstallEngagesErgAtDefault120OnConnect() = runTest {
        val fake = FakeClient()
        val controller = ErgController(fake, store(), backgroundScope)
        controller.connect(fake.candidate)
        advanceTimeBy(1_000)
        assertEquals(120, fake.writes.single())
        assertEquals(120, controller.state.value.confirmedWatts)
        assertEquals(ErgLink.RIDING, controller.state.value.link)
    }

    @Test
    fun rememberedTargetEngagesOnReconnectToo() = runTest {
        val file = MemoryFile()
        SettingsStore(file).update { it.copy(targetWatts = 180) }
        val fake = FakeClient()
        val controller = ErgController(fake, SettingsStore(file), backgroundScope)
        controller.connect(fake.candidate)
        advanceTimeBy(1_000)
        assertEquals(listOf(180), fake.writes)
    }

    @Test
    fun targetClampedWithVisibleNotice() = runTest {
        val fake = FakeClient()
        val controller = ErgController(fake, store(), backgroundScope)
        controller.connect(fake.candidate)
        advanceTimeBy(1_000)
        fake.writes.clear()

        controller.setTarget(10)
        assertEquals(50, controller.state.value.targetWatts)
        assertEquals(50, controller.state.value.clampedNotice?.applied)
        assertEquals(10, controller.state.value.clampedNotice?.requested)

        controller.setTarget(900)
        assertEquals(600, controller.state.value.targetWatts)
        assertEquals(600, controller.state.value.clampedNotice?.applied)

        controller.setTarget(49)
        assertEquals(50, controller.state.value.targetWatts)
        controller.setTarget(601)
        assertEquals(600, controller.state.value.targetWatts)
        controller.setTarget(120)
        assertEquals(120, controller.state.value.targetWatts)
        controller.clearClampNotice()
        assertEquals(null, controller.state.value.clampedNotice)
    }

    @Test
    fun rapidAdjustmentsCoalesceToOneWrite() = runTest {
        val fake = FakeClient()
        val controller = ErgController(fake, store(), backgroundScope)
        controller.connect(fake.candidate)
        advanceTimeBy(1_000)
        fake.writes.clear()

        repeat(5) { controller.adjust(ErgController.STEP_WATTS) }
        advanceTimeBy(ErgController.DEBOUNCE_MS + 100)

        assertEquals(listOf(135), fake.writes)
        assertEquals(135, controller.state.value.confirmedWatts)
    }

    @Test
    fun dropWhileRidingReconnectsAndReappliesTarget() = runTest {
        val file = MemoryFile()
        SettingsStore(file).update { it.copy(targetWatts = 180) }
        val fake = FakeClient()
        val controller = ErgController(fake, SettingsStore(file), backgroundScope)
        controller.connect(fake.candidate)
        advanceTimeBy(1_000)
        fake.writes.clear()

        fake.drop(DisconnectReason.OUT_OF_RANGE)
        runCurrent()
        assertEquals(ErgLink.RECONNECTING, controller.state.value.link)

        advanceTimeBy(ErgController.RECONNECT_BACKOFF_MS[0] + 100)
        assertEquals(1, fake.connectCalls - 1)
        assertEquals(listOf(180), fake.writes)
        assertEquals(ErgLink.RIDING, controller.state.value.link)
    }

    @Test
    fun dropWhilePausedReconnectsIntoPausedAtMinLoad() = runTest {
        val file = MemoryFile()
        SettingsStore(file).update { it.copy(targetWatts = 180) }
        val fake = FakeClient()
        val controller = ErgController(fake, SettingsStore(file), backgroundScope)
        controller.connect(fake.candidate)
        advanceTimeBy(1_000)
        fake.writes.clear()

        controller.pause()
        advanceTimeBy(1_000)
        fake.writes.clear()

        fake.drop(DisconnectReason.TIMEOUT)
        advanceTimeBy(ErgController.RECONNECT_BACKOFF_MS[0] + 100)

        assertEquals(ErgLink.PAUSED, controller.state.value.link)
        assertEquals(listOf(50), fake.writes) // min load, never the riding target
    }

    @Test
    fun pauseSurvivesRestart() = runTest {
        val file = MemoryFile()
        val saved = SettingsStore(file).update {
            it.copy(targetWatts = 180, paused = true, trainer = TrainerIdentity("Hammer 51967", "fake-platform-id"))
        }
        assertTrue(saved.paused)

        // New controller instance = app restart, same persisted settings.
        val fake = FakeClient()
        val controller = ErgController(fake, SettingsStore(file), backgroundScope)
        assertTrue(controller.state.value.paused)
        assertEquals(180, controller.state.value.targetWatts)

        controller.connect(fake.candidate)
        advanceTimeBy(1_000)
        assertEquals(listOf(50), fake.writes) // min load on connect, NOT 180
        assertEquals(ErgLink.PAUSED, controller.state.value.link)
    }

    @Test
    fun editsWhilePausedUpdateSavedTargetOnly() = runTest {
        val fake = FakeClient()
        val file = MemoryFile()
        val controller = ErgController(fake, SettingsStore(file), backgroundScope)
        controller.connect(fake.candidate)
        advanceTimeBy(1_000)
        controller.pause()
        advanceTimeBy(1_000)
        fake.writes.clear()

        controller.setTarget(200)
        advanceTimeBy(2_000)
        assertEquals(0, fake.writes.size) // no resistance applied while paused
        assertEquals(200, controller.state.value.targetWatts)
        assertEquals(200, SettingsStore(file).load().targetWatts) // but remembered

        controller.resume()
        advanceTimeBy(1_000)
        assertEquals(listOf(200), fake.writes)
    }

    @Test
    fun disconnectRequestsMinLoadFirstAndReportsConfirmed() = runTest {
        val fake = FakeClient()
        val controller = ErgController(fake, store(), backgroundScope)
        controller.connect(fake.candidate)
        advanceTimeBy(1_000)
        fake.writes.clear()

        controller.disconnect()
        assertEquals(50, fake.writes.singleOrNull()) // min load BEFORE release
        assertEquals(1, fake.disconnectCalls)
        assertEquals(true, controller.state.value.releaseConfirmed)
        assertEquals(ErgLink.DISCONNECTED, controller.state.value.link)
    }

    @Test
    fun failedReleaseIsReportedHonestly() = runTest {
        val fake = FakeClient()
        val controller = ErgController(fake, store(), backgroundScope)
        controller.connect(fake.candidate)
        advanceTimeBy(1_000)
        fake.failWrites = true
        fake.writes.clear()

        controller.disconnect()
        assertEquals(50, fake.writes.singleOrNull()) // attempted
        assertEquals(false, controller.state.value.releaseConfirmed) // NOT claimed as released
        assertEquals(1, fake.disconnectCalls)
    }

    @Test
    fun deliberateDisconnectCancelsReconnection() = runTest {
        val fake = FakeClient()
        val controller = ErgController(fake, store(), backgroundScope)
        controller.connect(fake.candidate)
        advanceTimeBy(1_000)
        fake.drop(DisconnectReason.OUT_OF_RANGE)
        runCurrent()
        assertEquals(ErgLink.RECONNECTING, controller.state.value.link)

        controller.disconnect()
        val connectCallsAfterDisconnect = fake.connectCalls
        advanceTimeBy(ErgController.RECONNECT_BACKOFF_MS.sum() * 2)
        assertEquals(connectCallsAfterDisconnect, fake.connectCalls) // no further retries
        assertEquals(ErgLink.DISCONNECTED, controller.state.value.link)
    }

    @Test
    fun unsupportedProtocolConnectKeepsPreviousTrainer() = runTest {
        val settings = store("""{"trainer":{"name":"Hammer 51967","platformId":"real-hammer"},"targetWatts":155}""")
        val fake = FakeClient()
        val controller = ErgController(fake, settings, backgroundScope)
        runCurrent()

        // Client connects but classifies the device Unsupported (sensor).
        fake.nextProtocol = TrainerProtocol.Unsupported
        val strap = TrainerCandidate(name = "808S 0004026", platformId = "strap-id", rssi = -69)
        controller.connect(strap)
        runCurrent()

        assertEquals("Hammer 51967", settings.load().trainer?.name)
        assertEquals("real-hammer", settings.load().trainer?.platformId)
    }
}
