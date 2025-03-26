package com.mandarinkafe.mandarin.menu.domain.api
import com.mandarinkafe.mandarin.util.RVItem
import kotlinx.coroutines.flow.Flow

interface MenuInteractor {
    fun getMenuRvItem(): Flow<Pair<List<RVItem>?, String?>>
    fun getMockMenu(): List<RVItem>
}