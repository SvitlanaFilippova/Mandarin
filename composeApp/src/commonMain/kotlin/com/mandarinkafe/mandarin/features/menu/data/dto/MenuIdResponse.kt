package com.mandarinkafe.mandarin.features.menu.data.dto

import com.mandarinkafe.mandarin.core.data.dto.Response
import kotlinx.serialization.Serializable

@Serializable
data class MenuIdResponse(
    val correlationId: String,
    val externalMenus: List<Menu>,
) : Response()

@Serializable
data class Menu(
    val id: String,
    val name: String,
)





