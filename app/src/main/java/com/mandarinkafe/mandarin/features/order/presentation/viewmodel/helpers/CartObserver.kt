package com.mandarinkafe.mandarin.features.order.presentation.viewmodel.helpers

import com.mandarinkafe.mandarin.core.domain.api.ObserveCartItemsUseCase
import com.mandarinkafe.mandarin.core.domain.models.CartItem
import com.mandarinkafe.mandarin.features.order.domain.api.ResolvePickupPointUseCase
import com.mandarinkafe.mandarin.features.order.domain.models.OrderPickupPoint
import com.mandarinkafe.mandarin.util.Resource

class CartObserver(
    private val observeCartItems: ObserveCartItemsUseCase,
    private val resolvePickupPoint: ResolvePickupPointUseCase,
    private val recalculateCartSummary: (Int?) -> Unit
) {
    suspend fun observe(onStateUpdate: (List<CartItem>, Boolean, Boolean, OrderPickupPoint) -> Unit) {
        observeCartItems().collect { resource ->
            if (resource is Resource.Success) {
                val items = resource.data ?: return@collect
                val containNotDiscountable = items.any { !it.customizedMeal.meal.discountable }
                val pickupPoint = resolvePickupPoint(items.map { it.customizedMeal }.toSet())
                val isPickupOnly = items.any { it.customizedMeal.meal.isPickupOnly }
                onStateUpdate(items, containNotDiscountable, isPickupOnly, pickupPoint)
                recalculateCartSummary(null)
            }
        }
    }
}