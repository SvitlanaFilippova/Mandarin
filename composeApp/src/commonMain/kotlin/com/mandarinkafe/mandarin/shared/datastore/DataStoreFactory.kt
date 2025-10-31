package com.mandarinkafe.mandarin.shared.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import okio.Path.Companion.toPath

/**
 * Функция для создания DataStore в commonMain
 */
fun createDataStore(producePath: () -> String): DataStore<Preferences> =
    PreferenceDataStoreFactory.createWithPath(
        produceFile = { producePath().toPath() }
    )

/**
 * Имя файла для хранения настроек
 */
internal const val DATA_STORE_FILE_NAME = "mandarin.preferences_pb"
