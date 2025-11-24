package com.mandarinkafe.mandarin.features.menu.di

import com.mandarinkafe.mandarin.core.data.api.MenuFetcher
import com.mandarinkafe.mandarin.core.domain.api.MenuMetaCache
import com.mandarinkafe.mandarin.features.menu.data.impl.AnnouncementsRepositoryImpl
import com.mandarinkafe.mandarin.features.menu.data.impl.BannersRepositoryImpl
import com.mandarinkafe.mandarin.features.menu.data.impl.MenuMetaCacheImpl
import com.mandarinkafe.mandarin.features.menu.data.impl.MenuRepositoryImpl
import com.mandarinkafe.mandarin.features.menu.domain.api.AnnouncementsRepository
import com.mandarinkafe.mandarin.features.menu.domain.api.BannersRepository
import com.mandarinkafe.mandarin.features.menu.domain.api.GetAnnouncementsUseCase
import com.mandarinkafe.mandarin.features.menu.domain.api.GetBannersUseCase
import com.mandarinkafe.mandarin.features.menu.domain.api.MenuInteractor
import com.mandarinkafe.mandarin.features.menu.domain.api.MenuRepository
import com.mandarinkafe.mandarin.features.menu.domain.impl.GetAnnouncementsUseCaseImpl
import com.mandarinkafe.mandarin.features.menu.domain.impl.GetBannersUseCaseImpl
import com.mandarinkafe.mandarin.features.menu.domain.impl.MenuInteractorImpl
import com.mandarinkafe.mandarin.features.menu.presentation.viewmodel.MenuViewModel
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

    singleOf(::AnnouncementsRepositoryImpl) { bind<AnnouncementsRepository>() }

    singleOf(::MenuMetaCacheImpl) { bind<MenuMetaCache>() }

    // Domain Layer
    singleOf(::MenuInteractorImpl) { bind<MenuInteractor>() }

    singleOf(::GetBannersUseCaseImpl) { bind<GetBannersUseCase>() }

    singleOf(::GetAnnouncementsUseCaseImpl) { bind<GetAnnouncementsUseCase>() }

    // ViewModel
    single { MenuViewModel(get(), get(), get(), get(), get(), get()) }
}

