package com.mandarinkafe.mandarin.features.orderconfirmation.data.network.dto

data class DeletionMethodDto(
    val id: String,
    val comment: String?,
    val removalType: RemovalTypeDto?
)