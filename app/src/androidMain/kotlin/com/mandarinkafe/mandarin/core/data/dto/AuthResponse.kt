package com.mandarinkafe.mandarin.core.data.dto

import kotlinx.serialization.Serializable

@Serializable
class AuthResponse(val token: String?) : Response()