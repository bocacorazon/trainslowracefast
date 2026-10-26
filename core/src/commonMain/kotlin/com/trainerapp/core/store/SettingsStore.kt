package com.trainerapp.core.store

import kotlinx.serialization.SerializationException
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

/** First-use default target wattage (grill decision D3). */
const val DEFAULT_TARGET_WATTS: Int = 120

/**
 * Stable identity of the remembered trainer, stored in settings. Deliberately
 * transport-agnostic: `platformId` is whatever the transport layer uses as its
 * stable device address today (BLE MAC today, ANT+ id later).
 */
@Serializable
data class TrainerIdentity(
    val name: String,
    val platformId: String,
    val transport: String = TRANSPORT_BLE,
) {
    companion object {
        const val TRANSPORT_BLE = "ble"
        const val TRANSPORT_ANT_PLUS = "ant+"
    }
}

/** The persisted settings document. */
@Serializable
data class AppSettings(
    val trainer: TrainerIdentity? = null,
    val targetWatts: Int = DEFAULT_TARGET_WATTS,
    val paused: Boolean = false,
    /** Per-data-type source assignments (003): platformId of the device
     *  providing each data type. Forward-compatible: absent in pre-003
     *  settings files, defaults are empty. */
    val sources: Sources = Sources(),
) {
    @Serializable
    data class Sources(
        val heartRate: String? = null,
        val cadence: String? = null,
    )
}

/**
 * Read/write abstraction over the settings file so the store stays free of
 * platform I/O. JVM/desktop binds this to `~/.config/trainerapp/settings.json`.
 */
interface SettingsFile {
    fun readText(): String?
    fun writeText(content: String)
}

/**
 * Loads app settings as JSON through a [SettingsFile].
 * A missing file yields defaults (120 W, not paused, no trainer).
 */
class SettingsStore(private val file: SettingsFile) {

    private val json = Json { ignoreUnknownKeys = true }

    fun load(): AppSettings {
        val text = file.readText() ?: return AppSettings()
        return try {
            json.decodeFromString<AppSettings>(text)
        } catch (_: SerializationException) {
            AppSettings()
        }
    }

    fun save(settings: AppSettings) {
        file.writeText(json.encodeToString(AppSettings.serializer(), settings))
    }

    /** Load-modify-save in one call. */
    fun update(transform: (AppSettings) -> AppSettings): AppSettings {
        val updated = transform(load())
        save(updated)
        return updated
    }
}
