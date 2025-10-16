package com.mandarinkafe.mandarin.features.orderinfo.data.network.dto

import kotlinx.serialization.Serializable

@Serializable
data class ProblemDto(
    val description: String? = null,
    val hasProblem: Boolean? = null,
)





