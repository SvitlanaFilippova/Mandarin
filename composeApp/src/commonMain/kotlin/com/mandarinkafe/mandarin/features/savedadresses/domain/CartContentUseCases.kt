package com.mandarinkafe.mandarin.features.savedadresses.domain

import com.mandarinkafe.mandarin.core.domain.api.ClearCartUseCase
import com.mandarinkafe.mandarin.core.domain.api.ObserveCartItemsUseCase
import com.mandarinkafe.mandarin.features.order.domain.api.CalculateCartTotalWithDiscountUseCase
import com.mandarinkafe.mandarin.features.order.domain.api.ResolvePickupPointUseCase

data class CartContentUseCases(
    val observeCartItems: ObserveCartItemsUseCase,
    val resolvePickupPoint: ResolvePickupPointUseCase,
    val clearCart: ClearCartUseCase,
    val calculateCartTotalWithDiscount: CalculateCartTotalWithDiscountUseCase,
)
