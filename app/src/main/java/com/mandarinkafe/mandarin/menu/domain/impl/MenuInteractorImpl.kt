package com.mandarinkafe.mandarin.menu.domain.impl

import com.mandarinkafe.mandarin.menu.domain.MenuConverter
import com.mandarinkafe.mandarin.menu.domain.api.MenuRepository
import com.mandarinkafe.mandarin.menu.domain.models.MealCategory
import com.mandarinkafe.mandarin.menu.domain.models.MenuRVItem
import com.mandarinkafe.mandarin.menu.domain.usecase.MenuInteractor
import com.mandarinkafe.mandarin.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class MenuInteractorImpl(private val repository: MenuRepository) : MenuInteractor {

    override fun getMenu(): Flow<Pair<List<MenuRVItem>?, String?>> = repository.menu
        .map { result ->
            when (result) {
                is Resource.Success -> {
                    // Фильтруем все категории, которые не должны отображаться в общем меню (флаг isHidden)
                    val visibleMenu = result.data?.filterNot { it.isHidden }
                    Pair(MenuConverter.menuToMenuItems(visibleMenu), null)
                }

                is Resource.Error -> {
                    Pair(null, result.message)
                }
            }
        }

    override fun getAddons(): Flow<Pair<List<MenuRVItem>?, String?>> = repository.menu
        .map { result ->
            when (result) {
                is Resource.Success -> {
                    val addons = result.data?.filter { isAddonCategory(it) }
                    Pair(MenuConverter.menuToMenuItems(addons), null)
                }

                is Resource.Error -> {
                    Pair(null, result.message)
                }
            }
        }

    override fun getRecommends(): Flow<Pair<List<MenuRVItem>?, String?>> = repository.menu
        .map { result ->
            when (result) {
                is Resource.Success -> {
                    val recommends = result.data?.filter { isRecommends(it) }
                    Pair(MenuConverter.menuToMenuItems(recommends), null)
                }

                is Resource.Error -> {
                    Pair(null, result.message)
                }
            }
        }

    private fun isRecommends(category: MealCategory): Boolean {
        // TODO Вместо хардкода потом вынести в конфиг
        return category.name.contains("рекоменд", ignoreCase = true)
    }

    private fun isAddonCategory(category: MealCategory): Boolean {
        // TODO Вместо хардкода потом вынести в конфиг
        return category.name.contains("добавки", ignoreCase = true)
    }

    // метод, чтобы принудительно перезагрузить меню
    override suspend fun forceRefresh() {
        repository.forceRefresh()
    }
}
