package com.mandarinkafe.mandarin.features.more.domain.impl

import com.mandarinkafe.mandarin.features.more.domain.api.AppStoresRepository
import com.mandarinkafe.mandarin.features.more.domain.api.GetAppStoresUseCase
import com.mandarinkafe.mandarin.util.Resource

class GetAppStoresUseCaseImpl(
    private val repository: AppStoresRepository,
) : GetAppStoresUseCase {
    override suspend fun invoke(): Resource<List<com.mandarinkafe.mandarin.features.more.domain.models.AppStore>> {
        return repository.getAppStores()
    }
}
