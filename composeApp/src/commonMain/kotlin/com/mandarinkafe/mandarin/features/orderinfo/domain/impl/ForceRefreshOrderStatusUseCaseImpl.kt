package com.mandarinkafe.mandarin.features.orderinfo.domain.impl

import com.mandarinkafe.mandarin.core.domain.models.IncomingOrder
import com.mandarinkafe.mandarin.features.orderinfo.domain.api.ForceRefreshOrderStatusUseCase
import com.mandarinkafe.mandarin.features.orderinfo.domain.api.GetOrderStatusUseCase
import com.mandarinkafe.mandarin.util.Resource
import com.mandarinkafe.mandarin.util.getCurrentTimeMillis

class ForceRefreshOrderStatusUseCaseImpl(
    private val getOrderStatusUseCase: GetOrderStatusUseCase,
) : ForceRefreshOrderStatusUseCase {

    private var lastSuccessTime: Long = INITIAL_TIME

    override suspend fun invoke(id: String): Resource<IncomingOrder> {
        val now = getCurrentTimeMillis()
        val timeSinceLastSuccess = now - lastSuccessTime

        // Игнорируем запрос, если прошло менее 30 секунд с последнего успешного ответа
        if (timeSinceLastSuccess < FORCE_REFRESH_TTL_MS) {
            return Resource.Idle()
        }

        val result = getOrderStatusUseCase(id)

        // Обновляем время только при успешном ответе
        if (result is Resource.Success) {
            lastSuccessTime = now
        }

        return result
    }

    private companion object {
        private const val FORCE_REFRESH_TTL_MS = 30_000L // 30 секунд
        private const val INITIAL_TIME = 0L
    }
}

