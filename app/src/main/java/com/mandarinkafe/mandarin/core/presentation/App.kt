package com.mandarinkafe.mandarin.core.presentation

import android.app.Application
import com.mandarinkafe.mandarin.BuildConfig
import com.mandarinkafe.mandarin.core.di.coreModule
import com.mandarinkafe.mandarin.core.di.coreNetworkModule
import com.mandarinkafe.mandarin.features.address.di.addressModule
import com.mandarinkafe.mandarin.features.cart.di.cartModule
import com.mandarinkafe.mandarin.features.favorites.di.favoritesModule
import com.mandarinkafe.mandarin.features.infrastructure.di.infrastructureModule
import com.mandarinkafe.mandarin.features.mealdetails.di.mealDetailsModule
import com.mandarinkafe.mandarin.features.menu.di.menuModule
import com.mandarinkafe.mandarin.features.more.di.moreModule
import com.mandarinkafe.mandarin.features.order.di.orderModule
import com.mandarinkafe.mandarin.features.orderinfo.di.orderInfoModule
import com.mandarinkafe.mandarin.features.ordershistory.di.ordersHistoryModule
import com.mandarinkafe.mandarin.features.search.di.searchModule
import com.mandarinkafe.mandarin.util.Constants.LOCALE_RU
import com.yandex.mapkit.MapKitFactory
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin

class App : Application() {
    override fun onCreate() {
        super.onCreate()

        // Старт Koin
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
                searchModule
            )
        }

        // Настраиваем MapKit
        MapKitFactory.setApiKey(BuildConfig.MAPKIT_API_KEY)
        MapKitFactory.setLocale(LOCALE_RU)


        // Получаем MenuRefreshOnResumeObserver через Koin
//        val menuRefreshObserver: MenuRefreshOnResumeObserver =
//            org.koin.core.context.GlobalContext.get().koin.get()
        // Подписываемся на события жизненного цикла
//        ProcessLifecycleOwner.get().lifecycle.addObserver(menuRefreshObserver)
    }
}