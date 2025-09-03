package com.mandarinkafe.mandarin.features.menu.presentation.ui.components

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.runningFold
import kotlinx.coroutines.flow.stateIn

@Stable
class ScrollUiState(
    val listState: LazyListState,
    private val categoryPositions: List<Int>,
    private val subCategoryPositionsMap: Map<Int, List<Int>>
) {
    /**
     * Направление скролла: true — вверх, false — вниз
     */
    val isScrollingUp: StateFlow<Boolean> = snapshotFlow {
        listState.firstVisibleItemIndex to listState.firstVisibleItemScrollOffset
    }
        .runningFold(
            initial = Triple(false, 0, 0), // (isUp, prevIndex, prevOffset)
        ) { (_, prevIndex, prevOffset), (index, offset) ->
            when {
                index < prevIndex -> Triple(true, index, offset)
                index > prevIndex -> Triple(false, index, offset)
                offset < prevOffset -> Triple(true, index, offset)
                offset > prevOffset -> Triple(false, index, offset)
                else -> Triple(false, index, offset)
            }
        }
        .map { it.first } // берём только флаг isUp
        .stateIn(
            scope = CoroutineScope(Dispatchers.Main.immediate + SupervisorJob()),
            started = SharingStarted.Eagerly,
            initialValue = false
        )

    /**
     * Находится ли список в самом верху (первый элемент + смещение == 0)
     */
    val isAtTop: StateFlow<Boolean> = snapshotFlow {
        listState.firstVisibleItemIndex == 0 && listState.firstVisibleItemScrollOffset == 0
    }
        .stateIn(
            scope = CoroutineScope(Dispatchers.Main.immediate + SupervisorJob()),
            started = SharingStarted.Eagerly,
            initialValue = true
        )

    fun getActiveTabIndex(): Int {
        val firstVisible = listState.firstVisibleItemIndex
        return categoryPositions.indexOfLast { it <= firstVisible }.coerceAtLeast(0)
    }

    suspend fun scrollToCategory(index: Int) {
        listState.scrollToItem(categoryPositions[index] + 1)
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
        val target = subPositions.getOrNull(subIndex) ?: return
        listState.scrollToItem(
            index = target + 1
        )
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