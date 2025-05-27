package com.mandarinkafe.mandarin.features.menu.di

import com.mandarinkafe.mandarin.core.domain.api.MenuCache
import com.mandarinkafe.mandarin.features.menu.domain.api.BannersRepository
import com.mandarinkafe.mandarin.features.menu.domain.api.MenuRepository
import com.mandarinkafe.mandarin.features.menu.domain.impl.GetBannersUseCaseImpl
import com.mandarinkafe.mandarin.features.menu.domain.impl.MenuInteractorImpl
import com.mandarinkafe.mandarin.features.menu.domain.usecase.GetBannersUseCase
import com.mandarinkafe.mandarin.features.menu.domain.usecase.MenuInteractor
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
        cache: MenuCache,
    ): MenuInteractor {
        return MenuInteractorImpl(
            repository = repository,
            cache = cache
        )
    }

    @Provides
    fun provideGetBannersUseCase(repository: BannersRepository): GetBannersUseCase {
        return GetBannersUseCaseImpl(
            repository = repository,
        )
    }
}