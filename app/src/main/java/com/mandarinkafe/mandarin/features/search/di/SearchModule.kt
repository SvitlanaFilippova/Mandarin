package com.mandarinkafe.mandarin.features.search.di

import com.mandarinkafe.mandarin.features.menu.domain.api.MenuRepository
import com.mandarinkafe.mandarin.features.search.data.impl.LabelsRepositoryImpl
import com.mandarinkafe.mandarin.features.search.domain.api.LabelsRepository
import com.mandarinkafe.mandarin.features.search.domain.impl.GetFullMealListUseCaseImpl
import com.mandarinkafe.mandarin.features.search.domain.impl.GetLabelsUseCaseImpl
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
    fun provideLabelsRepository(menuRepository: MenuRepository): LabelsRepository {
        return LabelsRepositoryImpl(
            menuRepository = menuRepository
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
    fun provideGetFullMealListUseCase(repository: MenuRepository): GetFullMealListUseCase =
        GetFullMealListUseCaseImpl(
            repository = repository
        )
}