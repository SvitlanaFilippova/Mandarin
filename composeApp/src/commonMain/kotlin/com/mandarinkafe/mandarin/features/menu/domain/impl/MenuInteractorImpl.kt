package com.mandarinkafe.mandarin.features.menu.domain.impl

import com.mandarinkafe.mandarin.core.domain.api.MenuCache
import com.mandarinkafe.mandarin.core.domain.models.MealCategory
import com.mandarinkafe.mandarin.features.menu.domain.api.MenuInteractor
import com.mandarinkafe.mandarin.util.Resource
import com.mandarinkafe.mandarin.util.Resource.Success
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map

class MenuInteractorImpl(
    private val cache: MenuCache,
) : MenuInteractor {
    override val menu: StateFlow<Resource<List<MealCategory>>> get() = cache.mainMenu

    override fun getMenu(): Flow<Resource<List<MealCategory>>> {
        cache.fetchMenuIfNeeded()
        return cache.mainMenu.map { result ->
            when (result) {
                is Success -> Success(result.data ?: emptyList())
                else -> result
            }
        }
    }
}
