package com.mandarinkafe.mandarin.features.cart.domain.usecase

import com.mandarinkafe.mandarin.core.domain.models.Meal
import com.mandarinkafe.mandarin.features.cart.presentation.viewmodel.Recommends
import com.mandarinkafe.mandarin.util.Resource

interface GetRecommendsUseCase {
    suspend operator fun invoke(cartItems: Set<Meal>): Resource<Recommends>
}