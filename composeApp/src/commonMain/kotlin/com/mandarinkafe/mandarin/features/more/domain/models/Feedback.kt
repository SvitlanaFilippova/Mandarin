package com.mandarinkafe.mandarin.features.more.domain.models

data class Feedback(
    val name: String,
    val phone: String,
    val email: String,
    val message: String,
    val needAnswer: Boolean,
)