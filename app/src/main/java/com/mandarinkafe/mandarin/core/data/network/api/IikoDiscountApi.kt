package com.mandarinkafe.mandarin.core.data.network.api

import com.mandarinkafe.mandarin.core.data.dto.CustomerCategoriesRequest
import com.mandarinkafe.mandarin.features.infrastructure.data.network.CustomerCategoriesResponse
import com.mandarinkafe.mandarin.features.infrastructure.data.network.DiscountsRequest
import com.mandarinkafe.mandarin.features.infrastructure.data.network.DiscountsResponse
import com.mandarinkafe.mandarin.features.infrastructure.data.network.LoyaltyCustomerByPhoneRequest
import com.mandarinkafe.mandarin.features.infrastructure.data.network.LoyaltyCustomerResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface IikoDiscountApi {
    @POST("/api/1/discounts")
    suspend fun getDiscounts(
        @Body body: DiscountsRequest
    ): DiscountsResponse

    @POST("api/1/loyalty/iiko/customer/info")
    suspend fun getLoyaltyCustomerInfo(
        @Body body: LoyaltyCustomerByPhoneRequest
    ): LoyaltyCustomerResponse

    @POST("/api/1/loyalty/iiko/customer_category")
    suspend fun getAllCustomerCategories(
        @Body body: CustomerCategoriesRequest
    ): CustomerCategoriesResponse
}