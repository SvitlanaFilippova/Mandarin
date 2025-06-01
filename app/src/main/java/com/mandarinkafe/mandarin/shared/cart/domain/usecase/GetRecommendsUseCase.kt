package com.mandarinkafe.mandarin.shared.cart.domain.usecase

import com.mandarinkafe.mandarin.core.domain.models.Meal
import com.mandarinkafe.mandarin.util.Resource

interface GetRecommendsUseCase {
    suspend operator fun invoke(cartItems: Set<Meal>): Resource<List<Meal>>
}