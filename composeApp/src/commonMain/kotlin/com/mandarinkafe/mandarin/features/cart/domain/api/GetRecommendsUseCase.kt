package com.mandarinkafe.mandarin.features.cart.domain.api

import com.mandarinkafe.mandarin.core.domain.models.Meal
import com.mandarinkafe.mandarin.features.cart.domain.models.Recommends
import com.mandarinkafe.mandarin.util.Resource

interface GetRecommendsUseCase {
    suspend operator fun invoke(cartItems: Set<Meal>): Resource<Recommends>
}