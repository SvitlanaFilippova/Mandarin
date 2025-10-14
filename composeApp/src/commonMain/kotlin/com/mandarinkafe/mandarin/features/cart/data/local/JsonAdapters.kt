package com.mandarinkafe.mandarin.features.cart.data.local

import com.mandarinkafe.mandarin.core.domain.models.ModifierGroup
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer

object JsonAdapters {
    private val json = Json { ignoreUnknownKeys = true }

    fun listToJson(ids: List<String>): String =
        json.encodeToString(ListSerializer(String.serializer()), ids)

    fun jsonToList(s: String?): List<String> {
        return s?.let { json.decodeFromString(ListSerializer(String.serializer()), it) } ?: emptyList()
    }

    fun modsToJson(mods: List<ModifierGroup>): String =
        json.encodeToString(ListSerializer(serializer<ModifierGroup>()), mods)

    fun jsonToMods(s: String?): List<ModifierGroup> {
        return s?.let { json.decodeFromString(ListSerializer(serializer<ModifierGroup>()), it) } ?: emptyList()
    }
}
