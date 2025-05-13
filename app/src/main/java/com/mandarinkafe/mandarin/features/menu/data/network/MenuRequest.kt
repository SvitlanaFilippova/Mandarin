package com.mandarinkafe.mandarin.features.menu.data.network

data class MenuRequest(
    val externalMenuId: String,
    val organizationIds: List<String>,
)