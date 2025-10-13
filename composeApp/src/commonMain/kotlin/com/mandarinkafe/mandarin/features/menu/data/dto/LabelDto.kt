package com.mandarinkafe.mandarin.features.menu.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class LabelDto(
    val code: String,
    val name: String
)
