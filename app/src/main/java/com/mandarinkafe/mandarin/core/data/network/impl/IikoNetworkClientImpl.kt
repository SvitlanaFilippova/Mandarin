package com.mandarinkafe.mandarin.core.data.network.impl

import android.util.Log
import com.mandarinkafe.mandarin.BuildConfig
import com.mandarinkafe.mandarin.core.data.dto.AuthRequest
import com.mandarinkafe.mandarin.core.data.dto.CustomerCategoriesRequest
import com.mandarinkafe.mandarin.core.data.dto.OrganizationsRequest
import com.mandarinkafe.mandarin.core.data.dto.Response
import com.mandarinkafe.mandarin.core.data.network.IikoNetworkClient
import com.mandarinkafe.mandarin.core.data.network.api.IikoAuthApi
import com.mandarinkafe.mandarin.core.data.network.api.IikoDiscountApi
import com.mandarinkafe.mandarin.core.data.network.api.IikoMenuApi
import com.mandarinkafe.mandarin.core.data.network.api.IikoOrderApi
import com.mandarinkafe.mandarin.core.data.network.api.IikoTerminalApi
import com.mandarinkafe.mandarin.features.infrastructure.data.network.AliveTerminalGroupsRequest
import com.mandarinkafe.mandarin.features.infrastructure.data.network.DiscountsRequest
import com.mandarinkafe.mandarin.features.infrastructure.data.network.LoyaltyCustomerByPhoneRequest
import com.mandarinkafe.mandarin.features.infrastructure.data.network.TerminalGroupsIdsRequest
import com.mandarinkafe.mandarin.features.infrastructure.data.network.dto.paymenttype.PaymentTypesRequest
import com.mandarinkafe.mandarin.features.menu.data.network.MenuRequest
import com.mandarinkafe.mandarin.features.order.data.network.CreateDeliveryRequest
import com.mandarinkafe.mandarin.features.order.data.network.dto.OutgoingOrderDto
import com.mandarinkafe.mandarin.features.orderinfo.data.network.CancelOrderRequest
import com.mandarinkafe.mandarin.features.orderinfo.data.network.OderInfoRequest
import com.mandarinkafe.mandarin.util.Constants.HTTP_SERVER_ERROR
import com.mandarinkafe.mandarin.util.Constants.HTTP_SUCCESS
import com.mandarinkafe.mandarin.util.Constants.NO_CONNECTION
import com.mandarinkafe.mandarin.util.NetworkMonitor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class IikoNetworkClientImpl(
    private val networkMonitor: NetworkMonitor,
    private val authApi: IikoAuthApi,
    private val menuApi: IikoMenuApi,
    private val orderApi: IikoOrderApi,
    private val terminalApi: IikoTerminalApi,
    private val discountApi: IikoDiscountApi
) : IikoNetworkClient {
    private var token = ""
    private var organizationId = ""
    private var externalMenuId = ""

    private val logTag = "DEBUG ORDER API NetworkClient"

    private suspend fun authenticate() {
        if (token.isNotEmpty() && organizationId.isNotEmpty()) {
            // Уже авторизованы
            return
        }
        try {
            val authResponse = authApi.authenticate(AuthRequest(BuildConfig.IIKO_API_KEY))
            token = BEARER_PREFIX + authResponse.token

            val organizationsResponse = authApi.getOrganizations(
                token = token,
                body = OrganizationsRequest()
            )
            organizationId = organizationsResponse.organizations.firstOrNull()?.id
                ?: error("No organization found")
        } catch (e: Throwable) {
            Log.d(logTag, "Ошибка в методе authenticate: ${e.message}")
        }
    }

    private suspend fun ensureAuthenticated() {
        if (token.isEmpty() || organizationId.isEmpty()) authenticate()
    }

    private suspend fun getExternalMenuId(): String {
        val menuIdResponse = menuApi.getMenuId(token)
        return menuIdResponse.externalMenus.firstOrNull()?.id
            ?: error("Menu ID not found")
    }

    override suspend fun getMenu(): Response {
        if (!isConnected()) {
            return Response().apply { resultCode = NO_CONNECTION }
        }
        logRequest("getMenu", null)
        return withContext(Dispatchers.IO) {
            ensureAuthenticated()
            fetchMenu()
        }
    }

    private suspend fun fetchMenu(): Response {
        Log.d(logTag, "Запуск fetchMenu")

        return try {
            if (externalMenuId.isEmpty()) {
                Log.d(logTag, "externalMenuId пустой, начинаем загрузку ID")
                externalMenuId = getExternalMenuId()
                Log.d(logTag, "externalMenuId получен: $externalMenuId")
            }

            Log.d(logTag, "Отправка запроса на получение меню")
            val menuResponse = menuApi.getMenuById(
                token = token,
                body = MenuRequest(
                    externalMenuId = externalMenuId,
                    organizationIds = listOf(organizationId)
                )
            )

            Log.d(logTag, "Меню успешно получено.")
            menuResponse.apply { resultCode = HTTP_SUCCESS }

        } catch (e: Throwable) {
            Log.e(logTag, "Ошибка при получении меню: ${e.message}", e)
            Response().apply { resultCode = HTTP_SERVER_ERROR }
        }
    }

    override suspend fun getLoyaltyCustomerInfo(phone: String): Response {
        if (!isConnected()) {
            return Response().apply { resultCode = NO_CONNECTION }
        }
        return withContext(Dispatchers.IO) {
            ensureAuthenticated()
            fetchLoyaltyCustomerInfo(phone)
        }
    }

    override suspend fun getPaymentTypes(): Response {
        return try {
            if (!isConnected()) {
                return Response().apply { resultCode = NO_CONNECTION }
            }
            val response = orderApi.getPaymentTypes(
                token = token,
                body = PaymentTypesRequest(
                    organizationIds = listOf(organizationId)
                )
            )
            response.apply { resultCode = HTTP_SUCCESS }
        } catch (e: Throwable) {
            Log.d(logTag, ERROR + e.message)
            Response().apply { resultCode = HTTP_SERVER_ERROR }
        }
    }

    override suspend fun createDelivery(order: OutgoingOrderDto): Response {
        return try {
            if (!isConnected()) {
                return Response().apply { resultCode = NO_CONNECTION }
            }

            val request = CreateDeliveryRequest(
                order = order,
                organizationId = organizationId
            )
            Log.d(logTag, "Prepared request: $request")

            val response = orderApi.createDelivery(
                token = token,
                body = request
            )
            Log.d(logTag, "Received response: $response")

            response.apply {
                resultCode = HTTP_SUCCESS
            }
        } catch (e: Throwable) {
            Log.e(logTag, "Error in createDelivery: ${e.message}", e)
            Response().apply {
                resultCode = HTTP_SERVER_ERROR
            }
        }
    }

    private suspend fun fetchLoyaltyCustomerInfo(phone: String): Response {
        return try {
            val request = LoyaltyCustomerByPhoneRequest(
                organizationId = organizationId,
                phone = phone
            )
            val response = discountApi.getLoyaltyCustomerInfo(
                token = token,
                body = request
            )
            response.apply { resultCode = HTTP_SUCCESS }
        } catch (e: Throwable) {
            Log.d(logTag, ERROR + e.message)
            Response().apply { resultCode = HTTP_SERVER_ERROR }
        }
    }

    override suspend fun getSingleOrderInfoById(id: String): Response {
        if (!isConnected()) {
            return Response().apply { resultCode = NO_CONNECTION }
        }
        return fetchOrderStatuses(listOf(id))
    }

    override suspend fun getOrdersStatusesByIds(ids: List<String>): Response {
        if (!isConnected()) {
            return Response().apply { resultCode = NO_CONNECTION }
        }
        return fetchOrderStatuses(ids)
    }

    private suspend fun fetchOrderStatuses(ids: List<String>): Response {
        return try {
            if (!isConnected()) {
                return Response().apply { resultCode = NO_CONNECTION }
            }
            val request = OderInfoRequest(
                organizationId = organizationId,
                orderIds = ids
            )

            val response = orderApi.getOrdersStatusById(
                token = token,
                body = request
            )
            response.apply {
                resultCode = HTTP_SUCCESS
            }
        } catch (e: Throwable) {
            Log.e(logTag, "Error in getOrderStatusById: ${e.message}", e)
            Response().apply {
                resultCode = HTTP_SERVER_ERROR
                Log.d(logTag, "Created error response with code $HTTP_SERVER_ERROR")
            }
        }
    }

    override suspend fun getAllCustomerCategories(): Response {
        logRequest("getAllCustomerCategories", null)
        return try {
            if (!isConnected()) {
                return Response().apply { resultCode = NO_CONNECTION }
            }
            val response = discountApi.getAllCustomerCategories(
                token = token,
                body = CustomerCategoriesRequest(
                    organizationId = organizationId
                )
            )
            response.apply { resultCode = HTTP_SUCCESS }
        } catch (e: Throwable) {
            Log.d(logTag, ERROR + e.message)
            Response().apply { resultCode = HTTP_SERVER_ERROR }
        }
    }

    override suspend fun getDiscounts(): Response {
        logRequest("getDiscounts", null)
        return try {
            if (!isConnected()) {
                return Response().apply { resultCode = NO_CONNECTION }
            }
            val response = discountApi.getDiscounts(
                token = token,
                body = DiscountsRequest(
                    organizationIds = listOf(organizationId)
                )
            )
            response.apply { resultCode = HTTP_SUCCESS }
        } catch (e: Throwable) {
            Log.d(logTag, ERROR + e.message)
            Response().apply { resultCode = HTTP_SERVER_ERROR }
        }
    }

    override suspend fun cancelOrder(id: String): Response {
        logRequest("cancelOrder", id)
        return try {
            if (!isConnected()) {
                return Response().apply { resultCode = NO_CONNECTION }
            }
            val response = orderApi.cancelOrderById(
                token = token,
                body = CancelOrderRequest(
                    organizationId = organizationId,
                    orderId = id
                )
            )
            response.apply { resultCode = HTTP_SUCCESS }
        } catch (e: Throwable) {
            Log.d(logTag, ERROR + e.message)
            Response().apply { resultCode = HTTP_SERVER_ERROR }
        }
    }

    override suspend fun getTerminalGroupsIds(): Response {
        logRequest("getTerminalGroupsIds", null)
        return try {
            if (!isConnected()) {
                return Response().apply { resultCode = NO_CONNECTION }
            }
            val response = terminalApi.getTerminalGroupsIds(
                token = token,
                body = TerminalGroupsIdsRequest(
                    organizationIds = listOf(organizationId)
                )
            )
            response.apply { resultCode = HTTP_SUCCESS }
        } catch (e: Throwable) {
            Log.d(logTag, ERROR + e.message)
            Response().apply { resultCode = HTTP_SERVER_ERROR }
        }
    }

    override suspend fun getAliveTerminalGroups(terminalGroupIds: List<String>): Response {
        return try {
            if (!isConnected()) {
                return Response().apply { resultCode = NO_CONNECTION }
            }
            val response = terminalApi.getAliveTerminalGroups(
                token = token,
                body = AliveTerminalGroupsRequest(
                    organizationIds = listOf(organizationId),
                    terminalGroupIds = terminalGroupIds,
                )
            )
            response.apply { resultCode = HTTP_SUCCESS }
        } catch (e: Throwable) {
            Log.d(logTag, ERROR + e.message)
            Response().apply { resultCode = HTTP_SERVER_ERROR }
        }
    }

    private fun logRequest(method: String, params: Any?) {
        val timestamp = System.currentTimeMillis()
        Log.d(logTag, "➡️ [$timestamp] $method params=$params")
    }

    private fun isConnected(): Boolean {
        val isConnected = networkMonitor.isNetworkAvailable()
        Log.d(logTag, "isConnected: $isConnected ")
        return isConnected
    }

    private companion object {
        const val BEARER_PREFIX = "Bearer "
        const val ERROR = "Ошибка: "
    }
}