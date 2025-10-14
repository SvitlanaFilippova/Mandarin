package com.mandarinkafe.mandarin.core.di

import com.mandarinkafe.mandarin.shared.ui.viewmodel.SharedViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val coreViewModelModule = module {
    // ViewModel
    viewModelOf(::SharedViewModel)
}

