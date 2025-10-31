package com.mandarinkafe.mandarin.features.menu.domain.api

import com.mandarinkafe.mandarin.features.menu.domain.models.Banner
import com.mandarinkafe.mandarin.util.Resource

interface GetBannersUseCase {
    suspend operator fun invoke(): Resource<List<Banner>>
}