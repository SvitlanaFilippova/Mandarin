package com.mandarinkafe.mandarin.features.search.di

import com.mandarinkafe.mandarin.features.search.data.impl.LabelsRepositoryImpl
import com.mandarinkafe.mandarin.features.search.domain.api.FilterUseCase
import com.mandarinkafe.mandarin.features.search.domain.api.GetFullMealListUseCase
import com.mandarinkafe.mandarin.features.search.domain.api.GetLabelsUseCase
import com.mandarinkafe.mandarin.features.search.domain.api.LabelsRepository
import com.mandarinkafe.mandarin.features.search.domain.impl.FilterUseCaseImpl
import com.mandarinkafe.mandarin.features.search.domain.impl.GetFullMealListUseCaseImpl
import com.mandarinkafe.mandarin.features.search.domain.impl.GetLabelsUseCaseImpl
import com.mandarinkafe.mandarin.features.search.presentation.viewmodel.SearchViewModel
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val searchModule = module {
    singleOf(::LabelsRepositoryImpl) { bind<LabelsRepository>() }
    singleOf(::GetLabelsUseCaseImpl) { bind<GetLabelsUseCase>() }
    singleOf(::GetFullMealListUseCaseImpl) { bind<GetFullMealListUseCase>() }
    singleOf(::FilterUseCaseImpl) { bind<FilterUseCase>() }

    // ViewModel
    single { SearchViewModel(get(), get(), get(), get()) }
}


