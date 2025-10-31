package com.mandarinkafe.mandarin.features.infrastructure.domain.api

import com.mandarinkafe.mandarin.util.Resource

interface AliveTerminalRepository {
    suspend fun checkAliveTerminals(): Resource<Boolean>
}