package com.mandarinkafe.mandarin.features.mealdetails.domain.api

import com.mandarinkafe.mandarin.core.domain.models.Meal
import com.mandarinkafe.mandarin.util.Resource

interface GetMealByIdUseCase {
    suspend operator fun invoke(id: String): Resource<Meal>
}