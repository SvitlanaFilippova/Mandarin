package com.mandarinkafe.mandarin.features.infrastructure.domain.impl

import com.mandarinkafe.mandarin.features.infrastructure.domain.api.AliveTerminalRepository
import com.mandarinkafe.mandarin.features.infrastructure.domain.api.CheckIfTerminalIsAliveUseCase
import com.mandarinkafe.mandarin.util.Resource
import com.mandarinkafe.mandarin.util.getCurrentTimeMillis
import kotlinx.coroutines.delay

class CheckIfTerminalIsAliveUseCaseImpl(private val repository: AliveTerminalRepository) :
    CheckIfTerminalIsAliveUseCase {

    private var cachedStatus: Boolean? = null
    private var lastCacheTime: Long = INITIAL_CACHE_TIME

    override suspend fun invoke(): Resource<Boolean> {
        val now = getCurrentTimeMillis()
        val useCache = cachedStatus != null && now - lastCacheTime < CACHE_TTL_MS

        return if (useCache) {
            Resource.Success(cachedStatus!!)
        } else {
            val result = fetchAliveStatusWithSingleRetry()
            if (result is Resource.Success) {
                cachedStatus = result.data
                lastCacheTime = now
            }
            result
        }
    }

    /**
     * Повторяем один раз при сбоях сети/сервера; без сети повтор бессмысленен.
     */
    private suspend fun fetchAliveStatusWithSingleRetry(): Resource<Boolean> {
        val first = repository.checkAliveTerminals()
        if (first is Resource.Success || first is Resource.ErrorNoInternet) {
            return first
        }
        delay(RETRY_DELAY_MS)
        return repository.checkAliveTerminals()
    }

    companion object {
        private const val CACHE_TTL_MS = 30 * 1000L
        private const val INITIAL_CACHE_TIME = 0L
        private const val RETRY_DELAY_MS = 500L
    }
}
