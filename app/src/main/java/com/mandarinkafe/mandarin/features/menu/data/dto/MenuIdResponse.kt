package com.mandarinkafe.mandarin.features.menu.data.dto

import com.mandarinkafe.mandarin.core.data.dto.Response

data class MenuIdResponse(
    val correlationId: String,
    val externalMenus: List<Menu>,
) : Response()

data class Menu(
    val id: String,
    val name: String
)

