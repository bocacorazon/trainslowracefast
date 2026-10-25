package com.trainerapp.core.store

import java.nio.file.Files
import java.nio.file.Path

/**
 * File-backed [SettingsFile] for the desktop app. The default location is
 * `~/.config/trainerapp/settings.json` (increment 002 requirement 11).
 */
class JvmSettingsFile(val path: Path) : SettingsFile {

    override fun readText(): String? =
        try {
            if (Files.exists(path)) Files.readString(path) else null
        } catch (_: java.io.IOException) {
            null
        }

    override fun writeText(content: String) {
        path.parent?.let { Files.createDirectories(it) }
        Files.writeString(path, content)
    }

    companion object {
        fun default(): JvmSettingsFile =
            JvmSettingsFile(Path.of(System.getProperty("user.home"), ".config", "trainerapp", "settings.json"))
    }
}
