package com.mandarinkafe.mandarin.features.menu.di

import com.mandarinkafe.mandarin.core.data.api.MenuFetcher
import com.mandarinkafe.mandarin.core.domain.api.MenuMetaCache
import com.mandarinkafe.mandarin.features.menu.data.api.ImageValidator
import com.mandarinkafe.mandarin.features.menu.data.impl.BannersRepositoryImpl
import com.mandarinkafe.mandarin.features.menu.data.impl.MenuMetaCacheImpl
import com.mandarinkafe.mandarin.features.menu.data.impl.MenuRepositoryImpl
import com.mandarinkafe.mandarin.features.menu.domain.api.BannersRepository
import com.mandarinkafe.mandarin.features.menu.domain.api.MenuRepository
import com.mandarinkafe.mandarin.features.menu.domain.impl.GetBannersUseCaseImpl
import com.mandarinkafe.mandarin.features.menu.domain.impl.MenuInteractorImpl
import com.mandarinkafe.mandarin.features.menu.domain.usecase.GetBannersUseCase
import com.mandarinkafe.mandarin.features.menu.domain.usecase.MenuInteractor
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val menuModule = module {

    // Data Layer
    singleOf(::MenuRepositoryImpl) {
        bind<MenuRepository>()
        bind<MenuFetcher>()
    }

    singleOf(::BannersRepositoryImpl) { bind<BannersRepository>() }

    singleOf(::MenuMetaCacheImpl) { bind<MenuMetaCache>() }

    // Domain Layer
    singleOf(::MenuInteractorImpl) { bind<MenuInteractor>() }

    singleOf(::GetBannersUseCaseImpl) { bind<GetBannersUseCase>() }
}

