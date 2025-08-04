package com.mandarinkafe.mandarin.features.mealdetails.di

import com.mandarinkafe.mandarin.core.domain.api.MenuCache
import com.mandarinkafe.mandarin.features.mealdetails.domain.impl.GetAddonsUseCaseImpl
import com.mandarinkafe.mandarin.features.mealdetails.domain.usecase.GetAddonsUseCase
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
}