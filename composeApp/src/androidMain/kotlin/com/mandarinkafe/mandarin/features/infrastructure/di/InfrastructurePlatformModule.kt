package com.mandarinkafe.mandarin.features.infrastructure.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.mandarinkafe.mandarin.core.data.network.NetworkMonitor
import com.mandarinkafe.mandarin.shared.database.AppDatabase
import com.mandarinkafe.mandarin.shared.datastore.createDataStore
import com.mandarinkafe.mandarin.shared.device.DeviceInfoProvider
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val infrastructurePlatformModule = module {
    
    // DataStore (Android-specific, требует Context)
    single<DataStore<Preferences>> {
        createDataStore(androidContext())
    }
    
    // AppDatabase (Android-specific, требует Context)
    single {
        val driver = app.cash.sqldelight.driver.android.AndroidSqliteDriver(
            schema = AppDatabase.Schema,
            context = androidContext(),
            name = "app.db"
        )
        AppDatabase(driver)
    }

    // NetworkMonitor (требует Context на Android)
    single { NetworkMonitor(get()) }

    // DeviceInfoProvider (Android-specific)
    singleOf(::DeviceInfoProvider)
}

