package com.mandarinkafe.mandarin.features.order.domain.impl

import com.mandarinkafe.mandarin.core.domain.api.MenuCache
import com.mandarinkafe.mandarin.core.domain.models.IncomingOrder
import com.mandarinkafe.mandarin.features.order.data.mapper.toOutgoingOrderItem
import com.mandarinkafe.mandarin.features.order.domain.api.CreateOrderUseCase
import com.mandarinkafe.mandarin.features.order.domain.api.OrderRepository
import com.mandarinkafe.mandarin.features.order.domain.models.OutgoingOrder
import com.mandarinkafe.mandarin.util.Resource
import kotlinx.coroutines.flow.firstOrNull

class CreateOrderUseCaseImpl(
    private val repository: OrderRepository,
    private val menuCache: MenuCache
) : CreateOrderUseCase {
    override suspend fun invoke(outgoingOrder: OutgoingOrder): Resource<IncomingOrder> {
        val updatedOrder = withDeliveryItemIfNeeded(outgoingOrder)
        return repository.createOrder(updatedOrder)
    }

    private suspend fun withDeliveryItemIfNeeded(outgoingOrder: OutgoingOrder): OutgoingOrder {
        if (outgoingOrder.deliveryRealCost <= 0) return outgoingOrder

        val deliveryCategory = menuCache.deliveryItems.firstOrNull()
        val deliveryMeal = deliveryCategory?.meals?.firstOrNull {
            it.name.contains("$DELIVERY_ZONE_STRING ${outgoingOrder.deliveryZoneID}")
        } ?: return outgoingOrder // не добавляем доставку, если не нашли

        val deliveryItem = deliveryMeal.toOutgoingOrderItem()
        val updatedItems = outgoingOrder.items + deliveryItem

        return outgoingOrder.copy(items = updatedItems)
    }

    private companion object {
        const val DELIVERY_ZONE_STRING = "Доставка зона"
    }
}

