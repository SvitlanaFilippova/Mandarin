package com.mandarinkafe.mandarin.menu.data.network

data class MenuRequest(
    val externalMenuId: String,
    val organizationIds: List<String>,
)