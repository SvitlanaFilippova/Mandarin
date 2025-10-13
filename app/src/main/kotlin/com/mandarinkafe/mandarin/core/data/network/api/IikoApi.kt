package com.mandarinkafe.mandarin.core.data.network.api

import com.mandarinkafe.mandarin.core.data.dto.CustomerCategoriesRequest
import com.mandarinkafe.mandarin.core.data.dto.OrganizationsRequest
import com.mandarinkafe.mandarin.core.data.dto.OrganizationsResponse
import com.mandarinkafe.mandarin.features.infrastructure.data.network.AliveTerminalGroupsRequest
import com.mandarinkafe.mandarin.features.infrastructure.data.network.CustomerCategoriesResponse
import com.mandarinkafe.mandarin.features.infrastructure.data.network.DiscountsRequest
import com.mandarinkafe.mandarin.features.infrastructure.data.network.DiscountsResponse
import com.mandarinkafe.mandarin.features.infrastructure.data.network.LoyaltyCustomerByPhoneRequest
import com.mandarinkafe.mandarin.features.infrastructure.data.network.LoyaltyCustomerResponse
import com.mandarinkafe.mandarin.features.infrastructure.data.network.TerminalGroupsIdsRequest
import com.mandarinkafe.mandarin.features.infrastructure.data.network.dto.AliveTerminalGroupsResponse
import com.mandarinkafe.mandarin.features.infrastructure.data.network.dto.TerminalGroupsIdsResponse
import com.mandarinkafe.mandarin.features.infrastructure.data.network.dto.paymenttype.PaymentTypesRequest
import com.mandarinkafe.mandarin.features.infrastructure.data.network.dto.paymenttype.PaymentTypesResponse
import com.mandarinkafe.mandarin.features.order.data.network.CreateDeliveryRequest
import com.mandarinkafe.mandarin.features.order.data.network.dto.CreateDeliveryResponse
import com.mandarinkafe.mandarin.features.orderinfo.data.network.CancelOrderRequest
import com.mandarinkafe.mandarin.features.orderinfo.data.network.CancelOrderResponse
import com.mandarinkafe.mandarin.features.orderinfo.data.network.OderInfoRequest
import com.mandarinkafe.mandarin.features.orderinfo.data.network.OrdersInfoResponse
import io.github.aakira.napier.Napier
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody

class IikoApi(
    private val client: HttpClient,
) {

    // Список организаций
    suspend fun getOrganizations(body: OrganizationsRequest): OrganizationsResponse {
        return try {
            val response = client.post("/api/1/organizations") {
                setBody(body)
            }
            response.body()
        } catch (e: Exception) {
            logError("getOrganizations", e)
            throw e
        }
    }

    // Терминалы
    suspend fun getTerminalGroupsIds(body: TerminalGroupsIdsRequest): TerminalGroupsIdsResponse {
        return try {
            val response = client.post("/api/1/terminal_groups") {
                setBody(body)
            }
            response.body()
        } catch (e: Exception) {
            logError("getTerminalGroupsIds", e)
            throw e
        }
    }

    suspend fun getAliveTerminalGroups(body: AliveTerminalGroupsRequest): AliveTerminalGroupsResponse {
        return try {
            val response = client.post("/api/1/terminal_groups/is_alive") {
                setBody(body)
            }
            response.body()
        } catch (e: Exception) {
            logError("getAliveTerminalGroups", e)
            throw e
        }
    }

    // Заказ
    suspend fun createDelivery(body: CreateDeliveryRequest): CreateDeliveryResponse {
        return try {
            val response = client.post("/api/1/deliveries/create") {
                setBody(body)
            }
            response.body()
        } catch (e: Exception) {
            logError("createDelivery", e)
            throw e
        }
    }

    suspend fun getOrdersStatusById(body: OderInfoRequest): OrdersInfoResponse {
        return try {
            val response = client.post("/api/1/deliveries/by_id") {
                setBody(body)
            }
            response.body()
        } catch (e: Exception) {
            logError("getOrdersStatusById", e)
            throw e
        }
    }

    suspend fun cancelOrderById(body: CancelOrderRequest): CancelOrderResponse {
        return try {
            val response = client.post("/api/1/deliveries/cancel") {
                setBody(body)
            }
            response.body()
        } catch (e: Exception) {
            logError("cancelOrderById", e)
            throw e
        }
    }

    // Типы оплаты
    suspend fun getPaymentTypes(body: PaymentTypesRequest): PaymentTypesResponse {
        return try {
            val response = client.post("/api/1/payment_types") {
                setBody(body)
            }
            response.body()
        } catch (e: Exception) {
            logError("getPaymentTypes", e)
            throw e
        }
    }

    // Скидки и лояльность
    suspend fun getDiscounts(body: DiscountsRequest): DiscountsResponse {
        return try {
            val response = client.post("/api/1/discounts") {
                setBody(body)
            }
            response.body()
        } catch (e: Exception) {
            logError("getDiscounts", e)
            throw e
        }
    }

    suspend fun getLoyaltyCustomerInfo(body: LoyaltyCustomerByPhoneRequest): LoyaltyCustomerResponse {
        return try {
            val response = client.post("/api/1/loyalty/iiko/customer/info") {
                setBody(body)
            }
            response.body()
        } catch (e: Exception) {
            logError("getLoyaltyCustomerInfo", e)
            throw e
        }
    }

    suspend fun getAllCustomerCategories(body: CustomerCategoriesRequest): CustomerCategoriesResponse {
        return try {
            val response = client.post("/api/1/loyalty/iiko/customer_category") {
                setBody(body)
            }
            response.body()
        } catch (e: Exception) {
            logError("getAllCustomerCategories", e)
            throw e
        }
    }

    private fun logError(methodName: String, e: Exception) {
        Napier.e("❌ $methodName - Ошибка: ${e.message}", e)
    }
}