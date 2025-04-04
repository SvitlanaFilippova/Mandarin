package com.mandarinkafe.mandarin.menu.domain.impl

import com.mandarinkafe.mandarin.menu.domain.MenuConverter
import com.mandarinkafe.mandarin.menu.domain.api.MenuRepository
import com.mandarinkafe.mandarin.menu.domain.models.MealCategory
import com.mandarinkafe.mandarin.menu.domain.usecase.MenuInteractor
import com.mandarinkafe.mandarin.util.RVItem
import com.mandarinkafe.mandarin.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow

class MenuInteractorImpl(private val repository: MenuRepository) : MenuInteractor {

    private val menuCache = MutableStateFlow<Resource<List<MealCategory>>?>(null)

    override fun getMenu(): Flow<Pair<List<RVItem>?, String?>> = flow {
        // Загружаем и кэшируем меню, если ещё не загружено
        if (menuCache.value == null) {
            repository.getMenu().collect { menuCache.value = it }
        }
        emit(
            when (val result = menuCache.value) {
                is Resource.Success -> {
                    // Фильтруем все категории, которые не должны отображаться в общем меню (флаг isHidden)
                    val visibleMenu = result.data?.filterNot { it.isHidden }
                    Pair(MenuConverter.menuToMenuItems(visibleMenu), null)
                }

                is Resource.Error -> Pair(null, result.message)
                else -> Pair(null, "Неизвестная ошибка")
            }
        )
    }

    override fun getAddons(): Flow<Pair<List<RVItem>?, String?>> = flow {
        if (menuCache.value == null) {
            repository.getMenu().collect { menuCache.value = it }
        }

        emit(
            when (val result = menuCache.value) {
                is Resource.Success -> {
                    val addons = result.data?.filter { isAddonCategory(it) }
                    Pair(MenuConverter.menuToMenuItems(addons), null)
                }

                is Resource.Error -> Pair(null, result.message)
                else -> Pair(null, "Неизвестная ошибка")
            }
        )
    }

    override fun getRecommends(): Flow<Pair<List<RVItem>?, String?>> = flow {
        if (menuCache.value == null) {
            repository.getMenu().collect { menuCache.value = it }
        }

        emit(
            when (val result = menuCache.value) {
                is Resource.Success -> {
                    val addons = result.data?.filter { isRecommends(it) }
                    Pair(MenuConverter.menuToMenuItems(addons), null)
                }

                is Resource.Error -> Pair(null, result.message)
                else -> Pair(null, "Неизвестная ошибка")
            }
        )
    }

    private fun isRecommends(category: MealCategory): Boolean {
        // TODO Вместо хардкода потом вынести в конфиг
        return category.name.contains("рекоменд", ignoreCase = true)
    }

    private fun isAddonCategory(category: MealCategory): Boolean {
        // TODO Вместо хардкода потом вынести в конфиг
        return category.name.contains("добавки", ignoreCase = true)
    }

    // метод, чтобы принудительно перезагрузить меню и обновить в кэше:
    override suspend fun refreshMenu() {
        repository.getMenu().collect { menuCache.value = it }
    }

}