package com.mandarinkafe.mandarin.features.menu.di

import com.mandarinkafe.mandarin.features.menu.domain.api.BannersRepository
import com.mandarinkafe.mandarin.features.menu.domain.api.MenuRepository
import com.mandarinkafe.mandarin.features.menu.domain.impl.GetBannersUseCaseImpl
import com.mandarinkafe.mandarin.features.menu.domain.impl.MenuInteractorImpl
import com.mandarinkafe.mandarin.features.menu.domain.usecase.CategoryFilter
import com.mandarinkafe.mandarin.features.menu.domain.usecase.GetBannersUseCase
import com.mandarinkafe.mandarin.features.menu.domain.usecase.MenuInteractor
import com.mandarinkafe.mandarin.util.di.Addons
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
        @Addons addonsFilter: CategoryFilter
    ): MenuInteractor {
        return MenuInteractorImpl(
            repository = repository,
            addonsFilter = addonsFilter
        )
    }

    @Provides
    fun provideGetBannersUseCase(repository: BannersRepository): GetBannersUseCase {
        return GetBannersUseCaseImpl(
            repository = repository,
        )
    }
}