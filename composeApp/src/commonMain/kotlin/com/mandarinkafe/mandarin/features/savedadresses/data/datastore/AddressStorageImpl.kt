package com.mandarinkafe.mandarin.features.savedadresses.data.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.mandarinkafe.mandarin.core.domain.models.Address
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class AddressStorageImpl(
    private val dataStore: DataStore<Preferences>
) : AddressStorage {

    private val json = Json { ignoreUnknownKeys = true }

    override suspend fun getSavedAddresses(): List<Address> =
        dataStore.data
            .map { prefs ->
                val jsonString = prefs[stringPreferencesKey(ADDRESSES_KEY)]
                if (jsonString.isNullOrEmpty()) {
                    emptyList()
                } else {
                    runCatching {
                        json.decodeFromString<List<Address>>(jsonString)
                    }.getOrElse { e ->
                        Napier.e("Ошибка чтения сохранённых адресов", e)
                        emptyList()
                    }
                }
            }
            .first()

    override suspend fun saveAddress(item: Address) {
        val current = getSavedAddresses().toMutableList()
        val existingIndex = current.indexOfFirst { it.id == item.id }
        if (existingIndex != -1) {
            current[existingIndex] = item
        } else {
            current.add(item)
        }

        val jsonString = json.encodeToString<List<Address>>(current)
        dataStore.edit { prefs ->
            prefs[stringPreferencesKey(ADDRESSES_KEY)] = jsonString
        }
    }

    override suspend fun removeAddress(id: String) {
        val updated = getSavedAddresses().filterNot { it.id == id }
        val jsonString = json.encodeToString<List<Address>>(updated)
        dataStore.edit { prefs ->
            prefs[stringPreferencesKey(ADDRESSES_KEY)] = jsonString
        }
    }

    private companion object {
        const val ADDRESSES_KEY = "ADDRESSES_KEY"
    }
}

