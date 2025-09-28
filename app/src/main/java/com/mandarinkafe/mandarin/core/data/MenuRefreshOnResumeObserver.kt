package com.mandarinkafe.mandarin.core.data

import android.util.Log
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.mandarinkafe.mandarin.core.domain.api.ForceRefreshMenuUseCase
import com.mandarinkafe.mandarin.core.domain.api.MenuCache
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MenuRefreshOnResumeObserver @Inject constructor(
    private val forceRefreshMenuUseCase: ForceRefreshMenuUseCase,
    private val menuCache: MenuCache
) : DefaultLifecycleObserver {

    override fun onStart(owner: LifecycleOwner) {
        val now = System.currentTimeMillis()
        val lastRefresh = menuCache.lastRefreshTime

        if (now - lastRefresh > REFRESH_INTERVAL_MS) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    forceRefreshMenuUseCase()
                } catch (e: Exception) {
                    Log.e("MenuRefresh", "Menu refresh failed", e)
                }
            }
        }
    }

    private companion object {
        const val REFRESH_INTERVAL_MS = 30 * 60 * 1000L // 30 минут
    }
}