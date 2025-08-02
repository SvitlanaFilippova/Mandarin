package com.mandarinkafe.mandarin.features.order.domain.impl

import com.mandarinkafe.mandarin.core.domain.api.MenuCache
import com.mandarinkafe.mandarin.core.domain.mapper.Mapper.toCustomizedMeal
import com.mandarinkafe.mandarin.features.order.domain.api.CreateOrderUseCase
import com.mandarinkafe.mandarin.features.order.domain.api.OrderRepository
import com.mandarinkafe.mandarin.features.order.domain.models.Order
import com.mandarinkafe.mandarin.features.order.domain.models.OrderInfo
import com.mandarinkafe.mandarin.util.Resource
import kotlinx.coroutines.flow.firstOrNull

class CreateOrderUseCaseImpl(
    private val repository: OrderRepository,
    private val menuCache: MenuCache
) : CreateOrderUseCase {
    override suspend fun invoke(order: Order): Resource<OrderInfo> {
        val updatedOrder = withDeliveryItemIfNeeded(order)
        return repository.createOrder(updatedOrder)
    }

    private suspend fun withDeliveryItemIfNeeded(order: Order): Order {
        if (order.deliveryRealCost <= 0) return order

        val deliveryCategory = menuCache.deliveryCategory.firstOrNull()
        val deliveryMeal = deliveryCategory?.meals?.firstOrNull {
            it.name.contains("$DELIVERY_ZONE_STRING ${order.deliveryZoneID}")
        } ?: return order // не добавляем доставку, если не нашли

        val updatedItems = order.cartItems.toMutableMap()
        val deliveryItem = deliveryMeal.toCustomizedMeal()
        updatedItems[deliveryItem] = 1

        return order.copy(cartItems = updatedItems)
    }

    private companion object {
        const val DELIVERY_ZONE_STRING = "Доставка зона"
    }
}