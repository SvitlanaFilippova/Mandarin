package com.mandarinkafe.mandarin.core.presentation

import android.app.Application
import com.mandarinkafe.mandarin.core.data.config.ApiKeys
import com.mandarinkafe.mandarin.core.di.coreModule
import com.mandarinkafe.mandarin.core.di.coreNetworkModule
import com.mandarinkafe.mandarin.features.address.di.addressModule
import com.mandarinkafe.mandarin.features.address.di.addressPlatformModule
import com.mandarinkafe.mandarin.features.cart.di.cartModule
import com.mandarinkafe.mandarin.features.favorites.di.favoritesModule
import com.mandarinkafe.mandarin.features.infrastructure.di.infrastructureModule
import com.mandarinkafe.mandarin.features.infrastructure.di.infrastructurePlatformModule
import com.mandarinkafe.mandarin.features.mealdetails.di.mealDetailsModule
import com.mandarinkafe.mandarin.features.menu.di.menuModule
import com.mandarinkafe.mandarin.features.menu.di.menuPlatformModule
import com.mandarinkafe.mandarin.features.more.di.moreModule
import com.mandarinkafe.mandarin.features.more.di.morePlatformModule
import com.mandarinkafe.mandarin.features.order.di.orderModule
import com.mandarinkafe.mandarin.features.orderinfo.di.orderInfoModule
import com.mandarinkafe.mandarin.features.ordershistory.di.ordersHistoryModule
import com.mandarinkafe.mandarin.features.savedadresses.di.savedAddressesModule
import com.mandarinkafe.mandarin.features.search.di.searchModule
import com.mandarinkafe.mandarin.shared.di.sharedModule
import com.mandarinkafe.mandarin.util.Constants.LOCALE_RU
import com.yandex.mapkit.MapKitFactory
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@App)
            modules(
                coreModule,
                coreNetworkModule,
                addressModule,
                cartModule,
                favoritesModule,
                infrastructureModule,
                mealDetailsModule,
                menuModule,
                moreModule,
                orderModule,
                orderInfoModule,
                ordersHistoryModule,
                savedAddressesModule,
                searchModule,
                sharedModule,
                infrastructurePlatformModule,
                menuPlatformModule,
                addressPlatformModule,
                morePlatformModule
            )
        }

        // Настраиваем MapKit
        MapKitFactory.setApiKey(ApiKeys.MAPKIT_API_KEY)
        MapKitFactory.setLocale(LOCALE_RU)

        // Инициализация Napier
        Napier.base(DebugAntilog())

    }
}
