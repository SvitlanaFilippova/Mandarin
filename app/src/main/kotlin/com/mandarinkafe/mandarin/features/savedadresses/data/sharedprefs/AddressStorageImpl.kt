package com.mandarinkafe.mandarin.features.savedadresses.data.sharedprefs

import android.content.SharedPreferences
import androidx.core.content.edit
import com.mandarinkafe.mandarin.core.domain.models.Address
import io.github.aakira.napier.Napier
import kotlinx.serialization.json.Json

class AddressStorageImpl(private val sharedPreferences: SharedPreferences) :
    AddressStorage {

    private val json = Json { ignoreUnknownKeys = true }

    override fun getSavedAddresses(): List<Address> {
        return try {
            val jsonString = sharedPreferences.getString(ADDRESSES_KEY, null)
            if (jsonString.isNullOrEmpty()) {
                mutableListOf()
            } else {
                json.decodeFromString<List<Address>>(jsonString)
            }
        } catch (e: Exception) {
            Napier.e(
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

        val jsonString = json.encodeToString(current)
        sharedPreferences.edit { putString(ADDRESSES_KEY, jsonString) }
    }

    override fun removeAddress(id: String) {
        val current = getSavedAddresses().toMutableList()
        val updated = current.filterNot { it.id == id }
        val jsonString = json.encodeToString(updated)
        sharedPreferences.edit { putString(ADDRESSES_KEY, jsonString) }
    }

    private fun clear() {
        sharedPreferences.edit { remove(ADDRESSES_KEY) }
    }

    private companion object {
        const val ADDRESSES_KEY = "ADDRESSES_KEY"
    }
}