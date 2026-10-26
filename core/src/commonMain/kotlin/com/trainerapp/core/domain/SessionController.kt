package com.trainerapp.core.domain

import com.trainerapp.core.store.SettingsStore

/**
 * Session orchestration for the multi-device ride (003).
 *
 * Pure domain logic, transport-agnostic by design: it decides ORDER and
 * persistence, and the transport layer (jvmMain BlueZ/sdbus sessions)
 * executes connects/disconnects in that order. This keeps commonMain free
 * of BLE types and lets a future WASM/Android adapter reuse it unchanged.
 *
 *  - Launch: connect the remembered trainer first (ERG engages as in 002),
 *    then each remembered sensor source (HR before cadence) — decision D6.
 *  - Disconnect-all: the trainer's min-load release happens FIRST, then
 *    every other source is dropped — decision D10.
 *  - Source assignments persist via SettingsStore (settings.json grows
 *    sources.heartRate / sources.cadence).
 */
class SessionController(private val store: SettingsStore) {

    /** Ordered platformIds to connect at launch: trainer, HR source, cadence source. */
    fun launchConnectOrder(): List<String> {
        val s = store.load()
        val ordered = buildList {
            s.trainer?.let { add(it.platformId) }
            s.sources.heartRate?.let { add(it) }
            s.sources.cadence?.let { add(it) }
        }
        return ordered.distinct()
    }

    /**
     * Ordered platformIds to disconnect: the trainer first (its min-load
     * release sequence must run before any other source is dropped), then
     * every other connected source in connection order.
     */
    fun disconnectOrder(connectedPlatformIds: Collection<String>): List<String> {
        val trainer = store.load().trainer?.platformId
        val trainerFirst = connectedPlatformIds.filter { it == trainer }
        val rest = connectedPlatformIds.filter { it != trainer }
        return trainerFirst + rest
    }

    /** Persists the per-data-type source assignment (manual override). */
    fun rememberSources(heartRate: String?, cadence: String?) {
        store.update { it.copy(sources = it.sources.copy(heartRate = heartRate, cadence = cadence)) }
    }

    /** The remembered heart-rate source, if any. */
    fun rememberedHeartRate(): String? = store.load().sources.heartRate
}
