package com.mandarinkafe.mandarin.core.data.network

import com.mandarinkafe.mandarin.core.data.dto.AuthRequest
import com.mandarinkafe.mandarin.core.data.dto.AuthResponse
import com.mandarinkafe.mandarin.core.data.dto.OrganizationsRequest
import com.mandarinkafe.mandarin.core.data.dto.OrganizationsResponse
import com.mandarinkafe.mandarin.features.menu.data.dto.MenuIdResponse
import com.mandarinkafe.mandarin.features.menu.data.dto.MenuResponse
import com.mandarinkafe.mandarin.features.menu.data.network.MenuRequest
import com.mandarinkafe.mandarin.features.order.data.network.LoyaltyCustomerByPhoneRequest
import com.mandarinkafe.mandarin.features.order.data.network.dto.CreateDeliveryResponse
import com.mandarinkafe.mandarin.features.order.data.network.dto.LoyaltyCustomerResponse
import com.mandarinkafe.mandarin.features.order.data.network.dto.createdelivery.CreateDeliveryRequest
import com.mandarinkafe.mandarin.features.order.data.network.dto.createdelivery.paymenttype.PaymentTypesRequest
import com.mandarinkafe.mandarin.features.order.data.network.dto.createdelivery.paymenttype.PaymentTypesResponse
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface IikoApiService {
    @POST("/api/1/access_token")
    suspend fun authenticate(@Body request: AuthRequest): AuthResponse

    @POST("/api/1/organizations")
    suspend fun getOrganizations(
        @Header("Authorization") token: String,
        @Body body: OrganizationsRequest
    ): OrganizationsResponse

    @POST("api/2/menu")
    suspend fun getMenuId(
        @Header("Authorization") token: String,
    ): MenuIdResponse

    @POST("api/2/menu/by_id")
    suspend fun getMenuById(
        @Header("Authorization") token: String,
        @Body body: MenuRequest
    ): MenuResponse

    @POST("api/1/loyalty/iiko/customer/info")
    suspend fun getLoyaltyCustomerInfo(
        @Header("Authorization") token: String,
        @Body body: LoyaltyCustomerByPhoneRequest
    ): LoyaltyCustomerResponse

    @POST("/api/1/payment_types")
    suspend fun getPaymentTypes(
        @Header("Authorization") token: String,
        @Body body: PaymentTypesRequest
    ): PaymentTypesResponse

    @POST("/api/1/deliveries/create")
    suspend fun createDelivery(
        @Header("Authorization") token: String,
        @Body body: CreateDeliveryRequest
    ): CreateDeliveryResponse

}
