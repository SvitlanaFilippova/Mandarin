package com.mandarinkafe.mandarin.core.data.network

import com.mandarinkafe.mandarin.core.data.dto.AuthRequest
import com.mandarinkafe.mandarin.core.data.dto.AuthResponse
import com.mandarinkafe.mandarin.core.data.dto.CustomerCategoriesRequest
import com.mandarinkafe.mandarin.core.data.dto.OrganizationsRequest
import com.mandarinkafe.mandarin.core.data.dto.OrganizationsResponse
import com.mandarinkafe.mandarin.features.discounts.data.LoyaltyCustomerByPhoneRequest
import com.mandarinkafe.mandarin.features.discounts.data.network.CustomerCategoriesResponse
import com.mandarinkafe.mandarin.features.discounts.data.network.DiscountsRequest
import com.mandarinkafe.mandarin.features.discounts.data.network.DiscountsResponse
import com.mandarinkafe.mandarin.features.discounts.data.network.LoyaltyCustomerResponse
import com.mandarinkafe.mandarin.features.menu.data.dto.MenuIdResponse
import com.mandarinkafe.mandarin.features.menu.data.dto.MenuResponse
import com.mandarinkafe.mandarin.features.menu.data.network.MenuRequest
import com.mandarinkafe.mandarin.features.order.data.network.CreateDeliveryRequest
import com.mandarinkafe.mandarin.features.order.data.network.dto.CreateDeliveryResponse
import com.mandarinkafe.mandarin.features.order.data.network.dto.paymenttype.PaymentTypesRequest
import com.mandarinkafe.mandarin.features.order.data.network.dto.paymenttype.PaymentTypesResponse
import com.mandarinkafe.mandarin.features.orderinfo.data.network.CancelOrderRequest
import com.mandarinkafe.mandarin.features.orderinfo.data.network.CancelOrderResponse
import com.mandarinkafe.mandarin.features.orderinfo.data.network.OderInfoRequest
import com.mandarinkafe.mandarin.features.orderinfo.data.network.OrdersInfoResponse
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


    @POST("/api/1/discounts")
    suspend fun getDiscounts(
        @Header("Authorization") token: String,
        @Body body: DiscountsRequest
    ): DiscountsResponse

    @POST("/api/1/loyalty/iiko/customer_category")
    suspend fun getAllCustomerCategories(
        @Header("Authorization") token: String,
        @Body body: CustomerCategoriesRequest
    ): CustomerCategoriesResponse
}
