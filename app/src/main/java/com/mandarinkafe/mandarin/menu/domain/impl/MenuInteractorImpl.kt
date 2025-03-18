package com.mandarinkafe.mandarin.menu.domain.impl

import com.mandarinkafe.mandarin.core.ui.RVItem
import com.mandarinkafe.mandarin.menu.domain.api.MenuInteractor
import com.mandarinkafe.mandarin.menu.domain.api.MenuRepository
import com.mandarinkafe.mandarin.menu.domain.models.MealCategory
import com.mandarinkafe.mandarin.menu.domain.models.MenuRVItem
import com.mandarinkafe.mandarin.menu.domain.models.mockMenuData
import com.mandarinkafe.mandarin.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class MenuInteractorImpl(private val repository: MenuRepository) : MenuInteractor {


    override fun getMenu(): Flow<Pair<List<RVItem>?, String?>> {
        return repository.getMenu().map { result ->
            when (result) {
                is Resource.Success -> {
                    Pair(menuToMenuItems(result.data), null)
                }

                is Resource.Error -> {
                    Pair(null, result.message)
                }
            }
        }
    }

    override fun getMockMenu(): List<RVItem> {
        return menuToMenuItems(mockMenuData)
    }


    private fun menuToMenuItems(menu: List<MealCategory>?): List<RVItem> {
        val menuItems = buildList<RVItem> {
            menu?.forEach { category ->
                if (!category.subCategories.isNullOrEmpty()) {
                    this += MenuRVItem.HeaderItem(
                        categoryName = category.name,
                        subCategoriesNames = buildList {
                            category.subCategories.forEach { this += it.name }
                        },
                        tabIcon = category.tabIcon
                    )

                    category.subCategories.forEach { subCategory ->
                        if (!subCategory.meals.isNullOrEmpty()) {
                            this += MenuRVItem.SubHeaderItem(
                                categoryName = subCategory.name
                            )
                            this += subCategory.meals.map { MenuRVItem.MealItem(meal = it) }
                        }
                    }
                } else {
                    if (!category.meals.isNullOrEmpty()) {
                        this += MenuRVItem.HeaderItem(
                            categoryName = category.name,
                            subCategoriesNames = null,
                            tabIcon = category.tabIcon
                        )
                        this += category.meals.map { MenuRVItem.MealItem(it) }
                    }
                }
            }
        }
        return menuItems
    }
}

