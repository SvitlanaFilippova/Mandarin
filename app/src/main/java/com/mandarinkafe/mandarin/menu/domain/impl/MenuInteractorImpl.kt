package com.mandarinkafe.mandarin.menu.domain.impl

import com.mandarinkafe.mandarin.di.Addons
import com.mandarinkafe.mandarin.di.Recommends
import com.mandarinkafe.mandarin.menu.domain.api.MenuRepository
import com.mandarinkafe.mandarin.menu.domain.mappers.MenuRVItemMapper
import com.mandarinkafe.mandarin.menu.domain.models.MealCategory
import com.mandarinkafe.mandarin.menu.domain.models.MenuRVItem
import com.mandarinkafe.mandarin.menu.domain.usecase.CategoryFilter
import com.mandarinkafe.mandarin.menu.domain.usecase.MenuInteractor
import com.mandarinkafe.mandarin.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class MenuInteractorImpl(
    private val repository: MenuRepository,
    @Recommends private val recommendsFilter: CategoryFilter,
    @Addons private val addonsFilter: CategoryFilter
) : MenuInteractor {

    override fun getMenu(): Flow<Pair<List<MenuRVItem>?, String?>> = repository.getMenu()
        .map { result ->
            when (result) {
                is Resource.Success -> {
                    // Фильтруем все категории, которые не должны отображаться в общем меню (флаг isHidden)
                    val visibleMenu = result.data?.filterNot { it.isHidden }
                    Pair(MenuRVItemMapper.menuToMenuItems(visibleMenu), null)
                }

                is Resource.Error -> {
                    Pair(null, result.message)
                }

                is Resource.Loading -> {
                    Pair(null, null)
                }
            }
        }

    override fun getAddons(): Flow<Pair<List<MealCategory>?, String?>> = repository.getMenu()
        .map { result ->
            when (result) {
                is Resource.Success -> {
                    val addons = result.data?.filter { addonsFilter.isMatch(it) }
                    Pair(addons, null)
                }

                is Resource.Error -> {
                    Pair(null, result.message)
                }

                is Resource.Loading -> {
                    Pair(null, null)
                }
            }
        }

    override fun getRecommends(): Flow<Pair<List<MenuRVItem>?, String?>> = repository.getMenu()
        .map { result ->
            when (result) {
                is Resource.Success -> {
                    val recommends = result.data?.filter { recommendsFilter.isMatch(it) }
                    Pair(MenuRVItemMapper.menuToMenuItems(recommends), null)
                }

                is Resource.Error -> {
                    Pair(null, result.message)
                }

                is Resource.Loading -> {
                    Pair(null, null)
                }
            }
        }

    // метод, чтобы принудительно перезагрузить меню
    override suspend fun forceRefresh() {
        repository.forceRefresh()
    }
}
