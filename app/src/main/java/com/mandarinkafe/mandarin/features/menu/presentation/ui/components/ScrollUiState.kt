package com.mandarinkafe.mandarin.features.menu.presentation.ui.components

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember

@Stable
class ScrollUiState(
    val listState: LazyListState,
    private val categoryPositions: List<Int>,
    private val subCategoryPositionsMap: Map<Int, List<Int>>
) {
    fun getActiveTabIndex(): Int {
        val firstVisible = listState.firstVisibleItemIndex
        return categoryPositions.indexOfLast { it <= firstVisible }.coerceAtLeast(0)
    }

    suspend fun scrollToCategory(index: Int) {
        listState.scrollToItem(categoryPositions[index])
    }

    fun getActiveSubTabIndexForHeader(headerIndex: Int): Int {
        val firstVisible = listState.firstVisibleItemIndex
        val subPositions =
            subCategoryPositionsMap[categoryPositions.getOrNull(headerIndex)] ?: return 0
        return subPositions.indexOfLast { it <= firstVisible }.coerceAtLeast(0)
    }

    suspend fun scrollToSubCategory(headerIndex: Int, subIndex: Int) {
        val subPositions =
            subCategoryPositionsMap[categoryPositions.getOrNull(headerIndex)] ?: return
        listState.scrollToItem(subPositions.getOrNull(subIndex) ?: return)
    }
}

@Composable
fun rememberScrollUiState(
    categoryPositions: List<Int>,
    subCategoryPositionsMap: Map<Int, List<Int>>
): ScrollUiState {
    val listState = rememberLazyListState()
    return remember(categoryPositions) {
        ScrollUiState(listState, categoryPositions, subCategoryPositionsMap)
    }
}