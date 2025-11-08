package com.mandarinkafe.mandarin.features.ordershistory.data.network

import com.mandarinkafe.mandarin.features.ordershistory.data.network.dto.SavedOrderDto
import kotlinx.serialization.Serializable

@Serializable
data class OrdersHistoryUpdateRequest(val data: SavedOrderDto)

