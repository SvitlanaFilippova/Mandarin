package com.mandarinkafe.mandarin.shared.di

import com.mandarinkafe.mandarin.core.data.api.RefreshMenuIfStaleUseCase
import com.mandarinkafe.mandarin.core.domain.api.FavoritesApi
import com.mandarinkafe.mandarin.core.domain.api.GetInitialDataUseCase
import com.mandarinkafe.mandarin.core.domain.api.ObserveCartCountUseCase
import com.mandarinkafe.mandarin.shared.ui.viewmodel.SharedViewModel
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val sharedModule = module {

    // --- SharedViewModel ---
    singleOf(::SharedViewModel)
}
