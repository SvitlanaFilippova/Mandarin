package com.mandarinkafe.mandarin.features.menu.domain.impl

import com.mandarinkafe.mandarin.core.domain.api.MenuCache
import com.mandarinkafe.mandarin.core.domain.models.MealCategory
import com.mandarinkafe.mandarin.features.menu.domain.api.MenuRepository
import com.mandarinkafe.mandarin.features.menu.domain.usecase.MenuInteractor
import com.mandarinkafe.mandarin.util.Resource
import com.mandarinkafe.mandarin.util.Resource.Success
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map

class MenuInteractorImpl(
    private val repository: MenuRepository,
    private val cache: MenuCache
) : MenuInteractor {
    override val menu: StateFlow<Resource<List<MealCategory>>> get() = cache.menu

    override fun getMenu(): Flow<Resource<List<MealCategory>>> {
        cache.fetchMenuIfNeeded()
        return cache.menu.map { result ->
            when (result) {
                is Success -> {
                    val filtered = result.data?.filterNot { it.isHidden }
                    Success(filtered ?: emptyList())
                }
                else -> result
            }
        }
    }

    // метод, чтобы принудительно перезагрузить меню
    override suspend fun forceRefresh() {
        cache.forceRefresh { repository.fetchMenu() }
    }
}
