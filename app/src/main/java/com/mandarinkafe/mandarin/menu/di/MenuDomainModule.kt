package com.mandarinkafe.mandarin.menu.di

import com.mandarinkafe.mandarin.di.Addons
import com.mandarinkafe.mandarin.di.Recommends
import com.mandarinkafe.mandarin.menu.domain.api.MenuRepository
import com.mandarinkafe.mandarin.menu.domain.impl.MenuInteractorImpl
import com.mandarinkafe.mandarin.menu.domain.usecase.CategoryFilter
import com.mandarinkafe.mandarin.menu.domain.usecase.MenuInteractor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
class MenuDomainModule {

    @Provides
    fun provideMenuInteractor(
        repository: MenuRepository,
        @Recommends recommendsFilter: CategoryFilter,
        @Addons addonsFilter: CategoryFilter
    ): MenuInteractor {
        return MenuInteractorImpl(
            repository = repository,
            recommendsFilter = recommendsFilter,
            addonsFilter = addonsFilter
        )
    }
}