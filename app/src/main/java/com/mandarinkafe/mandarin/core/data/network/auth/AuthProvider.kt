package com.mandarinkafe.mandarin.core.data.network.auth

interface AuthProvider {
    suspend fun getToken(): String
    suspend fun refreshToken(): String
}