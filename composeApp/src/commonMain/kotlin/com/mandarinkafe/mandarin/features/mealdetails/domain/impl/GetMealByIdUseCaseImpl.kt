package com.mandarinkafe.mandarin.features.mealdetails.domain.impl

import com.mandarinkafe.mandarin.core.domain.api.MenuCache
import com.mandarinkafe.mandarin.core.domain.models.Meal
import com.mandarinkafe.mandarin.features.mealdetails.domain.usecase.GetMealByIdUseCase
import com.mandarinkafe.mandarin.util.Resource

class GetMealByIdUseCaseImpl(private val cache: MenuCache) : GetMealByIdUseCase {
    override suspend fun invoke(id: String): Resource<Meal> {
        val result = cache.getMealById(id)
        return when (result) {
            null -> Resource.ErrorEmptyData()
            else -> Resource.Success(data = result)
        }
    }
}