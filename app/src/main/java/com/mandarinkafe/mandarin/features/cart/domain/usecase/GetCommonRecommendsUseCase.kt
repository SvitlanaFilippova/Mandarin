package com.mandarinkafe.mandarin.features.cart.domain.usecase

import com.mandarinkafe.mandarin.core.domain.models.Meal
import com.mandarinkafe.mandarin.util.Resource

interface GetCommonRecommendsUseCase {
    suspend operator fun invoke(): Resource<List<Meal>>
}