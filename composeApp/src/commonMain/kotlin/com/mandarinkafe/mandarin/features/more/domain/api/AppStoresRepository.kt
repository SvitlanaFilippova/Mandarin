package com.mandarinkafe.mandarin.features.more.domain.api

import com.mandarinkafe.mandarin.features.more.domain.models.AppStore
import com.mandarinkafe.mandarin.util.Resource

interface AppStoresRepository {
    suspend fun getAppStores(): Resource<List<AppStore>>
}
