package com.mandarinkafe.mandarin.core.data.network.api

import com.mandarinkafe.mandarin.core.data.dto.CustomerCategoriesRequest
import com.mandarinkafe.mandarin.features.infrastructure.data.network.CustomerCategoriesResponse
import com.mandarinkafe.mandarin.features.infrastructure.data.network.DiscountsRequest
import com.mandarinkafe.mandarin.features.infrastructure.data.network.DiscountsResponse
import com.mandarinkafe.mandarin.features.infrastructure.data.network.LoyaltyCustomerByPhoneRequest
import com.mandarinkafe.mandarin.features.infrastructure.data.network.LoyaltyCustomerResponse
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface IikoDiscountApi {
    @POST("/api/1/discounts")
    suspend fun getDiscounts(
        @Header("Authorization") token: String,
        @Body body: DiscountsRequest
    ): DiscountsResponse

    @POST("api/1/loyalty/iiko/customer/info")
    suspend fun getLoyaltyCustomerInfo(
        @Header("Authorization") token: String,
        @Body body: LoyaltyCustomerByPhoneRequest
    ): LoyaltyCustomerResponse

    @POST("/api/1/loyalty/iiko/customer_category")
    suspend fun getAllCustomerCategories(
        @Header("Authorization") token: String,
        @Body body: CustomerCategoriesRequest
    ): CustomerCategoriesResponse
}