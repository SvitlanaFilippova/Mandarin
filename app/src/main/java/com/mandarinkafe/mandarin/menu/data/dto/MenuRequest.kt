package com.mandarinkafe.mandarin.menu.data.dto

data class MenuRequest(
    val externalMenuId: String,
    val organizationIds: List<String>,
)