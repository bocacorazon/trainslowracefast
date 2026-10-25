package com.trainerapp.core.ble

import com.trainerapp.core.domain.DisconnectReason
import com.trainerapp.core.domain.TrainerCandidate
import com.trainerapp.core.domain.TrainerProtocol
import com.trainerapp.core.domain.TrainerState
import com.trainerapp.core.domain.ControlResult
import com.trainerapp.core.protocol.GattInventory
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertContentEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

/** Contract tests for [BlueFalconTrainerClient] against [FakeBleBackend]. */
class BlueFalconTrainerClientTest {

    private companion object {
        const val DEVICE = "dev-hammer"
        const val POWER_CHAR = "00002a63-0000-1000-8000-00805f9b34fb"
        const val SPEED_CHAR = "00002a65-0000-1000-8000-00805f9b34fb"
        const val VENDOR_CONTROL = "ca31a533-a858-4dc7-a650-fdeb6dad4c14"
        const val CPS_CONTROL = "a026e005-0a7d-4ab3-97fa-f1500f9feb8b"
        const val FTMS_CONTROL = "00002ad9-0000-1000-8000-00805f9b34fb"
    }

    private fun newClient(
        scope: TestScope,
        fake: FakeBleBackend,
        timeMillis: () -> Long = { 0L },
    ): BlueFalconTrainerClient = BlueFalconTrainerClient(
        backend = fake,
        codec = TestCodec,
        timeMillis = timeMillis,
        dispatcher = UnconfinedTestDispatcher(scope.testScheduler),
        ackTimeoutMillis = 50,
    )

    /** Runs [body] with a client whose background jobs are always closed. */
    private suspend fun withClient(
        scope: TestScope,
        fake: FakeBleBackend,
        timeMillis: () -> Long = { 0L },
        body: suspend (BlueFalconTrainerClient) -> Unit,
    ) {
        val client = newClient(scope, fake, timeMillis)
        try {
            body(client)
        } finally {
            client.close()
        }
    }

    private suspend fun scanOne(
        client: BlueFalconTrainerClient,
        fake: FakeBleBackend,
        name: String = "Hammer 51967",
        rssi: Int = -55,
    ): TrainerCandidate {
        fake.scanResults_.value = listOf(
            BleScanResult(DEVICE, name, rssi, emptyList())
        )
        return client.scan().take(1).toList().single()
    }

    private fun hammerServices(): List<BleServiceInfo> {
        val json = String(
            javaClass.classLoader.getResource("fixtures/hammer-gatt-inventory.json")!!
                .readBytes()
        )
        val inventory: GattInventory = Json.decodeFromString(GattInventory.serializer(), json)
        return inventory.services.map { s ->
            BleServiceInfo(
                uuid = s.uuid.lowercase(),
                characteristics = s.characteristics.map { c ->
                    BleCharacteristicInfo(
                        uuid = c.uuid.lowercase(),
                        properties = c.properties.map { it.lowercase() }.toSet(),
                        descriptors = c.descriptors,
                    )
                },
            )
        }
    }

    private suspend fun connectHammer(
        scope: TestScope,
        fake: FakeBleBackend,
        client: BlueFalconTrainerClient,
        timeMillis: () -> Long = { 0L },
    ) {
        fake.services = mapOf(DEVICE to hammerServices())
        val candidate = scanOne(client, fake)
        client.connect(candidate)
    }

    @Test
    fun scan_emits_candidates_without_device_addresses() = runTest {
        val fake = FakeBleBackend()
        withClient(this, fake) { client ->
            val candidate = scanOne(client, fake)

            assertEquals("Hammer 51967", candidate.name)
            assertEquals(-55, candidate.rssi)
            // platformId is a stable opaque token, never the BlueZ address.
            assertTrue(candidate.platformId.startsWith("bf-"))
            assertFalse(DEVICE in candidate.platformId)

            // Second scan reports the same stable platformId for the same device.
            val again = scanOne(client, fake, rssi = -60)
            assertEquals(candidate.platformId, again.platformId)
        }
    }

    @Test
    fun scan_reports_scanning_state_and_returns_to_idle() = runTest {
        val fake = FakeBleBackend()
        withClient(this, fake) { client ->
            val seen = mutableListOf<TrainerState>()
            val job = launch(UnconfinedTestDispatcher(testScheduler)) { client.state.collect { seen += it } }
            fake.scanResults_.value = listOf(BleScanResult(DEVICE, "Hammer 51967", -55, emptyList()))
            client.scan().take(1).toList()
            job.cancel()
            assertTrue(seen.any { it is TrainerState.Scanning }, "saw states: $seen")
            assertTrue(client.state.value is TrainerState.Idle, "ended: ${client.state.value}")
            assertTrue(fake.stopScanCalls.isNotEmpty(), "scan was stopped")
        }
    }

    @Test
    fun connect_classifies_hammer_inventory_as_cycleops() = runTest {
        val fake = FakeBleBackend()
        withClient(this, fake) { client ->
            connectHammer(this, fake, client)
            val st = client.state.value
            assertTrue(st is TrainerState.Connected, "state: $st")
            assertEquals(TrainerProtocol.CycleOpsProprietary, st.protocol)
        }
    }

    @Test
    fun connect_passes_through_discovery_states() = runTest {
        val fake = FakeBleBackend()
        withClient(this, fake) { client ->
            fake.services = mapOf(DEVICE to hammerServices())
            val candidate = scanOne(client, fake)

            val states = mutableListOf<TrainerState>()
            val collector = launch(UnconfinedTestDispatcher(testScheduler)) { client.state.collect { states += it } }
            client.connect(candidate)
            collector.cancel()

            val ci = states.indexOfFirst { it is TrainerState.Connecting }
            val di = states.indexOfFirst { it is TrainerState.DiscoveringServices }
            val co = states.indexOfLast { it is TrainerState.Connected }
            assertTrue(ci in 0 until di, "Connecting before Discovering: $states")
            assertTrue(di in 0 until co, "Discovering before Connected: $states")
        }
    }

    @Test
    fun connect_unknown_candidate_marks_failed() = runTest {
        val fake = FakeBleBackend()
        withClient(this, fake) { client ->
            client.connect(TrainerCandidate("ghost", "bf-doesnotexist", -40))
            assertTrue(client.state.value is TrainerState.Failed, "state: ${client.state.value}")
        }
    }

    @Test
    fun telemetry_power_parsed_from_cycling_power_measurement() = runTest {
        val fake = FakeBleBackend()
        withClient(this, fake) { client ->
            connectHammer(this, fake, client)

            // Real CPM format (verified live in the smoke ride): flags
            // uint16 LE (0x1834), power sint16 LE = 250 W = 0x00FA.
            fake.valueFlow(POWER_CHAR).value = byteArrayOf(0x34, 0x18, 0xfa.toByte(), 0x00)

            assertEquals(250, client.telemetry.value.watts)
            assertFalse(client.telemetry.value.stale)
        }
    }

    // Speed tests intentionally removed: 0x2A65 on the Hammer H1 is Cycling
    // Power Vector, not a speed source. Speed returns when a slice wires a
    // real source (crank/wheel revolution data or a speed sensor).

    @Test
    fun telemetry_goes_stale_after_3s_without_data() = runTest {
        val fake = FakeBleBackend()
        var now = 1_000L
        withClient(this, fake, timeMillis = { now }) { client ->
            connectHammer(this, fake, client)

            fake.valueFlow(POWER_CHAR).value = byteArrayOf(0x34, 0x18, 0xfa.toByte(), 0x00)
            assertFalse(client.telemetry.value.stale)

            now = 5_000L
            advanceTimeBy(2_000)
            assertTrue(client.telemetry.value.stale, "telemetry went stale")
            assertEquals(250, client.telemetry.value.watts, "stale data is kept, flagged only")
        }
    }

    @Test
    fun set_target_power_writes_codec_frame_to_wahoo_legacy_char() = runTest {
        val fake = FakeBleBackend()
        withClient(this, fake) { client ->
            connectHammer(this, fake, client)
            // The H1's real ERG path is the Wahoo-legacy a026e005 char
            // (verified live); it is tried first.
            fake.autoIndicateOnWrite += CPS_CONTROL

            val result = client.setTargetPower(120)
            assertEquals(ControlResult.Acknowledged, result)

            val write = fake.writeLog.single()
            assertEquals(DEVICE, write.deviceKey)
            assertEquals(CPS_CONTROL, write.charKey)
            assertContentEquals(
                TestCodec.encodeTargetPower(120, TrainerProtocol.CycleOpsProprietary),
                write.data,
            )
        }
    }

    @Test
    fun control_falls_back_to_cycleops_vendor_char_when_wahoo_indicate_unsupported() = runTest {
        val fake = FakeBleBackend()
        fake.unsupportedIndicate += CPS_CONTROL
        withClient(this, fake) { client ->
            connectHammer(this, fake, client)
            fake.autoIndicateOnWrite += VENDOR_CONTROL

            val result = client.setTargetPower(200)
            assertEquals(ControlResult.Acknowledged, result)
            assertEquals(VENDOR_CONTROL, fake.writeLog.single().charKey)
        }
    }

    @Test
    fun ftms_trainer_control_goes_to_ftms_control_point() = runTest {
        val fake = FakeBleBackend()
        fake.services = mapOf(
            DEVICE to listOf(
                BleServiceInfo(
                    uuid = "00001826-0000-1000-8000-00805f9b34fb",
                    characteristics = listOf(
                        BleCharacteristicInfo(
                            uuid = FTMS_CONTROL,
                            properties = setOf("write"),
                        ),
                        BleCharacteristicInfo(
                            uuid = "00002a36-0000-1000-8000-00805f9b34fb",
                            properties = setOf("notify"),
                        ),
                    ),
                ),
            )
        )
        withClient(this, fake) { client ->
            val candidate = scanOne(client, fake)
            client.connect(candidate)
            assertEquals(TrainerProtocol.Ftms, (client.state.value as TrainerState.Connected).protocol)
            fake.autoIndicateOnWrite += FTMS_CONTROL

            val result = client.setTargetPower(150)
            assertEquals(ControlResult.Acknowledged, result)
            assertEquals(FTMS_CONTROL, fake.writeLog.single().charKey)
        }
    }

    @Test
    fun ftms_without_indicate_still_writes_and_returns_written() = runTest {
        val fake = FakeBleBackend()
        fake.services = mapOf(
            DEVICE to listOf(
                BleServiceInfo(
                    uuid = "00001826-0000-1000-8000-00805f9b34fb",
                    characteristics = listOf(
                        BleCharacteristicInfo(
                            uuid = FTMS_CONTROL,
                            properties = setOf("write"),
                        ),
                    ),
                ),
            )
        )
        // FTMS control points are write-only on real hardware (no indicate).
        fake.unsupportedIndicate += FTMS_CONTROL
        withClient(this, fake) { client ->
            val candidate = scanOne(client, fake)
            client.connect(candidate)
            assertEquals(TrainerProtocol.Ftms, (client.state.value as TrainerState.Connected).protocol)

            val result = client.setTargetPower(150)
            assertEquals(ControlResult.Written, result)
            assertEquals(FTMS_CONTROL, fake.writeLog.single().charKey)
        }
    }

    @Test
    fun set_target_power_returns_written_when_no_indication() = runTest {
        val fake = FakeBleBackend()
        withClient(this, fake) { client ->
            connectHammer(this, fake, client)

            val result = client.setTargetPower(150)
            assertEquals(ControlResult.Written, result)
        }
    }

    @Test
    fun set_target_power_fails_when_not_connected() = runTest {
        val fake = FakeBleBackend()
        withClient(this, fake) { client ->
            val result = client.setTargetPower(120)
            assertTrue(result is ControlResult.Failed, "result: $result")
        }
    }

    @Test
    fun set_target_power_fails_when_protocol_not_controllable() = runTest {
        val fake = FakeBleBackend()
        fake.services = mapOf(
            DEVICE to listOf(
                BleServiceInfo(
                    uuid = "00001818-0000-1000-8000-00805f9b34fb",
                    characteristics = listOf(
                        BleCharacteristicInfo(POWER_CHAR, setOf("notify")),
                    ),
                ),
            )
        )
        withClient(this, fake) { client ->
            val candidate = scanOne(client, fake)
            client.connect(candidate)
            assertEquals(TrainerProtocol.TelemetryOnly, (client.state.value as TrainerState.Connected).protocol)

            val result = client.setTargetPower(120)
            assertTrue(result is ControlResult.Failed, "result: $result")
            assertTrue(fake.writeLog.isEmpty(), "no control write was sent")
        }
    }

    @Test
    fun request_min_load_writes_min_load_frame() = runTest {
        val fake = FakeBleBackend()
        withClient(this, fake) { client ->
            connectHammer(this, fake, client)
            fake.autoIndicateOnWrite += CPS_CONTROL

            val result = client.requestMinLoad()
            assertEquals(ControlResult.Acknowledged, result)
            assertContentEquals(
                TestCodec.encodeMinLoad(TrainerProtocol.CycleOpsProprietary),
                fake.writeLog.last().data,
            )
        }
    }

    @Test
    fun user_disconnect_reports_user_requested_and_stops_telemetry() = runTest {
        val fake = FakeBleBackend()
        withClient(this, fake) { client ->
            connectHammer(this, fake, client)
            fake.valueFlow(POWER_CHAR).value = byteArrayOf(0x34, 0x18, 0xfa.toByte(), 0x00)

            client.disconnect()

            val st = client.state.value
            assertTrue(st is TrainerState.Disconnected, "state: $st")
            assertEquals(DisconnectReason.USER_REQUESTED, st.reason)
            assertEquals(listOf(DEVICE), fake.disconnectCalls)
            // Notification subscriptions are cancelled on disconnect.
            fake.valueFlow(POWER_CHAR).value = byteArrayOf(0x34, 0x18, 0xf4.toByte(), 0x01)
            assertEquals(250, client.telemetry.value.watts)
        }
    }

    @Test
    fun unexpected_disconnect_reports_out_of_range() = runTest {
        val fake = FakeBleBackend()
        withClient(this, fake) { client ->
            connectHammer(this, fake, client)

            fake.connections_.value = emptyMap()

            val st = client.state.value
            assertTrue(st is TrainerState.Disconnected, "state: $st")
            assertEquals(DisconnectReason.OUT_OF_RANGE, st.reason)
        }
    }
}
