package com.mandarinkafe.mandarin.features.favorites.di

import com.mandarinkafe.mandarin.core.domain.api.FavoritesApi
import com.mandarinkafe.mandarin.core.domain.api.FavoritesReader
import com.mandarinkafe.mandarin.core.domain.api.FavoritesWriter
import com.mandarinkafe.mandarin.features.favorites.data.datastore.FavoritesStorage
import com.mandarinkafe.mandarin.features.favorites.data.datastore.FavoritesStorageImpl
import com.mandarinkafe.mandarin.features.favorites.data.impl.FavoritesRepositoryImpl
import com.mandarinkafe.mandarin.features.favorites.data.impl.FavoritesValidator
import com.mandarinkafe.mandarin.features.favorites.data.remote.FavoritesRemoteDataSource
import com.mandarinkafe.mandarin.features.favorites.data.remote.FavoritesRemoteDataSourceImpl
import com.mandarinkafe.mandarin.features.favorites.domain.impl.FavoritesInteractorImpl
import com.mandarinkafe.mandarin.features.favorites.presentation.viewmodel.FavoritesViewModel
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val favoritesModule = module {

    // DataStore storage
    singleOf(::FavoritesStorageImpl) { bind<FavoritesStorage>() }

    // Remote storage
    singleOf(::FavoritesRemoteDataSourceImpl) { bind<FavoritesRemoteDataSource>() }

    // Validator
    singleOf(::FavoritesValidator)

    // Repository
    singleOf(::FavoritesRepositoryImpl) { bind<FavoritesReader>(); bind<FavoritesWriter>() }

    // Interactor
    singleOf(::FavoritesInteractorImpl) { bind<FavoritesApi>() }

    // ViewModel
    single { FavoritesViewModel(get()) }
}


