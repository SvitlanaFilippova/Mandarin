package com.mandarinkafe.mandarin.kmp.di

import com.mandarinkafe.mandarin.core.di.ServiceLocator
import com.mandarinkafe.mandarin.core.di.coreModule
import com.mandarinkafe.mandarin.core.di.coreNetworkModule
import com.mandarinkafe.mandarin.features.account.di.accountModule
import com.mandarinkafe.mandarin.features.address.di.addressModule
import com.mandarinkafe.mandarin.features.auth.di.authModule
import com.mandarinkafe.mandarin.features.cart.di.cartModule
import com.mandarinkafe.mandarin.features.favorites.di.favoritesModule
import com.mandarinkafe.mandarin.features.infrastructure.di.infrastructureModule
import com.mandarinkafe.mandarin.features.mealdetails.di.mealDetailsModule
import com.mandarinkafe.mandarin.features.menu.di.menuModule
import com.mandarinkafe.mandarin.features.more.di.moreModule
import com.mandarinkafe.mandarin.features.order.di.orderModule
import com.mandarinkafe.mandarin.features.orderinfo.di.orderInfoModule
import com.mandarinkafe.mandarin.features.ordershistory.di.ordersHistoryModule
import com.mandarinkafe.mandarin.features.savedadresses.di.savedAddressesModule
import com.mandarinkafe.mandarin.features.search.di.searchModule
import com.mandarinkafe.mandarin.shared.di.sharedModule
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration


fun initKoinCommon(appDeclaration: KoinAppDeclaration = {}): KoinApplication {
    val koinApp = startKoin {
        appDeclaration()
        modules(
            coreModule,
            coreNetworkModule,
            addressModule,
            authModule,
            accountModule,
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
        )

    }

    ServiceLocator.koin = koinApp.koin
    return koinApp
}
