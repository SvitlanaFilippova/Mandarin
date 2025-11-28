package com.mandarinkafe.mandarin.shared.di

import com.mandarinkafe.mandarin.shared.presentation.viewmodel.SharedViewModel
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val sharedModule = module {

    // --- SharedViewModel ---
    singleOf(::SharedViewModel)
}

