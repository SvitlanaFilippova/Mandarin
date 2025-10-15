package com.mandarinkafe.mandarin.features.search.di

import com.mandarinkafe.mandarin.features.search.presentation.viewmodel.SearchViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val searchPlatformModule = module {
    viewModelOf(::SearchViewModel)
}

