package com.mandarinkafe.mandarin.features.mealdetails.di

import com.mandarinkafe.mandarin.features.mealdetails.domain.api.GetAddonsUseCase
import com.mandarinkafe.mandarin.features.mealdetails.domain.api.GetMealByIdUseCase
import com.mandarinkafe.mandarin.features.mealdetails.domain.api.ReconstructCustomizedMealUseCase
import com.mandarinkafe.mandarin.features.mealdetails.domain.impl.GetAddonsUseCaseImpl
import com.mandarinkafe.mandarin.features.mealdetails.domain.impl.GetMealByIdUseCaseImpl
import com.mandarinkafe.mandarin.features.mealdetails.domain.impl.ReconstructCustomizedMealUseCaseImpl
import com.mandarinkafe.mandarin.features.mealdetails.presentation.viewmodel.MealDetailsViewModel
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val mealDetailsModule = module {
    singleOf(::GetAddonsUseCaseImpl) { bind<GetAddonsUseCase>() }
    singleOf(::GetMealByIdUseCaseImpl) { bind<GetMealByIdUseCase>() }
    singleOf(::ReconstructCustomizedMealUseCaseImpl) { bind<ReconstructCustomizedMealUseCase>() }

    // ViewModel
    factoryOf(::MealDetailsViewModel)
}





