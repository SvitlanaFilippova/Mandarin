package com.mandarinkafe.mandarin.menu.domain.impl

import com.mandarinkafe.mandarin.menu.domain.MenuConverter
import com.mandarinkafe.mandarin.menu.domain.api.MenuInteractor
import com.mandarinkafe.mandarin.menu.domain.api.MenuRepository
import com.mandarinkafe.mandarin.menu.domain.models.mockMenuData
import com.mandarinkafe.mandarin.util.RVItem
import com.mandarinkafe.mandarin.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class MenuInteractorImpl(private val repository: MenuRepository) : MenuInteractor {

    override fun getMenu(): Flow<Pair<List<RVItem>?, String?>> {
        return repository.getMenu().map { result ->
            when (result) {
                is Resource.Success -> {
                    Pair(MenuConverter.menuToMenuItems(result.data), null)
                }

                is Resource.Error -> {
                    Pair(null, result.message)
                }
            }
        }
    }

    override fun getMockMenu(): List<RVItem> {
        return MenuConverter.menuToMenuItems(mockMenuData)
    }
}
