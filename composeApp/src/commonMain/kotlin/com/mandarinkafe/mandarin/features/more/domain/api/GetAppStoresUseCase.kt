package com.mandarinkafe.mandarin.features.more.domain.api

import com.mandarinkafe.mandarin.features.more.domain.models.AppStore
import com.mandarinkafe.mandarin.util.Resource

interface GetAppStoresUseCase {
    suspend operator fun invoke(): Resource<List<AppStore>>
}
