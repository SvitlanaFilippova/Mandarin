package com.mandarinkafe.mandarin.features.search.di

import com.mandarinkafe.mandarin.core.domain.api.MenuCache
import com.mandarinkafe.mandarin.features.search.data.impl.LabelsRepositoryImpl
import com.mandarinkafe.mandarin.features.search.domain.api.LabelsRepository
import com.mandarinkafe.mandarin.features.search.domain.impl.FilterUseCaseImpl
import com.mandarinkafe.mandarin.features.search.domain.impl.GetFullMealListUseCaseImpl
import com.mandarinkafe.mandarin.features.search.domain.impl.GetLabelsUseCaseImpl
import com.mandarinkafe.mandarin.features.search.domain.usecase.FilterUseCase
import com.mandarinkafe.mandarin.features.search.domain.usecase.GetFullMealListUseCase
import com.mandarinkafe.mandarin.features.search.domain.usecase.GetLabelsUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class SearchModule {

    @Provides
    @Singleton
    fun provideLabelsRepository(menuCache: MenuCache): LabelsRepository {
        return LabelsRepositoryImpl(
            menuCache = menuCache
        )
    }

    @Provides
    @Singleton
    fun provideGetLabelsUseCase(repository: LabelsRepository): GetLabelsUseCase =
        GetLabelsUseCaseImpl(
            repository = repository
        )

    @Provides
    @Singleton
    fun provideGetFullMealListUseCase(menuCache: MenuCache): GetFullMealListUseCase =
        GetFullMealListUseCaseImpl(
            menuCache = menuCache
        )

    @Provides
    @Singleton
    fun provideGetFilterUseCase(): FilterUseCase =
        FilterUseCaseImpl(
        )
}