package com.mandarinkafe.mandarin.features.menu.domain.api

import com.mandarinkafe.mandarin.features.menu.domain.models.Banner
import com.mandarinkafe.mandarin.util.Resource

interface BannersRepository {
    suspend fun getBanners(): Resource<List<Banner>>
    suspend fun loadBanners(): Resource<Unit>
}