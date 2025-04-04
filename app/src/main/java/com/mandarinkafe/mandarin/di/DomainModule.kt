package com.mandarinkafe.mandarin.di

import com.mandarinkafe.mandarin.favorites.domain.api.FavoritesRepository
import com.mandarinkafe.mandarin.favorites.domain.impl.FavoritesInteractorImpl
import com.mandarinkafe.mandarin.favorites.domain.usecase.FavoritesInteractor
import com.mandarinkafe.mandarin.menu.domain.api.MenuRepository
import com.mandarinkafe.mandarin.menu.domain.impl.MenuInteractorImpl
import com.mandarinkafe.mandarin.menu.domain.usecase.MenuInteractor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
class DomainModule {

    @Provides
    fun provideFavoritesInteractor(repository: FavoritesRepository): FavoritesInteractor {
        return FavoritesInteractorImpl(repository)
    }

    @Provides
    fun provideMenuInteractor(repository: MenuRepository): MenuInteractor {
        return MenuInteractorImpl(repository)
    }
}