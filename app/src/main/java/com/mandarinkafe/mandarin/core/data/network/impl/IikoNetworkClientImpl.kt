package com.mandarinkafe.mandarin.core.data.network.impl

import android.util.Log
import com.mandarinkafe.mandarin.BuildConfig
import com.mandarinkafe.mandarin.core.data.dto.AuthRequest
import com.mandarinkafe.mandarin.core.data.dto.OrganizationsRequest
import com.mandarinkafe.mandarin.core.data.dto.Response
import com.mandarinkafe.mandarin.core.data.network.IikoApiService
import com.mandarinkafe.mandarin.core.data.network.IikoNetworkClient
import com.mandarinkafe.mandarin.features.menu.data.network.MenuRequest
import com.mandarinkafe.mandarin.features.order.data.network.CreateDeliveryRequest
import com.mandarinkafe.mandarin.features.order.data.network.LoyaltyCustomerByPhoneRequest
import com.mandarinkafe.mandarin.features.order.data.network.dto.OutgoingOrderDto
import com.mandarinkafe.mandarin.features.order.data.network.dto.paymenttype.PaymentTypesRequest
import com.mandarinkafe.mandarin.features.orderinfo.data.network.OderInfoRequest
import com.mandarinkafe.mandarin.util.Constants.HTTP_SERVER_ERROR
import com.mandarinkafe.mandarin.util.Constants.HTTP_SUCCESS
import com.mandarinkafe.mandarin.util.Constants.NO_CONNECTION
import com.mandarinkafe.mandarin.util.NetworkMonitor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class IikoNetworkClientImpl(
    private val networkMonitor: NetworkMonitor,
    private val iikoService: IikoApiService,
) : IikoNetworkClient {
    private val logTag = "IIKO NetworkClient"
    private var token = ""
    private var organizationId = ""
    private var externalMenuId = ""

    private suspend fun authenticate() {
        if (token.isNotEmpty() && organizationId.isNotEmpty()) {
            // Уже авторизованы
            return
        }
        try {
            val authResponse = iikoService.authenticate(AuthRequest(BuildConfig.IIKO_API_KEY))
            token = BEARER_PREFIX + authResponse.token

            val organizationsResponse = iikoService.getOrganizations(
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
        val menuIdResponse = iikoService.getMenuId(token)
        return menuIdResponse.externalMenus.firstOrNull()?.id
            ?: error("Menu ID not found")
    }

    override suspend fun getMenu(): Response {
        if (!isConnected()) {
            return Response().apply { resultCode = NO_CONNECTION }
        }
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
            val menuResponse = iikoService.getMenuById(
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
            val response = iikoService.getPaymentTypes(
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

    private val logTagORDER = "DEBUG ORDER NetworkClient"

    override suspend fun createDelivery(order: OutgoingOrderDto): Response {
        return try {
            val request = CreateDeliveryRequest(
                order = order,
                organizationId = organizationId
            )

            val response = iikoService.createDelivery(
                token = token,
                body = request
            )
            Log.d(logTagORDER, "Received response: $response")

            response.apply {
                resultCode = HTTP_SUCCESS
                Log.d(logTagORDER, "Modified response code to HTTP_SUCCESS")
            }
        } catch (e: Throwable) {
            Log.e(logTagORDER, "Error in createDelivery: ${e.message}", e)
            Response().apply {
                resultCode = HTTP_SERVER_ERROR
                Log.d(logTagORDER, "Created error response with code $HTTP_SERVER_ERROR")
            }
        }
    }

    private suspend fun fetchLoyaltyCustomerInfo(phone: String): Response {
        return try {
            val request = LoyaltyCustomerByPhoneRequest(
                organizationId = organizationId,
                phone = phone
            )
            val response = iikoService.getLoyaltyCustomerInfo(
                token = token,
                body = request
            )
            response.apply { resultCode = HTTP_SUCCESS }
        } catch (e: Throwable) {
            Log.d(logTag, ERROR + e.message)
            Response().apply { resultCode = HTTP_SERVER_ERROR }
        }
    }


    override suspend fun getOrderStatusById(id: String): Response {
        return try {
            val request = OderInfoRequest(
                organizationId = organizationId,
                orderIds = listOf(id)
            )

            val response = iikoService.getOrdersStatusById(
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

    private fun isConnected(): Boolean {
        return networkMonitor.isNetworkAvailable()
    }

    private companion object {
        const val BEARER_PREFIX = "Bearer "
        const val ERROR = "Ошибка: "
    }
}