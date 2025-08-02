package com.mandarinkafe.mandarin.util.di

import com.mandarinkafe.mandarin.features.menu.domain.impl.KeywordCategoryFilter
import com.mandarinkafe.mandarin.features.menu.domain.usecase.CategoryFilter
import com.mandarinkafe.mandarin.util.Constants.CATEGORY_RECOMMENDS
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object CategoryFilterModule {

    @Provides
    @Recommends
    fun provideRecommendsCategoryFilter(): CategoryFilter =
        KeywordCategoryFilter(CATEGORY_RECOMMENDS)
}