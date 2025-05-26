package com.mandarinkafe.mandarin.features.cart.domain.usecase

import com.mandarinkafe.mandarin.core.domain.models.Meal

interface GetRecommendsUseCase {
    suspend operator fun invoke(cartItems: Set<Meal>): Set<Meal>
}