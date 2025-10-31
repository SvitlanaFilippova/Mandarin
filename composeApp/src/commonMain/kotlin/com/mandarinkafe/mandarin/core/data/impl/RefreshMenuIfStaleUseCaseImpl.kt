package com.mandarinkafe.mandarin.core.data.impl

import com.mandarinkafe.mandarin.core.data.api.RefreshMenuIfStaleUseCase
import com.mandarinkafe.mandarin.core.domain.api.ForceRefreshMenuUseCase
import com.mandarinkafe.mandarin.core.domain.api.MenuCache
import com.mandarinkafe.mandarin.util.getCurrentTimeMillis
import io.github.aakira.napier.Napier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RefreshMenuIfStaleUseCaseImpl(
    private val forceRefreshMenuUseCase: ForceRefreshMenuUseCase,
    private val menuCache: MenuCache
) : RefreshMenuIfStaleUseCase {
    override suspend fun invoke() {
        val now = getCurrentTimeMillis()
        val lastRefresh = menuCache.lastRefreshTime

        if (now - lastRefresh > REFRESH_INTERVAL_MS) {
            CoroutineScope(Dispatchers.Default).launch {
                try {
                    forceRefreshMenuUseCase()
                } catch (e: Exception) {
                    Napier.e("Menu refresh failed", e)
                }
            }
        }
    }

    private companion object {
        const val REFRESH_INTERVAL_MS = 30 * 60 * 1000L // 30 минут
    }
}
