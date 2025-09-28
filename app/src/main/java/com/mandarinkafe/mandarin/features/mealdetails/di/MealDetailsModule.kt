package com.mandarinkafe.mandarin.features.mealdetails.di

import com.mandarinkafe.mandarin.core.domain.api.MenuCache
import com.mandarinkafe.mandarin.features.mealdetails.domain.impl.GetAddonsUseCaseImpl
import com.mandarinkafe.mandarin.features.mealdetails.domain.impl.GetMealByIdUseCaseImpl
import com.mandarinkafe.mandarin.features.mealdetails.domain.usecase.GetAddonsUseCase
import com.mandarinkafe.mandarin.features.mealdetails.domain.usecase.GetMealByIdUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class MealDetailsModule {

    @Provides
    @Singleton
    fun provideGetAddonsUseCase(
        cache: MenuCache,
    ): GetAddonsUseCase {
        return GetAddonsUseCaseImpl(
            cache = cache,
        )
    }

    @Provides
    @Singleton
    fun provideGetMealByIdUseCase(
        cache: MenuCache,
    ): GetMealByIdUseCase {
        return GetMealByIdUseCaseImpl(
            cache = cache,
        )
    }


}