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
import com.mandarinkafe.mandarin.features.order.data.network.dto.OutgoingDiscountItemDto
import com.mandarinkafe.mandarin.features.order.data.network.dto.OutgoingIikoCardDiscountDto
import com.mandarinkafe.mandarin.features.order.data.network.dto.OutgoingOrderDto
import com.mandarinkafe.mandarin.features.order.domain.api.OrderRepository
import com.mandarinkafe.mandarin.features.order.domain.models.DeliveryType
import com.mandarinkafe.mandarin.features.order.domain.models.OutgoingOrder
import com.mandarinkafe.mandarin.features.order.domain.models.OutgoingOrderItem
import com.mandarinkafe.mandarin.features.orderinfo.data.toDomain
import com.mandarinkafe.mandarin.util.Constants.HTTP_SUCCESS
import com.mandarinkafe.mandarin.util.Constants.NO_CONNECTION
import com.mandarinkafe.mandarin.util.Resource
import io.github.aakira.napier.Napier

class OrderRepositoryImpl(
    private val networkClient: IikoNetworkClient,
    private val menuCache: MenuCache,
) : OrderRepository {

    override suspend fun createOrder(outgoingOrder: OutgoingOrder): Resource<IncomingOrder> {
        return try {
            val orderItems = prepareOrderItems(outgoingOrder)
            val discountInfo = createDiscountInfo(
                discountPercent = outgoingOrder.discountPercent,
                items = orderItems
            )
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
        discountInfo: OutgoingDiscountInfoDto?,
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
                    type = OrderConstants.CUSTOMER_TYPE_REGULAR
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
        discountPercent: Int,
        items: List<OutgoingOrderItem>,
    ): OutgoingDiscountInfoDto? {
        if (discountPercent <= 0) return null

        val discountItems = buildList {
            items.forEach { item ->
                // Добавляем само блюдо, если оно discountable
                if (item.discountable) {
                    val discountSum = item.price * item.amount * (discountPercent / PERCENT_DIVISOR)
                    add(
                        OutgoingDiscountItemDto(
                            positionId = item.positionId,
                            sum = discountSum,
                            amount = item.amount
                        )
                    )
                }

                // Добавляем модификаторы, если они discountable
                item.modifiers?.forEach { modifier ->
                    if (modifier.discountable) {
                        val discountSum = modifier.price * modifier.amount * (discountPercent / PERCENT_DIVISOR)
                        add(
                            OutgoingDiscountItemDto(
                                positionId = modifier.positionId,
                                sum = discountSum,
                                amount = modifier.amount
                            )
                        )
                    }
                }
            }
        }

        if (discountItems.isEmpty()) return null

        return OutgoingDiscountInfoDto(
            discounts = listOf(
                OutgoingIikoCardDiscountDto(
                    programId = DISCOUNT_PROGRAM_ID,
                    programName = DISCOUNT_PROGRAM_NAME,
                    discountItems = discountItems,
                    type = OrderConstants.DISCOUNT_TYPE_IIKO_CARD
                )
            )
        )
    }

    private companion object {
        const val DISCOUNT_PROGRAM_ID = "f8990000-6beb-ac1f-a9f4-08dd129f16da"
        const val DISCOUNT_PROGRAM_NAME = "Скидки"
        const val PERCENT_DIVISOR = 100.0
    }
}