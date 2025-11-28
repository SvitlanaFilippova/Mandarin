package com.mandarinkafe.mandarin.features.ordershistory.data.network

import com.mandarinkafe.mandarin.core.data.dto.Response
import com.mandarinkafe.mandarin.features.ordershistory.data.network.dto.SavedOrderDto
import kotlinx.serialization.Serializable

@Serializable
data class OrdersHistoryResponse(val data: List<SavedOrderDto>? = null) : Response()

@Serializable
data class OrderHistoryItemResponse(val data: SavedOrderDto? = null) : Response()

