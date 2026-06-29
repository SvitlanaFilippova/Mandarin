package com.mandarinkafe.mandarin.features.more.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class FeedbackRequest(
    val message: String,
    val needAnswer: Boolean,
)
