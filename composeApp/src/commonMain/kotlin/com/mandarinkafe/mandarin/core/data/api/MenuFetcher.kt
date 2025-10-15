package com.mandarinkafe.mandarin.core.data.api

import com.mandarinkafe.mandarin.core.domain.models.MealCategory
import com.mandarinkafe.mandarin.util.Resource

interface MenuFetcher {
    suspend fun fetchMenu(): Resource<List<MealCategory>>
}




