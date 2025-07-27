package com.mandarinkafe.mandarin.features.delivery.di

import com.mandarinkafe.mandarin.features.address.address.domain.api.DeliveryAreaRepository
import com.mandarinkafe.mandarin.features.address.address.domain.api.GetDeliveryZoneUseCase
import com.mandarinkafe.mandarin.features.address.address.domain.impl.DeliveryAreaRepositoryImpl
import com.mandarinkafe.mandarin.features.address.address.domain.impl.GetDeliveryZoneUseCaseImpl
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

    @Provides
    fun provideGetDeliveryZoneUseCase(repository: DeliveryAreaRepository): GetDeliveryZoneUseCase {
        return GetDeliveryZoneUseCaseImpl(
            deliveryAreaRepository = repository,
        )
    }
}