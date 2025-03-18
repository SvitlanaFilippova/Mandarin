package com.mandarinkafe.mandarin.menu.domain.api

import com.mandarinkafe.mandarin.menu.domain.models.MealCategory
import com.mandarinkafe.mandarin.util.Resource
import kotlinx.coroutines.flow.Flow

interface MenuRepository {
     fun getMenu(): Flow<Resource<List<MealCategory>>>
     }