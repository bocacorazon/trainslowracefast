package com.trainerapp.core.store

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class SettingsStoreTest {

    /** In-memory backend standing in for the JSON file in common tests. */
    private class MemoryFile : SettingsFile {
        var content: String? = null
        override fun readText(): String? = content
        override fun writeText(text: String) {
            content = text
        }
    }

    @Test
    fun loadsDefaultsWhenNoFileExists() {
        val store = SettingsStore(MemoryFile())
        val settings = store.load()

        assertNull(settings.trainer)
        assertEquals(DEFAULT_TARGET_WATTS, settings.targetWatts)
        assertEquals(false, settings.paused)
        assertEquals(120, DEFAULT_TARGET_WATTS)
    }

    @Test
    fun savedSettingsRoundTripThroughTheFile() {
        val file = MemoryFile()
        val store = SettingsStore(file)

        val saved = store.update {
            it.copy(
                trainer = TrainerIdentity(name = "HAMMER 1234", platformId = "AA:BB:CC:DD:EE:FF"),
                targetWatts = 245,
                paused = true,
            )
        }

        // The file itself is JSON and round-trips through a fresh store.
        val reloaded = SettingsStore(file).load()
        assertEquals(saved, reloaded)
        assertEquals(TrainerIdentity(name = "HAMMER 1234", platformId = "AA:BB:CC:DD:EE:FF"), reloaded.trainer)
        assertEquals(245, reloaded.targetWatts)
        assertEquals(true, reloaded.paused)
    }

    @Test
    fun corruptFileYieldsDefaultsInsteadOfThrowing() {
        val file = MemoryFile().apply { content = "{not valid json" }
        val store = SettingsStore(file)

        val settings = store.load()

        assertNull(settings.trainer)
        assertEquals(DEFAULT_TARGET_WATTS, settings.targetWatts)
        assertEquals(false, settings.paused)
    }

    @Test
    fun contentIsJsonWithExpectedFields() {
        val file = MemoryFile()
        val store = SettingsStore(file)
        store.update { it.copy(targetWatts = 180) }

        val json = requireNotNull(file.content)
        assertTrue(json.contains("\"targetWatts\":180"))
        assertTrue(json.trimStart().startsWith("{"))
    }
}
