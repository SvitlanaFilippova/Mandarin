package com.mandarinkafe.mandarin.core.data.dto

import com.google.gson.annotations.SerializedName

data class AuthRequest(
    @SerializedName("apiLogin")
    val apiKey: String
)