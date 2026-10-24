package com.trainerapp.core.store

import java.nio.file.Files
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class JvmSettingsFileTest {

    @Test
    fun roundTripsThroughARealFile() {
        val dir = Files.createTempDirectory("trainerapp-settings-test")
        val file = JvmSettingsFile(dir.resolve("settings.json"))

        assertNull(file.readText())

        file.writeText("""{"targetWatts":210,"paused":true}""")
        assertEquals("""{"targetWatts":210,"paused":true}""", file.readText())

        val store = SettingsStore(file)
        store.update { it.copy(targetWatts = 95) }
        assertEquals(95, SettingsStore(file).load().targetWatts)

        dir.toFile().deleteRecursively()
    }

    @Test
    fun createsParentDirectoriesOnWrite() {
        val dir = Files.createTempDirectory("trainerapp-settings-test")
        val nested = dir.resolve("trainerapp").resolve("settings.json")

        JvmSettingsFile(nested).writeText("{}")

        assertTrue(Files.exists(nested))
        dir.toFile().deleteRecursively()
    }

    @Test
    fun defaultPathIsConfigDirUnderUserHome() {
        val file = JvmSettingsFile.default()
        val expected = System.getProperty("user.home") + "/.config/trainerapp/settings.json"
        assertEquals(expected, file.path.toString())
    }
}
