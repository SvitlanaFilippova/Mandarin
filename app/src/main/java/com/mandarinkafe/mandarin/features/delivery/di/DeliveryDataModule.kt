package com.mandarinkafe.mandarin.features.delivery.di

import com.mandarinkafe.mandarin.core.domain.api.DeliveryAreaRepository
import com.mandarinkafe.mandarin.features.delivery.impl.DeliveryAreaRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class DeliveryDataModule {

    @Provides
    fun provideDeliveryAreaRepository(
    ): DeliveryAreaRepository {
        return DeliveryAreaRepositoryImpl()
    }

}