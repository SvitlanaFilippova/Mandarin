package com.mandarinkafe.mandarin.features.menu.data.impl

import com.mandarinkafe.mandarin.core.data.network.ServerNetworkClient
import com.mandarinkafe.mandarin.features.menu.data.dto.OrderAcceptStatusDto
import com.mandarinkafe.mandarin.features.menu.data.dto.OrderAcceptStatusHttpResponse
import com.mandarinkafe.mandarin.features.menu.domain.api.OrderAcceptStatusRepository
import com.mandarinkafe.mandarin.features.menu.domain.models.OrderAcceptStatusSnapshot
import com.mandarinkafe.mandarin.util.Constants.HTTP_SUCCESS
import com.mandarinkafe.mandarin.util.Constants.MENU_REMOTE_CACHE_STALE_INTERVAL_MS
import com.mandarinkafe.mandarin.util.Constants.NO_CONNECTION
import com.mandarinkafe.mandarin.util.Resource
import io.github.aakira.napier.Napier
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class OrderAcceptStatusRepositoryImpl(
    private val networkClient: ServerNetworkClient,
) : OrderAcceptStatusRepository {

    private var cache: OrderAcceptStatusSnapshot? = null
    private var lastRefreshTime: Long = 0

    @OptIn(ExperimentalTime::class)
    override suspend fun loadOrderAcceptStatus(): Resource<Unit> {
        val response = try {
            networkClient.getOrderAcceptStatus()
        } catch (e: Exception) {
            Napier.e("OrderAcceptStatusRepository: loadOrderAcceptStatus", e)
            applyFailOpenCache()
            return Resource.Success(Unit)
        }

        if (response.resultCode == NO_CONNECTION) {
            applyFailOpenCache()
            return Resource.Success(Unit)
        }

        if (response.resultCode != HTTP_SUCCESS) {
            applyFailOpenCache()
            return Resource.Success(Unit)
        }

        val payload = (response as? OrderAcceptStatusHttpResponse)?.payload
        if (payload == null) {
            applyFailOpenCache()
            return Resource.Success(Unit)
        }

        cache = snapshotFromDto(payload)
        lastRefreshTime = Clock.System.now().toEpochMilliseconds()
        return Resource.Success(Unit)
    }

    @OptIn(ExperimentalTime::class)
    override suspend fun loadOrderAcceptStatusIfStale(): Resource<Unit> {
        val now = Clock.System.now().toEpochMilliseconds()
        if (now - lastRefreshTime > MENU_REMOTE_CACHE_STALE_INTERVAL_MS) {
            return loadOrderAcceptStatus()
        }
        return Resource.Success(Unit)
    }

    override suspend fun getOrderAcceptStatus(): Resource<OrderAcceptStatusSnapshot> {
        if (cache == null) {
            loadOrderAcceptStatus()
        }
        return Resource.Success(cache ?: OrderAcceptStatusSnapshot.accepting())
    }

    @OptIn(ExperimentalTime::class)
    override suspend fun fetchOrderAcceptStatusFresh(): OrderAcceptStatusSnapshot {
        val response = try {
            networkClient.getOrderAcceptStatus()
        } catch (e: Exception) {
            Napier.e("OrderAcceptStatusRepository: fetchOrderAcceptStatusFresh", e)
            return OrderAcceptStatusSnapshot.accepting()
        }

        if (response.resultCode == NO_CONNECTION) {
            return OrderAcceptStatusSnapshot.accepting()
        }

        if (response.resultCode != HTTP_SUCCESS) {
            return OrderAcceptStatusSnapshot.accepting()
        }

        val payload = (response as? OrderAcceptStatusHttpResponse)?.payload
        if (payload == null) {
            return OrderAcceptStatusSnapshot.accepting()
        }

        val snapshot = snapshotFromDto(payload)
        cache = snapshot
        lastRefreshTime = Clock.System.now().toEpochMilliseconds()
        return snapshot
    }

    @OptIn(ExperimentalTime::class)
    private fun applyFailOpenCache() {
        cache = OrderAcceptStatusSnapshot.accepting()
        lastRefreshTime = Clock.System.now().toEpochMilliseconds()
    }

    private fun snapshotFromDto(dto: OrderAcceptStatusDto): OrderAcceptStatusSnapshot {
        val closingTrimmed = dto.closingTime?.trim()?.takeIf { it.isNotBlank() }
        val endAcceptTrimmed = dto.orderAcceptanceEndTime?.trim()?.takeIf { it.isNotBlank() }
        val isClosedForWholeDay =
            !dto.isAcceptingOrders &&
                    dto.closingTime == null &&
                    dto.orderAcceptanceEndTime == null
        val serverTrimmed = dto.serverTime.trim().takeIf { it.isNotBlank() }
        return OrderAcceptStatusSnapshot(
            isAcceptingOrders = dto.isAcceptingOrders,
            closingTime = closingTrimmed,
            orderAcceptanceEndTime = endAcceptTrimmed,
            serverTime = serverTrimmed,
            isClosedForWholeDay = isClosedForWholeDay,
        )
    }
}

