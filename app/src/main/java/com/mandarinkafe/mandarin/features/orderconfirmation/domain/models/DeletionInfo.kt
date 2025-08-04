package com.mandarinkafe.mandarin.features.orderconfirmation.domain.models

data class DeletionInfo(
    val isDeleted: Boolean = false,
    val comment: String? = null,
    val removalType: String? = null
)
