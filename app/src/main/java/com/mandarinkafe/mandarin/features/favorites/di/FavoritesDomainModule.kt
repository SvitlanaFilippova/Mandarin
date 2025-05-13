package com.mandarinkafe.mandarin.features.favorites.di

import com.mandarinkafe.mandarin.features.favorites.domain.api.FavoritesRepository
import com.mandarinkafe.mandarin.features.favorites.domain.impl.FavoritesInteractorImpl
import com.mandarinkafe.mandarin.features.favorites.domain.usecase.FavoritesInteractor
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