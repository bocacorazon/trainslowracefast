package com.trainerapp.core.domain

import com.trainerapp.core.store.SettingsFile
import com.trainerapp.core.store.SettingsStore
import com.trainerapp.core.store.TrainerIdentity
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

/**
 * Session orchestration contract tests (003): launch ordering
 * (trainer first, D6), disconnect-all ordering (trainer min-load release
 * first, D10), and source-assignment persistence.
 */
class SessionControllerTest {

    private class FakeSettingsFile(private var text: String? = null) : SettingsFile {
        override fun readText(): String? = text
        override fun writeText(content: String) { text = content }
    }

    private fun storeWith(
        trainer: TrainerIdentity? = TrainerIdentity("Hammer 51967", "trainer-1"),
        heartRate: String? = null,
        cadence: String? = null,
    ): SettingsStore {
        val file = FakeSettingsFile()
        val store = SettingsStore(file)
        store.update {
            it.copy(trainer = trainer, sources = it.sources.copy(heartRate = heartRate, cadence = cadence))
        }
        return store
    }

    @Test
    fun launch_order_is_trainer_first_then_sensors() {
        val controller = SessionController(storeWith(heartRate = "strap-1"))
        assertEquals(listOf("trainer-1", "strap-1"), controller.launchConnectOrder())
    }

    @Test
    fun launch_order_with_cadence_puts_hr_before_cadence() {
        val controller = SessionController(storeWith(heartRate = "strap-1", cadence = "cadence-1"))
        assertEquals(listOf("trainer-1", "strap-1", "cadence-1"), controller.launchConnectOrder())
    }

    @Test
    fun launch_order_is_deduplicated_when_a_sensor_is_also_the_trainer() {
        val controller = SessionController(storeWith(heartRate = "trainer-1"))
        assertEquals(listOf("trainer-1"), controller.launchConnectOrder())
    }

    @Test
    fun launch_order_empty_when_nothing_remembered() {
        val controller = SessionController(storeWith(trainer = null))
        assertEquals(emptyList(), controller.launchConnectOrder())
    }

    @Test
    fun disconnect_order_runs_trainer_release_before_other_sources() {
        val controller = SessionController(storeWith(heartRate = "strap-1"))
        val order = controller.disconnectOrder(listOf("strap-1", "trainer-1", "another-1"))
        assertEquals(listOf("trainer-1", "strap-1", "another-1"), order)
    }

    @Test
    fun disconnect_order_with_no_trainer_keeps_connected_order() {
        val controller = SessionController(storeWith(trainer = null))
        assertEquals(listOf("strap-1", "another-1"), controller.disconnectOrder(listOf("strap-1", "another-1")))
    }

    @Test
    fun source_assignments_persist_across_store_reload() {
        val file = FakeSettingsFile()
        val store = SettingsStore(file)
        store.update { it.copy(trainer = TrainerIdentity("Hammer 51967", "trainer-1")) }

        val controller = SessionController(store)
        controller.rememberSources(heartRate = "strap-1", cadence = null)
        assertEquals("strap-1", controller.rememberedHeartRate())

        // A brand-new store over the same file (app restart) remembers it.
        val reloaded = SessionController(SettingsStore(file))
        assertEquals("strap-1", reloaded.rememberedHeartRate())
        assertEquals(listOf("trainer-1", "strap-1"), reloaded.launchConnectOrder())
    }

    @Test
    fun pre_003_settings_files_load_with_empty_sources() {
        val file = FakeSettingsFile("""{"trainer": null, "targetWatts": 150, "paused": false}""")
        val controller = SessionController(SettingsStore(file))
        assertNull(controller.rememberedHeartRate())
        assertEquals(emptyList(), controller.launchConnectOrder())
    }
}
