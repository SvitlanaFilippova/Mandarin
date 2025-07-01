package com.mandarinkafe.mandarin.features.menu.domain.api

import com.mandarinkafe.mandarin.core.domain.models.MealCategory
import com.mandarinkafe.mandarin.util.Resource


interface MenuRepository {
    suspend fun fetchMenu(): Resource<List<MealCategory>>
}