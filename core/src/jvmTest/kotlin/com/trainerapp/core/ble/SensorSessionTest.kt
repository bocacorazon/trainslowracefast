package com.trainerapp.core.ble

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * SensorSession contract tests against [FakeBleBackend]: concurrency with
 * the trainer session, per-device independent reconnect (a strap drop never
 * disturbs trainer state), stale flagging, and deliberate disconnect.
 *
 * The session owns its coroutines on an UnconfinedTestDispatcher (the 002
 * client-test pattern) with a manual clock; every test closes the session in
 * a finally block so the stale ticker never outlives the test (the
 * self-perpetuating delay chain otherwise pins the shared scheduler).
 */
@OptIn(ExperimentalCoroutinesApi::class)
class SensorSessionTest {

    private class Clock(var now: Long = 1_000)

    private companion object {
        const val STRAP = "dev-strap"
        const val TRAINER = "dev-hammer"
        const val HR_CHAR = "00002a37-0000-1000-8000-00805f9b34fb"
    }

    /** A live-captured H808S frame (77 bpm, RR present). */
    private val capturedFrame: ByteArray = byteArrayOf(0x16, 0x4d, 0x06, 0x03)

    private fun FakeBleBackend.withStrapConnected(): FakeBleBackend {
        services = mapOf(
            STRAP to listOf(
                BleServiceInfo(
                    uuid = "0000180d-0000-1000-8000-00805f9b34fb",
                    characteristics = listOf(
                        BleCharacteristicInfo(uuid = HR_CHAR, properties = setOf("notify")),
                    ),
                ),
            ),
        )
        connections_.value = mapOf(STRAP to true, TRAINER to true)
        return this
    }

    private suspend fun TestScope.withSession(
        fake: FakeBleBackend,
        clock: Clock = Clock(),
        body: suspend (SensorSession) -> Unit,
    ) {
        val session = SensorSession(
            backend = fake,
            deviceKey = STRAP,
            telemetryCharacteristicUuid = HR_CHAR,
            dispatcher = UnconfinedTestDispatcher(testScheduler),
            timeMillis = { clock.now },
        )
        try {
            body(session)
        } finally {
            session.close()
        }
    }

    @Test
    fun connect_subscribes_and_parses_captured_frames() = runTest {
        val fake = FakeBleBackend().withStrapConnected()
        withSession(fake) { session ->
            session.connect()

            assertTrue(
                session.state.value == SensorSessionState.Connected,
                "state: ${session.state.value}",
            )
            assertTrue(
                fake.notifyCalls.any { it.deviceKey == STRAP && it.charKey == HR_CHAR && it.enable },
            )

            fake.valueFlow(HR_CHAR).value = capturedFrame
            assertEquals(77, session.telemetry.value.heartRateBpm)
            assertEquals(false, session.telemetry.value.stale)
        }
    }

    @Test
    fun captured_frame_with_two_rr_intervals_parses() = runTest {
        val fake = FakeBleBackend().withStrapConnected()
        withSession(fake) { session ->
            session.connect()

            // Real captured frame "16 4a 67 03 75 03" — 74 bpm, two RR intervals.
            fake.valueFlow(HR_CHAR).value = byteArrayOf(0x16, 0x4a, 0x67, 0x03, 0x75, 0x03)

            assertEquals(74, session.telemetry.value.heartRateBpm)
        }
    }

    @Test
    fun unexpected_drop_reconnects_independently_while_trainer_rides() = runTest {
        val fake = FakeBleBackend().withStrapConnected()
        withSession(fake) { session ->
            session.connect()
            fake.valueFlow(HR_CHAR).value = capturedFrame

            // The strap drops (backend reports it disconnected).
            fake.connections_.value = mapOf(STRAP to false, TRAINER to true)
            session.onUnexpectedDrop()

            assertTrue(
                session.state.value is SensorSessionState.Reconnecting,
                "state: ${session.state.value}",
            )
            assertEquals(1, (session.state.value as SensorSessionState.Reconnecting).attempt)
            // The trainer's connection is untouched by the strap's reconnect loop.
            assertEquals(true, fake.connections_.value[TRAINER])
        }
    }

    @Test
    fun telemetry_flags_stale_after_3s_without_data() = runTest {
        val fake = FakeBleBackend().withStrapConnected()
        val clock = Clock()
        withSession(fake, clock) { session ->
            session.connect()

            fake.valueFlow(HR_CHAR).value = capturedFrame
            advanceTimeBy(1_000)
            assertEquals(false, session.telemetry.value.stale)

            clock.now = 5_000 // 4 s of silence
            advanceTimeBy(2_000)
            assertTrue(session.telemetry.value.stale, "HR telemetry flags stale after 3 s")
            // Stale data is kept, flagged only — same honesty rule as watts.
            assertEquals(77, session.telemetry.value.heartRateBpm)
        }
    }

    @Test
    fun deliberate_disconnect_cancels_reconnect_and_unsubscribes() = runTest {
        val fake = FakeBleBackend().withStrapConnected()
        withSession(fake) { session ->
            session.connect()
            fake.valueFlow(HR_CHAR).value = capturedFrame

            session.disconnect()

            assertEquals(SensorSessionState.Disconnected, session.state.value)
            assertTrue(
                fake.notifyCalls.any { it.deviceKey == STRAP && it.charKey == HR_CHAR && !it.enable },
            )
            assertTrue(fake.disconnectCalls.contains(STRAP))
            // A later unexpected drop report is ignored after a deliberate disconnect.
            fake.connections_.value = mapOf(STRAP to false)
            session.onUnexpectedDrop()
            assertEquals(SensorSessionState.Disconnected, session.state.value)
        }
    }

    @Test
    fun device_without_the_characteristic_connects_no_retry_loop() = runTest {
        val fake = FakeBleBackend()
        fake.services = mapOf(
            STRAP to listOf(
                BleServiceInfo(
                    uuid = "00001800-0000-1000-8000-00805f9b34fb",
                    characteristics = listOf(
                        BleCharacteristicInfo(uuid = "00002a00-0000-1000-8000-00805f9b34fb"),
                    ),
                ),
            ),
        )
        fake.connections_.value = mapOf(STRAP to true)
        withSession(fake) { session ->
            session.connect()

            assertEquals(SensorSessionState.Disconnected, session.state.value)
            assertTrue(
                session.state.value !is SensorSessionState.Reconnecting,
                "no reconnect loop for a device without the HR characteristic",
            )
        }
    }
}