package com.mandarinkafe.mandarin.menu.domain.api
import com.mandarinkafe.mandarin.core.ui.RVItem
import com.mandarinkafe.mandarin.menu.domain.models.MealCategory
import kotlinx.coroutines.flow.Flow

interface MenuInteractor {
    fun getMenuRvItem(): Flow<Pair<List<RVItem>?, String?>>
    fun getMockMenu(): List<RVItem>
    fun getMenu(): Flow<Pair<List<MealCategory>?, String?>>

}