package com.mandarinkafe.mandarin.features.ordershistory.data.mapper

import com.mandarinkafe.mandarin.features.order.data.network.dto.ErrorInfoDto
import com.mandarinkafe.mandarin.features.order.domain.models.CreationStatus
import com.mandarinkafe.mandarin.features.order.domain.models.DeliveryType
import com.mandarinkafe.mandarin.features.order.domain.models.ErrorInfo
import com.mandarinkafe.mandarin.features.orderinfo.domain.models.DeliveryStatus
import com.mandarinkafe.mandarin.features.ordershistory.data.mapper.OrdersHistoryMapper.toDomain
import com.mandarinkafe.mandarin.features.ordershistory.data.mapper.OrdersHistoryMapper.toDto
import com.mandarinkafe.mandarin.features.ordershistory.data.network.dto.SavedOrderDto
import com.mandarinkafe.mandarin.features.ordershistory.domain.models.SavedOrder
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class OrdersHistoryMapperTest {

    @Test
    fun creationErrorDtoMapsToInactiveDomainOrder() {
        val dto = SavedOrderDto(
            id = "order-error",
            timestamp = 1L,
            orderType = "DELIVERY",
            status = "Unconfirmed",
            creationStatus = "Error",
            errorInfo = ErrorInfoDto(
                code = "ProductExcluded",
                message = "Raw iiko error",
                userMessage = "Трайфл временно недоступен",
                errorReason = "product excluded",
            ),
        )

        val order = dto.toDomain()

        assertEquals(CreationStatus.ERROR, order.creationStatus)
        assertEquals(DeliveryStatus.UNCONFIRMED, order.status)
        assertEquals("Трайфл временно недоступен", order.errorInfo?.userMessage)
        assertFalse(order.isActive)
    }

    @Test
    fun creationErrorDomainOrderMapsToDto() {
        val order = SavedOrder(
            id = "order-error",
            timestamp = 1L,
            orderType = DeliveryType.DELIVERY,
            creationStatus = CreationStatus.ERROR,
            errorInfo = ErrorInfo(
                code = "ProductExcluded",
                message = "Raw iiko error",
                userMessage = "Трайфл временно недоступен",
                errorReason = "product excluded",
            ),
        )

        val dto = order.toDto()

        assertEquals("Error", dto.creationStatus)
        assertEquals("Трайфл временно недоступен", dto.errorInfo?.userMessage)
    }
}
