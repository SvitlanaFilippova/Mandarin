package com.mandarinkafe.mandarin.features.cart.data.local

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.mandarinkafe.mandarin.core.domain.models.ModifierGroup

object JsonAdapters {

    private val gson = Gson()

    fun listToJson(ids: List<String>): String =
        gson.toJson(ids)

    fun jsonToList(s: String?): List<String> {
        return s?.let {
            val type = object : TypeToken<List<String>>() {}.type
            gson.fromJson(it, type)
        } ?: emptyList()
    }

    fun modsToJson(mods: List<ModifierGroup>): String =
        gson.toJson(mods)

    fun jsonToMods(s: String?): List<ModifierGroup> {
        return s?.let {
            val type = object : TypeToken<List<ModifierGroup>>() {}.type
            gson.fromJson(it, type)
        } ?: emptyList()
    }
}