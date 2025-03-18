package com.mandarinkafe.mandarin.di

import com.mandarinkafe.mandarin.menu.domain.api.FavoritesInteractor
import com.mandarinkafe.mandarin.menu.domain.api.MenuInteractor
import com.mandarinkafe.mandarin.menu.domain.impl.FavoritesInteractorImpl
import com.mandarinkafe.mandarin.menu.domain.impl.MenuInteractorImpl
import org.koin.dsl.module

val domainModule = module {

    single<FavoritesInteractor> {
        FavoritesInteractorImpl(repository = get())
    }

    single<MenuInteractor> { MenuInteractorImpl(repository = get()) }
}