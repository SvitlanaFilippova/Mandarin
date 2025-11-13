package com.mandarinkafe.mandarin.core.data.network.impl

import com.mandarinkafe.mandarin.core.data.dto.CustomerCategoriesRequest
import com.mandarinkafe.mandarin.core.data.dto.OrganizationsRequest
import com.mandarinkafe.mandarin.core.data.dto.Response
import com.mandarinkafe.mandarin.core.data.network.IikoNetworkClient
import com.mandarinkafe.mandarin.core.data.network.NetworkMonitor
import com.mandarinkafe.mandarin.core.data.network.api.IikoApi
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
import io.github.aakira.napier.Napier

class IikoNetworkClientImpl(
    private val networkMonitor: NetworkMonitor,
    private val iikoApi: IikoApi,
) : IikoNetworkClient {

    private var organizationId: String = ""

    /** Загружаем organizationId один раз при первом обращении */
    private suspend fun ensureOrganizationId(): String {
        if (organizationId.isNotEmpty()) {
            return organizationId
        }
        return try {
            val response = iikoApi.getOrganizations(body = OrganizationsRequest())
            organizationId = response.organizations.firstOrNull()?.id
                ?: error("Не найдена организация")
            organizationId
        } catch (e: Exception) {
            Napier.e("Ошибка получения organizationId", e)
            throw e
        }
    }

    override suspend fun getLoyaltyCustomerInfo(phone: String): Response {
        if (!isConnected()) {
            return Response().apply { resultCode = NO_CONNECTION }
        }

        return try {
            val orgId = ensureOrganizationId()
            val response = iikoApi.getLoyaltyCustomerInfo(
                body = LoyaltyCustomerByPhoneRequest(
                    organizationId = orgId,
                    phone = phone
                )
            )
            response.apply { resultCode = HTTP_SUCCESS }
        } catch (e: Throwable) {
            Napier.e("Ошибка getLoyaltyCustomerInfo", e)
            Response().apply { resultCode = HTTP_SERVER_ERROR }
        }
    }

    override suspend fun getPaymentTypes(): Response {
        if (!isConnected()) {
            return Response().apply { resultCode = NO_CONNECTION }
        }
        return try {
            val orgId = ensureOrganizationId()
            val response = iikoApi.getPaymentTypes(body = PaymentTypesRequest(listOf(orgId)))
            response.apply { resultCode = HTTP_SUCCESS }
        } catch (e: Throwable) {
            Napier.e("Ошибка getPaymentTypes", e)
            Response().apply { resultCode = HTTP_SERVER_ERROR }
        }
    }


    override suspend fun createDelivery(order: OutgoingOrderDto): Response {
        if (!isConnected()) return Response().apply { resultCode = NO_CONNECTION }
        return try {
            val orgId = ensureOrganizationId()
            val request = CreateDeliveryRequest(order, orgId)

            val response = iikoApi.createDelivery(body = request)
            response.apply { resultCode = HTTP_SUCCESS }
        } catch (e: Throwable) {
            Napier.e("Ошибка createDelivery: ${e.message}", e)
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
            Napier.e("Ошибка getOrdersStatuses: ${e.message}", e)
            Response().apply { resultCode = HTTP_SERVER_ERROR }
        }
    }

    override suspend fun getAllCustomerCategories(): Response {
        if (!isConnected()) {
            return Response().apply { resultCode = NO_CONNECTION }
        }
        return try {
            val orgId = ensureOrganizationId()
            val response = iikoApi.getAllCustomerCategories(
                body = CustomerCategoriesRequest(orgId)
            )
            response.apply { resultCode = HTTP_SUCCESS }
        } catch (e: Throwable) {
            Napier.e("Ошибка getAllCustomerCategories: ${e.message}", e)
            Response().apply { resultCode = HTTP_SERVER_ERROR }
        }
    }

    override suspend fun getDiscounts(): Response {
        if (!isConnected()) {
            return Response().apply { resultCode = NO_CONNECTION }
        }
        return try {
            val orgId = ensureOrganizationId()
            val response = iikoApi.getDiscounts(
                body = DiscountsRequest(listOf(orgId))
            )
            response.apply { resultCode = HTTP_SUCCESS }
        } catch (e: Throwable) {
            Napier.e("Ошибка getDiscounts: ${e.message}", e)
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
            Napier.e("Ошибка cancelOrder: ${e.message}", e)
            Response().apply { resultCode = HTTP_SERVER_ERROR }
        }
    }

    override suspend fun getTerminalGroupsIds(): Response {
        if (!isConnected()) {
            return Response().apply { resultCode = NO_CONNECTION }
        }
        return try {
            val orgId = ensureOrganizationId()
            val response = iikoApi.getTerminalGroupsIds(
                body = TerminalGroupsIdsRequest(listOf(orgId))
            )
            response.apply { resultCode = HTTP_SUCCESS }
        } catch (e: Throwable) {
            Napier.e("Ошибка getTerminalGroupsIds: ${e.message}", e)
            Response().apply { resultCode = HTTP_SERVER_ERROR }
        }
    }

    override suspend fun getAliveTerminalGroups(terminalGroupIds: List<String>): Response {
        if (!isConnected()) {
            return Response().apply { resultCode = NO_CONNECTION }
        }
        return try {
            val orgId = ensureOrganizationId()
            val response = iikoApi.getAliveTerminalGroups(
                body = AliveTerminalGroupsRequest(listOf(orgId), terminalGroupIds)
            )
            response.apply { resultCode = HTTP_SUCCESS }
        } catch (e: Throwable) {
            Napier.e("Ошибка getAliveTerminalGroups: ${e.message}", e)
            Response().apply { resultCode = HTTP_SERVER_ERROR }
        }
    }

    private fun isConnected(): Boolean {
        val connected = networkMonitor.isNetworkAvailable()
        return connected
    }
}
