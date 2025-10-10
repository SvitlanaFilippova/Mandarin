package com.mandarinkafe.mandarin.core.domain.impl

import com.mandarinkafe.mandarin.core.domain.api.ForceRefreshMenuUseCase
import com.mandarinkafe.mandarin.core.domain.api.MenuCache
import com.mandarinkafe.mandarin.features.menu.domain.api.MenuRepository

class ForceRefreshMenuUseCaseImpl(
    private val repository: MenuRepository,
    private val cache: MenuCache
) : ForceRefreshMenuUseCase {
    /**
     * метод, чтобы принудительно перезагрузить меню
     */
    override suspend fun invoke() {
        cache.forceRefresh { repository.fetchMenu() }
    }
}
