package com.mandarinkafe.mandarin.core.data.network.impl

import android.util.Log
import com.mandarinkafe.mandarin.core.data.dto.CustomerCategoriesRequest
import com.mandarinkafe.mandarin.core.data.dto.OrganizationsRequest
import com.mandarinkafe.mandarin.core.data.dto.Response
import com.mandarinkafe.mandarin.core.data.network.IikoNetworkClient
import com.mandarinkafe.mandarin.core.data.network.api.IikoApi
import com.mandarinkafe.mandarin.core.data.network.api.ServerApi
import com.mandarinkafe.mandarin.features.infrastructure.data.network.AliveTerminalGroupsRequest
import com.mandarinkafe.mandarin.features.infrastructure.data.network.DiscountsRequest
import com.mandarinkafe.mandarin.features.infrastructure.data.network.LoyaltyCustomerByPhoneRequest
import com.mandarinkafe.mandarin.features.infrastructure.data.network.TerminalGroupsIdsRequest
import com.mandarinkafe.mandarin.features.infrastructure.data.network.dto.paymenttype.PaymentTypesRequest
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
    private val iikoApi: IikoApi,
    private val menuApi: ServerApi,
) : IikoNetworkClient {

    private var organizationId: String = ""

    private val logTag = "NetworkClientDebug"

    /** Загружаем organizationId один раз при первом обращении */
    private suspend fun ensureOrganizationId(): String {
        if (organizationId.isNotEmpty()) {
            Log.d(logTag, "✅ Используем существующий organizationId: ${organizationId.take(10)}...")
            return organizationId
        }

        Log.d(logTag, "🔄 Запрашиваем organizationId...")
        return try {
            val response = iikoApi.getOrganizations(body = OrganizationsRequest())
            organizationId = response.organizations.firstOrNull()?.id
                ?: error("Не найдена организация")
            Log.d(logTag, "✅ Получен organizationId: ${organizationId.take(10)}...")
            organizationId
        } catch (e: Exception) {
            Log.e(logTag, "❌ Ошибка получения organizationId", e)
            throw e
        }
    }


    override suspend fun getMenu(): Response {
        if (!isConnected()) return Response().apply { resultCode = NO_CONNECTION }
        return withContext(Dispatchers.IO) {
            try {
                val menuResponse = menuApi.getMenu()
                organizationId = menuResponse.menu.intervals?.firstOrNull()?.organizationId ?: ""

                menuResponse.apply { resultCode = HTTP_SUCCESS }
            } catch (e: Throwable) {
                Log.e(logTag, "Ошибка при получении меню: ${e.message}", e)
                Response().apply { resultCode = HTTP_SERVER_ERROR }
            }
        }
    }

    override suspend fun getLoyaltyCustomerInfo(phone: String): Response {
        Log.d(logTag, "🔄 getLoyaltyCustomerInfo(phone: ${phone.take(5)}...)")
        if (!isConnected()) {
            Log.w(logTag, "📵 Нет соединения в getLoyaltyCustomerInfo")
            return Response().apply { resultCode = NO_CONNECTION }
        }

        return try {
            val orgId = ensureOrganizationId()
            Log.d(logTag, "📤 Запрос информации о клиенте...")
            val response = iikoApi.getLoyaltyCustomerInfo(
                body = LoyaltyCustomerByPhoneRequest(
                    organizationId = orgId,
                    phone = phone
                )
            )
            Log.d(logTag, "✅ Информация о клиенте получена")
            response.apply { resultCode = HTTP_SUCCESS }
        } catch (e: Throwable) {
            Log.e(logTag, "❌ Ошибка getLoyaltyCustomerInfo", e)
            Response().apply { resultCode = HTTP_SERVER_ERROR }
        }
    }

    override suspend fun getPaymentTypes(): Response {
        Log.d(logTag, "🔄 getPaymentTypes()")
        if (!isConnected()) {
            Log.w(logTag, "📵 Нет соединения в getPaymentTypes")
            return Response().apply { resultCode = NO_CONNECTION }
        }

        return try {
            val orgId = ensureOrganizationId()
            Log.d(logTag, "📤 Запрос типов оплаты...")
            val response = iikoApi.getPaymentTypes(body = PaymentTypesRequest(listOf(orgId)))
            Log.d(logTag, "✅ Типы оплаты получены")
            response.apply { resultCode = HTTP_SUCCESS }
        } catch (e: Throwable) {
            Log.e(logTag, "❌ Ошибка getPaymentTypes", e)
            Response().apply { resultCode = HTTP_SERVER_ERROR }
        }
    }

    override suspend fun createDelivery(order: OutgoingOrderDto): Response {
        if (!isConnected()) return Response().apply { resultCode = NO_CONNECTION }
        return try {
            val orgId = ensureOrganizationId()
            val response = iikoApi.createDelivery(
                body = CreateDeliveryRequest(order, orgId)
            )
            response.apply { resultCode = HTTP_SUCCESS }
        } catch (e: Throwable) {
            Log.e(logTag, "Ошибка createDelivery: ${e.message}", e)
            Response().apply { resultCode = HTTP_SERVER_ERROR }
        }
    }

    override suspend fun getSingleOrderInfoById(id: String): Response =
        getOrdersStatusesByIds(listOf(id))

    override suspend fun getOrdersStatusesByIds(ids: List<String>): Response {
        if (!isConnected()) return Response().apply { resultCode = NO_CONNECTION }
        return try {
            val orgId = ensureOrganizationId()
            val response = iikoApi.getOrdersStatusById(
                body = OderInfoRequest(orgId, ids)
            )
            response.apply { resultCode = HTTP_SUCCESS }
        } catch (e: Throwable) {
            Log.e(logTag, "Ошибка getOrdersStatuses: ${e.message}", e)
            Response().apply { resultCode = HTTP_SERVER_ERROR }
        }
    }

    override suspend fun getAllCustomerCategories(): Response {
        if (!isConnected()) return Response().apply { resultCode = NO_CONNECTION }
        return try {
            val orgId = ensureOrganizationId()
            val response = iikoApi.getAllCustomerCategories(
                body = CustomerCategoriesRequest(orgId)
            )
            response.apply { resultCode = HTTP_SUCCESS }
        } catch (e: Throwable) {
            Log.e(logTag, "Ошибка getAllCustomerCategories: ${e.message}", e)
            Response().apply { resultCode = HTTP_SERVER_ERROR }
        }
    }

    override suspend fun getDiscounts(): Response {
        if (!isConnected()) return Response().apply { resultCode = NO_CONNECTION }
        return try {
            val orgId = ensureOrganizationId()
            val response = iikoApi.getDiscounts(
                body = DiscountsRequest(listOf(orgId))
            )
            response.apply { resultCode = HTTP_SUCCESS }
        } catch (e: Throwable) {
            Log.e(logTag, "Ошибка getDiscounts: ${e.message}", e)
            Response().apply { resultCode = HTTP_SERVER_ERROR }
        }
    }

    override suspend fun cancelOrder(id: String): Response {
        if (!isConnected()) return Response().apply { resultCode = NO_CONNECTION }
        return try {
            val orgId = ensureOrganizationId()
            val response = iikoApi.cancelOrderById(
                body = CancelOrderRequest(orgId, id)
            )
            response.apply { resultCode = HTTP_SUCCESS }
        } catch (e: Throwable) {
            Log.e(logTag, "Ошибка cancelOrder: ${e.message}", e)
            Response().apply { resultCode = HTTP_SERVER_ERROR }
        }
    }

    override suspend fun getTerminalGroupsIds(): Response {
        if (!isConnected()) return Response().apply { resultCode = NO_CONNECTION }
        return try {
            val orgId = ensureOrganizationId()
            val response = iikoApi.getTerminalGroupsIds(
                body = TerminalGroupsIdsRequest(listOf(orgId))
            )
            response.apply { resultCode = HTTP_SUCCESS }
        } catch (e: Throwable) {
            Log.e(logTag, "Ошибка getTerminalGroupsIds: ${e.message}", e)
            Response().apply { resultCode = HTTP_SERVER_ERROR }
        }
    }

    override suspend fun getAliveTerminalGroups(terminalGroupIds: List<String>): Response {
        if (!isConnected()) return Response().apply { resultCode = NO_CONNECTION }
        return try {
            val orgId = ensureOrganizationId()
            val response = iikoApi.getAliveTerminalGroups(
                body = AliveTerminalGroupsRequest(listOf(orgId), terminalGroupIds)
            )
            response.apply { resultCode = HTTP_SUCCESS }
        } catch (e: Throwable) {
            Log.e(logTag, "Ошибка getAliveTerminalGroups: ${e.message}", e)
            Response().apply { resultCode = HTTP_SERVER_ERROR }
        }
    }

    private fun isConnected(): Boolean {
        val connected = networkMonitor.isNetworkAvailable()
        Log.d(logTag, "📶 Проверка сети: $connected")
        return connected
    }
}