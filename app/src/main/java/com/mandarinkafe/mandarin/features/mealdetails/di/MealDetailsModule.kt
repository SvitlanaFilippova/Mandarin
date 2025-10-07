package com.mandarinkafe.mandarin.features.mealdetails.di

import com.mandarinkafe.mandarin.features.mealdetails.domain.impl.GetAddonsUseCaseImpl
import com.mandarinkafe.mandarin.features.mealdetails.domain.impl.GetMealByIdUseCaseImpl
import com.mandarinkafe.mandarin.features.mealdetails.domain.usecase.GetAddonsUseCase
import com.mandarinkafe.mandarin.features.mealdetails.domain.usecase.GetMealByIdUseCase
import com.mandarinkafe.mandarin.features.mealdetails.presentation.viewmodel.MealDetailsViewModel
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module


val mealDetailsModule = module {

    singleOf(::GetAddonsUseCaseImpl) { bind<GetAddonsUseCase>() }
    singleOf(::GetMealByIdUseCaseImpl) { bind<GetMealByIdUseCase>() }
    viewModelOf(::MealDetailsViewModel)

}