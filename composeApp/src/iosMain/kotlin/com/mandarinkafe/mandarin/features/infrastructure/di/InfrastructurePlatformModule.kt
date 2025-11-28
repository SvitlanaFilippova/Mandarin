package com.mandarinkafe.mandarin.features.infrastructure.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.mandarinkafe.mandarin.core.data.network.NetworkMonitor
import com.mandarinkafe.mandarin.shared.database.AppDatabase
import com.mandarinkafe.mandarin.shared.datastore.createDataStore
import com.mandarinkafe.mandarin.shared.device.DeviceInfoProvider
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val infrastructurePlatformModule = module {

    // DataStore (iOS-specific, не требует параметров)
    single<DataStore<Preferences>> {
        createDataStore()
    }

    // AppDatabase (iOS-specific)
    single {
        val driver = app.cash.sqldelight.driver.native.NativeSqliteDriver(
            schema = AppDatabase.Schema,
            name = "app.db"
        )
        AppDatabase(driver)
    }

    // NetworkMonitor (iOS не требует параметров)
    single { NetworkMonitor() }

    // DeviceInfoProvider (iOS-specific)
    singleOf(::DeviceInfoProvider)
}

