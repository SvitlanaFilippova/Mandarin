package com.mandarinkafe.mandarin.core.di

import com.mandarinkafe.mandarin.core.data.api.RefreshMenuIfStaleUseCase
import com.mandarinkafe.mandarin.core.data.impl.MenuCacheImpl
import com.mandarinkafe.mandarin.core.data.impl.RefreshMenuIfStaleUseCaseImpl
import com.mandarinkafe.mandarin.core.domain.api.ForceRefreshMenuUseCase
import com.mandarinkafe.mandarin.core.domain.api.GetInitialDataUseCase
import com.mandarinkafe.mandarin.core.domain.api.MenuCache
import com.mandarinkafe.mandarin.core.domain.api.ObserveCartCountUseCase
import com.mandarinkafe.mandarin.core.domain.api.ObserveCartItemsUseCase
import com.mandarinkafe.mandarin.core.domain.impl.ForceRefreshMenuUseCaseImpl
import com.mandarinkafe.mandarin.core.domain.impl.GetInitialDataUseCaseImpl
import com.mandarinkafe.mandarin.core.domain.impl.ObserveCartCountUseCaseImpl
import com.mandarinkafe.mandarin.core.domain.impl.ObserveCartItemsUseCaseImpl
import com.mandarinkafe.mandarin.features.auth.domain.impl.AuthStateChecker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val coreModule = module {
    // Application-wide CoroutineScope
    single<CoroutineScope> {
        CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    }

    //  Menu Cache
    singleOf(::MenuCacheImpl) { bind<MenuCache>() }

    // UseCases
    singleOf(::GetInitialDataUseCaseImpl) { bind<GetInitialDataUseCase>() }
    singleOf(::ForceRefreshMenuUseCaseImpl) { bind<ForceRefreshMenuUseCase>() }
    singleOf(::ObserveCartCountUseCaseImpl) { bind<ObserveCartCountUseCase>() }
    singleOf(::ObserveCartItemsUseCaseImpl) { bind<ObserveCartItemsUseCase>() }
    singleOf(::RefreshMenuIfStaleUseCaseImpl) { bind<RefreshMenuIfStaleUseCase>() }
    singleOf(::AuthStateChecker)
}
