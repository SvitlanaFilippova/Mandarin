package com.mandarinkafe.mandarin.menu.data.dto

data class MenuIdResponse(
    val correlationId: String,
    val externalMenus: List<Menu>,
    ): Response()

data class Menu(
    val id: String,
    val name: String
)

