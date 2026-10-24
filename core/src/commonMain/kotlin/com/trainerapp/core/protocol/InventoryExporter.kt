package com.trainerapp.core.protocol

import kotlinx.serialization.json.Json

/**
 * Serializes the discovered GATT inventory for export.
 * The model has no address fields, so the export is sanitized by construction.
 */
object InventoryExporter {

    private val json = Json { prettyPrint = true; encodeDefaults = true }

    fun export(inventory: GattInventory): String = json.encodeToString(GattInventory.serializer(), inventory)
}
