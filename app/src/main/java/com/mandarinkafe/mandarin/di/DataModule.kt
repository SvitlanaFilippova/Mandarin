package com.mandarinkafe.mandarin.di

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.mandarinkafe.mandarin.menu.data.DtoToDomainConverter
import com.mandarinkafe.mandarin.menu.data.FavoritesRepositoryImpl
import com.mandarinkafe.mandarin.menu.data.LocalStorage
import com.mandarinkafe.mandarin.menu.data.MenuRepositoryImpl
import com.mandarinkafe.mandarin.menu.data.network.IkkoApiService
import com.mandarinkafe.mandarin.menu.data.network.NetworkClient
import com.mandarinkafe.mandarin.menu.data.network.RetrofitNetworkClient
import com.mandarinkafe.mandarin.menu.domain.api.FavoritesRepository
import com.mandarinkafe.mandarin.menu.domain.api.MenuRepository
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val dataModule = module {
    single<IkkoApiService> {
        Retrofit.Builder()
            .baseUrl("https://api-ru.iiko.services")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(
                IkkoApiService::
                class.java
            )
    }
    single<NetworkClient> {
        RetrofitNetworkClient(context = get(), ikkoService = get())
    }
    single<SharedPreferences> {
        androidContext().getSharedPreferences("local_storage", Context.MODE_PRIVATE)
    }
    single<LocalStorage> {
        LocalStorage(sharedPreferences = get())
    }

    single<FavoritesRepository> {
        FavoritesRepositoryImpl(localStorage = get())
    }
    single<MenuRepository> {
        MenuRepositoryImpl(networkClient = get(), converter = get())
    }
    single<MenuRepository> {
        MenuRepositoryImpl(networkClient = get(), converter = get())
    }
    single<Gson> {
        Gson()
    }
    single<DtoToDomainConverter> {
        DtoToDomainConverter(
            favoritesRepository = get()
        )
    }
}