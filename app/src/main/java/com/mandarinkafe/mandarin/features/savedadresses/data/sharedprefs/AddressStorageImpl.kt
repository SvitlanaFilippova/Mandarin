package com.mandarinkafe.mandarin.features.savedadresses.data.sharedprefs

import android.content.SharedPreferences
import androidx.core.content.edit
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.mandarinkafe.mandarin.core.domain.models.Address
import com.mandarinkafe.mandarin.util.AppLog

class AddressStorageImpl(private val sharedPreferences: SharedPreferences) :
    AddressStorage {

    private val gson = Gson()
    private val listType = object : TypeToken<MutableList<Address>>() {}.type

    override fun getSavedAddresses(): List<Address> {
        return try {
            val json = sharedPreferences.getString(ADDRESSES_KEY, null)
            if (json.isNullOrEmpty()) {
                mutableListOf()
            } else {
                gson.fromJson<List<Address>>(json, listType) ?: emptyList()
            }
        } catch (e: Exception) {
            AppLog.e(
                "Ошибка чтения сохранённых адресов: ${e.message}. Удаляю сохранённые адреса"
            )
            clear()
            mutableListOf()
        }
    }

    override fun saveAddress(item: Address) {
        val current = getSavedAddresses().toMutableList()

        // Удаляем существующий адрес с тем же id (если есть), чтобы заменить его
        val existingIndex = current.indexOfFirst { it.id == item.id }
        if (existingIndex != -1) {
            current[existingIndex] = item
        } else {
            current.add(item)
        }

        val json = gson.toJson(current, listType)
        sharedPreferences.edit { putString(ADDRESSES_KEY, json) }
    }

    override fun removeAddress(id: String) {
        val current = getSavedAddresses().toMutableList()
        val updated = current.filterNot { it.id == id }
        val json = gson.toJson(updated, listType)
        sharedPreferences.edit { putString(ADDRESSES_KEY, json) }
    }

    private fun clear() {
        sharedPreferences.edit { remove(ADDRESSES_KEY) }
    }

    private companion object {
        const val ADDRESSES_KEY = "ADDRESSES_KEY"
    }
}