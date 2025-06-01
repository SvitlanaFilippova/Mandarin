package com.mandarinkafe.mandarin.shared.cart.domain.usecase

import com.mandarinkafe.mandarin.core.domain.models.Meal
import com.mandarinkafe.mandarin.util.Resource

interface GetCommonRecommendsUseCase {
    suspend operator fun invoke(): Resource<List<Meal>>
}