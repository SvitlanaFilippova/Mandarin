package com.mandarinkafe.mandarin.features.savedadresses.di

import com.mandarinkafe.mandarin.features.savedadresses.presentation.viewmodel.SavedAddressesViewModel
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val savedAddressesModule = module {
    // ViewModel
    singleOf(::SavedAddressesViewModel)
}
