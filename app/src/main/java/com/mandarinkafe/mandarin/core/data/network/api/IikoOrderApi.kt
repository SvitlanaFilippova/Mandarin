package com.mandarinkafe.mandarin.core.data.network.api

import com.mandarinkafe.mandarin.features.infrastructure.data.network.dto.paymenttype.PaymentTypesRequest
import com.mandarinkafe.mandarin.features.infrastructure.data.network.dto.paymenttype.PaymentTypesResponse
import com.mandarinkafe.mandarin.features.order.data.network.CreateDeliveryRequest
import com.mandarinkafe.mandarin.features.order.data.network.dto.CreateDeliveryResponse
import com.mandarinkafe.mandarin.features.orderinfo.data.network.CancelOrderRequest
import com.mandarinkafe.mandarin.features.orderinfo.data.network.CancelOrderResponse
import com.mandarinkafe.mandarin.features.orderinfo.data.network.OderInfoRequest
import com.mandarinkafe.mandarin.features.orderinfo.data.network.OrdersInfoResponse
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface IikoOrderApi {
    @POST("/api/1/deliveries/create")
    suspend fun createDelivery(
        @Header("Authorization") token: String,
        @Body body: CreateDeliveryRequest
    ): CreateDeliveryResponse

    @POST("/api/1/deliveries/by_id")
    suspend fun getOrdersStatusById(
        @Header("Authorization") token: String,
        @Body body: OderInfoRequest
    ): OrdersInfoResponse

    @POST("/api/1/deliveries/cancel")
    suspend fun cancelOrderById(
        @Header("Authorization") token: String,
        @Body body: CancelOrderRequest
    ): CancelOrderResponse

    @POST("/api/1/payment_types")
    suspend fun getPaymentTypes(
        @Header("Authorization") token: String,
        @Body body: PaymentTypesRequest
    ): PaymentTypesResponse

}