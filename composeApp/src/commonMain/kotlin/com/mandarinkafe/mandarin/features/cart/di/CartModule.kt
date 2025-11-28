package com.mandarinkafe.mandarin.features.cart.di

import com.mandarinkafe.mandarin.core.di.DiConstants
import com.mandarinkafe.mandarin.core.domain.api.CartReader
import com.mandarinkafe.mandarin.core.domain.api.ClearCartUseCase
import com.mandarinkafe.mandarin.features.cart.data.impl.CartRepositoryImpl
import com.mandarinkafe.mandarin.features.cart.data.impl.RecommendsSchemaRepositoryImpl
import com.mandarinkafe.mandarin.features.cart.data.local.CartStorage
import com.mandarinkafe.mandarin.features.cart.data.local.SQLDelightCartStorage
import com.mandarinkafe.mandarin.features.cart.data.network.CartServerApi
import com.mandarinkafe.mandarin.features.cart.data.remote.CartRemoteDataSource
import com.mandarinkafe.mandarin.features.cart.data.remote.CartRemoteDataSourceImpl
import com.mandarinkafe.mandarin.features.cart.domain.api.CartInteractor
import com.mandarinkafe.mandarin.features.cart.domain.api.CartWriter
import com.mandarinkafe.mandarin.features.cart.domain.api.GetRecommendsUseCase
import com.mandarinkafe.mandarin.features.cart.domain.api.RecommendsSchemaRepository
import com.mandarinkafe.mandarin.features.cart.domain.impl.CartInteractorImpl
import com.mandarinkafe.mandarin.features.cart.domain.impl.ClearCartUseCaseImpl
import com.mandarinkafe.mandarin.features.cart.domain.impl.GetRecommendsUseCaseImpl
import com.mandarinkafe.mandarin.features.cart.presentation.viewmodel.CartViewModel
import com.mandarinkafe.mandarin.shared.database.AppDatabase
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.dsl.module


val cartModule = module {

    // CartQueries & Storage
    single { get<AppDatabase>().cartItemsQueries }
    singleOf(::SQLDelightCartStorage) { bind<CartStorage>() }

    // CartServerApi (для авторизованных запросов к Server API)
    single {
        CartServerApi(get(named(DiConstants.SERVER_AUTH_CLIENT_QUALIFIER)))
    }

    // Remote storage
    singleOf(::CartRemoteDataSourceImpl) { bind<CartRemoteDataSource>() }

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
    singleOf(::CartViewModel)
}


