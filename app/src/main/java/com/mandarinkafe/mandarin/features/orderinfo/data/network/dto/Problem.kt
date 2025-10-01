package com.mandarinkafe.mandarin.features.orderinfo.data.network.dto

import kotlinx.serialization.Serializable

@Serializable
data class Problem(
    val description: String,
    val hasProblem: Boolean
)