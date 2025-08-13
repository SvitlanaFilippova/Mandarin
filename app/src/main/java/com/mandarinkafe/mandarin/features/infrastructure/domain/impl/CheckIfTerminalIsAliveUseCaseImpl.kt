package com.mandarinkafe.mandarin.features.infrastructure.domain.impl

import com.mandarinkafe.mandarin.features.infrastructure.domain.api.AliveTerminalRepository
import com.mandarinkafe.mandarin.features.infrastructure.domain.api.CheckIfTerminalIsAliveUseCase
import com.mandarinkafe.mandarin.util.Resource

class CheckIfTerminalIsAliveUseCaseImpl(private val repository: AliveTerminalRepository) :
    CheckIfTerminalIsAliveUseCase {

    private var cachedStatus: Boolean? = null
    private var lastCacheTime: Long = INITIAL_CACHE_TIME

    override suspend fun invoke(): Resource<Boolean> {
        val now = System.currentTimeMillis()
        val useCache = cachedStatus != null && (now - lastCacheTime) < CACHE_TTL_MS

        return if (useCache) {
            Resource.Success(cachedStatus!!)
        } else {
            val result = repository.checkAliveTerminals()
            if (result is Resource.Success) {
                cachedStatus = result.data
                lastCacheTime = now
            }
            result
        }
    }

    companion object {
        private const val CACHE_TTL_MS = 60 * 1000L // 1 минута
        private const val INITIAL_CACHE_TIME = 0L
    }

}
