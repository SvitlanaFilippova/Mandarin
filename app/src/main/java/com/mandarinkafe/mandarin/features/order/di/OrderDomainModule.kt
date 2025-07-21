package com.mandarinkafe.mandarin.features.order.di

import com.mandarinkafe.mandarin.core.domain.api.DeliveryAreaRepository
import com.mandarinkafe.mandarin.features.order.domain.api.GetDeliveryZoneUseCase
import com.mandarinkafe.mandarin.features.order.domain.impl.GetDeliveryZoneUseCaseImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
class OrderDomainModule {

    @Provides
    fun provideGetDeliveryZoneUseCase(repository: DeliveryAreaRepository): GetDeliveryZoneUseCase {
        return GetDeliveryZoneUseCaseImpl(
            deliveryAreaRepository = repository,
        )
    }
}