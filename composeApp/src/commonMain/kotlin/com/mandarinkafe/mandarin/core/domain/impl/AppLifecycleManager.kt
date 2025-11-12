package com.mandarinkafe.mandarin.core.domain.impl

import com.mandarinkafe.mandarin.core.data.api.RefreshMenuIfStaleUseCase
import com.mandarinkafe.mandarin.features.auth.domain.api.AuthRepository
import com.mandarinkafe.mandarin.features.auth.domain.api.SyncUserDataUseCase
import io.github.aakira.napier.Napier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/**
 * Менеджер для выполнения действий при возврате приложения из фона
 */
class AppLifecycleManager(
    private val refreshMenuIfStaleUseCase: RefreshMenuIfStaleUseCase,
    private val syncUserDataUseCase: SyncUserDataUseCase,
    private val authRepository: AuthRepository,
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    fun onAppForegrounded() {
        scope.launch {
            try {
                // Обновляем меню, если оно устарело
                refreshMenuIfStaleUseCase()

                // Синхронизируем данные пользователя, если он авторизован
                if (authRepository.isAuthorized()) {
                    syncUserDataUseCase()
                }
            } catch (e: Exception) {
                Napier.e("AppLifecycleManager: Error during foreground refresh", e)
            }
        }
    }
}

