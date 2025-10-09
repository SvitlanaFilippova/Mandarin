package com.mandarinkafe.mandarin.features.cart.data.local

import com.mandarinkafe.mandarin.core.domain.models.ModifierGroup
import kotlinx.serialization.json.Json

object JsonAdapters {
    private val json = Json { ignoreUnknownKeys = true }

    fun listToJson(ids: List<String>): String =
        json.encodeToString(ids)

    fun jsonToList(s: String?): List<String> {
        return s?.let { json.decodeFromString(it) } ?: emptyList()
    }

    fun modsToJson(mods: List<ModifierGroup>): String =
        json.encodeToString(mods)

    fun jsonToMods(s: String?): List<ModifierGroup> {
        return s?.let { json.decodeFromString(it) } ?: emptyList()
    }
}
