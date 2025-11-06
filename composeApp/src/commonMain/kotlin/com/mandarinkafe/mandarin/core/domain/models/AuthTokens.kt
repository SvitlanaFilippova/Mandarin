package com.mandarinkafe.mandarin.core.domain.models

data class AuthTokens(
    val accessToken: String,
    val refreshToken: String,
    val tokenType: String
)