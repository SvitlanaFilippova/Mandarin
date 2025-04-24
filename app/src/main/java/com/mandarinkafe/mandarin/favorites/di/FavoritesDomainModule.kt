package com.mandarinkafe.mandarin.favorites.di

import com.mandarinkafe.mandarin.favorites.domain.api.FavoritesRepository
import com.mandarinkafe.mandarin.favorites.domain.impl.FavoritesInteractorImpl
import com.mandarinkafe.mandarin.favorites.domain.usecase.FavoritesInteractor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
class FavoritesDomainModule {

    @Provides
    fun provideFavoritesInteractor(repository: FavoritesRepository): FavoritesInteractor {
        return FavoritesInteractorImpl(repository)
    }
}