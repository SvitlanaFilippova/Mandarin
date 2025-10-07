package com.mandarinkafe.mandarin.features.cart.di

import com.mandarinkafe.mandarin.core.data.api.CartReader
import com.mandarinkafe.mandarin.core.domain.api.ClearCartUseCase
import com.mandarinkafe.mandarin.database.AppDatabase
import com.mandarinkafe.mandarin.features.cart.data.impl.CartRepositoryImpl
import com.mandarinkafe.mandarin.features.cart.data.impl.RecommendsSchemaRepositoryImpl
import com.mandarinkafe.mandarin.features.cart.data.local.CartStorage
import com.mandarinkafe.mandarin.features.cart.data.local.SQLDelightCartStorage
import com.mandarinkafe.mandarin.features.cart.domain.api.CartWriter
import com.mandarinkafe.mandarin.features.cart.domain.api.RecommendsSchemaRepository
import com.mandarinkafe.mandarin.features.cart.domain.impl.CartInteractorImpl
import com.mandarinkafe.mandarin.features.cart.domain.impl.ClearCartUseCaseImpl
import com.mandarinkafe.mandarin.features.cart.domain.impl.GetRecommendsUseCaseImpl
import com.mandarinkafe.mandarin.features.cart.domain.usecase.CartInteractor
import com.mandarinkafe.mandarin.features.cart.domain.usecase.GetRecommendsUseCase
import com.mandarinkafe.mandarin.features.cart.presentation.viewmodel.CartViewModel
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module


val cartModule = module {

    // CartQueries & Storage
    single { get<AppDatabase>().cartItemsQueries }
    singleOf(::SQLDelightCartStorage) { bind<CartStorage>() }

    // Cart Repository / Writer / Reader
    singleOf(::CartRepositoryImpl) { bind<CartWriter>(); bind<CartReader>() }

    // Recommends Schema Repository
    singleOf(::RecommendsSchemaRepositoryImpl) { bind<RecommendsSchemaRepository>() }

    // CartInteractor
    singleOf(::CartInteractorImpl) { bind<CartInteractor>() }

    // UseCases
    singleOf(::GetRecommendsUseCaseImpl) { bind<GetRecommendsUseCase>() }
    singleOf(::ClearCartUseCaseImpl) { bind<ClearCartUseCase>() }

    // ViewModel
    viewModelOf(::CartViewModel)
}
