package com.mandarinkafe.mandarin.features.order.data.impl

import com.mandarinkafe.mandarin.core.data.dto.Response
import com.mandarinkafe.mandarin.core.data.dto.order.CustomerDto
import com.mandarinkafe.mandarin.core.data.network.IikoNetworkClient
import com.mandarinkafe.mandarin.core.domain.api.MenuCache
import com.mandarinkafe.mandarin.core.domain.models.IncomingOrder
import com.mandarinkafe.mandarin.features.order.data.mapper.OrderConstants
import com.mandarinkafe.mandarin.features.order.data.mapper.toDeliveryPointDto
import com.mandarinkafe.mandarin.features.order.data.mapper.toOrderItems
import com.mandarinkafe.mandarin.features.order.data.network.dto.CreateDeliveryResponse
import com.mandarinkafe.mandarin.features.order.data.network.dto.OutgoingDiscountInfoDto
import com.mandarinkafe.mandarin.features.order.data.network.dto.OutgoingDiscountTypeDto
import com.mandarinkafe.mandarin.features.order.data.network.dto.OutgoingOrderDto
import com.mandarinkafe.mandarin.features.order.domain.api.OrderRepository
import com.mandarinkafe.mandarin.features.order.domain.models.DeliveryType
import com.mandarinkafe.mandarin.features.order.domain.models.OutgoingOrder
import com.mandarinkafe.mandarin.features.order.domain.models.OutgoingOrderItem
import com.mandarinkafe.mandarin.features.orderinfo.data.toDomain
import io.github.aakira.napier.Napier
import com.mandarinkafe.mandarin.util.Constants.HTTP_SUCCESS
import com.mandarinkafe.mandarin.util.Constants.NO_CONNECTION
import com.mandarinkafe.mandarin.util.Resource

class OrderRepositoryImpl(
    private val networkClient: IikoNetworkClient,
    private val menuCache: MenuCache,
) : OrderRepository {
    private val logTag = "ORDER API OrderRepository"

    override suspend fun createOrder(outgoingOrder: OutgoingOrder): Resource<IncomingOrder> {
        return try {
            val orderItems = prepareOrderItems(outgoingOrder)
            val discountInfo = createDiscountInfo(outgoingOrder.discountTypeId, orderItems)
            val orderDto = buildOrderDto(outgoingOrder, orderItems, discountInfo)
            val response = networkClient.createDelivery(orderDto)
            handleCreateOrderResponse(response)
        } catch (e: Exception) {
            Napier.e("Exception in createOrder: ${e.message}", e)
            Resource.ErrorOther("Ошибка: ${e.message}")
        }
    }

    private fun prepareOrderItems(outgoingOrder: OutgoingOrder): List<OutgoingOrderItem> {
        return outgoingOrder.items.toOrderItems()
    }

    private fun buildOrderDto(
        outgoingOrder: OutgoingOrder,
        orderItems: List<OutgoingOrderItem>,
        discountInfo: OutgoingDiscountInfoDto?
    ): OutgoingOrderDto {
        return with(outgoingOrder) {
            OutgoingOrderDto(
                phone = OrderConstants.PHONE_PREFIX + phone,
                orderServiceType = if (deliveryType == DeliveryType.DELIVERY) {
                    OrderConstants.DELIVERY_TYPE_DELIVERY
                } else {
                    OrderConstants.DELIVERY_TYPE_PICKUP
                },
                deliveryPoint = chosenAddress?.toDeliveryPointDto(),
                comment = comment,
                customer = CustomerDto(
                    name = name,
                    type = OrderConstants.CUSTOMER_TYPE_ONE_TIME
                ),
                items = orderItems,
                payments = null,
                discountsInfo = discountInfo
            )
        }
    }

    private fun handleCreateOrderResponse(response: Response): Resource<IncomingOrder> {
        return when (response.resultCode) {
            NO_CONNECTION -> Resource.ErrorNoInternet()

            HTTP_SUCCESS -> {
                val addons = menuCache.addonsCategories.value
                val orderInfo = (response as CreateDeliveryResponse).orderInfo?.toDomain(addons)

                when {
                    orderInfo == null ->
                        Resource.ErrorOther("Неизвестная ошибка")

                    orderInfo.errorInfo == null ->
                        Resource.Success(data = orderInfo)

                    else ->
                        Resource.ErrorOther(orderInfo.errorInfo.message ?: "Неизвестная ошибка")
                }
            }

            else -> {
                Napier.e("Server error or empty response. Code: ${response.resultCode}")
                Resource.ErrorOther("Ошибка сервера или пустой ответ")
            }
        }
    }


    private fun createDiscountInfo(
        discountTypeId: String?,
        items: List<OutgoingOrderItem>
    ): OutgoingDiscountInfoDto? {
        if (discountTypeId.isNullOrBlank()) return null

        val selectivePositions = items
            .filter { it.discountable }
            .flatMap { item ->
                val mealPosId = listOf(item.positionId)
                val modifiersPosIds = item.modifiers?.mapNotNull { it.positionId } ?: emptyList()
                mealPosId + modifiersPosIds
            }

        return OutgoingDiscountInfoDto(
            discounts = listOf(
                OutgoingDiscountTypeDto(
                    discountTypeId = discountTypeId,
                    selectivePositions = selectivePositions,
                    type = OrderConstants.DISCOUNT_TYPE_RMS
                )
            )
        )
    }
}