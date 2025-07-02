package com.mandarinkafe.mandarin.features.menu.presentation.ui.screen

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import com.mandarinkafe.mandarin.features.menu.domain.models.Banner
import com.mandarinkafe.mandarin.features.menu.presentation.models.MenuItem
import com.mandarinkafe.mandarin.features.menu.presentation.ui.components.getVisibleCategoryIndexes
import com.mandarinkafe.mandarin.features.menu.presentation.viewmodel.MenuContract.MenuEvent
import com.mandarinkafe.mandarin.shared.ui.viewmodel.SharedContract.SharedEvent
import com.mandarinkafe.mandarin.util.Constants.FORCE_SHOW_FAB_DURATION_MS
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
internal fun rememberScrollUi(
    listState: LazyListState,
    selectedTabIndex: Int,
    selectedSubTabIndex: Int,
    selectedMenuItemIndex: Int,
    menuItems: List<MenuItem>,
    categoriesNames: List<String>,
    onMenuEvent: (MenuEvent) -> Unit,
    onSharedEvent: (SharedEvent) -> Unit,

    ): ScrollUi {
    var isScrollingUp by remember { mutableStateOf(false) }
    var isScrollingDown by remember { mutableStateOf(false) }
    var previousIndex by remember { mutableIntStateOf(0) }
    var previousOffset by remember { mutableIntStateOf(0) }

    val isAtTop by remember {
        derivedStateOf {
            listState.firstVisibleItemScrollOffset == 0
        }
    }

    val coroutineScope = rememberCoroutineScope()

    val showMenuTopBar by remember {
        derivedStateOf { isAtTop || isScrollingUp }
    }

    var forceShowBackToTopFAB by remember { mutableStateOf(false) }
    val showBackToTopFAB by remember {
        derivedStateOf { forceShowBackToTopFAB || !isAtTop && !isScrollingDown }
    }

    // Скрыть/показать TopBar в зависимости от видимой части экрана
    LaunchedEffect(isAtTop) {
        if (isAtTop) {
            onSharedEvent(SharedEvent.ShowTopBar)
        } else {
            onSharedEvent(SharedEvent.HideTopBar)
        }
    }

    // Отслеживание изменения selectedMenuItemIndex и скролл при обновлении
    LaunchedEffect(selectedMenuItemIndex) {
        if (selectedMenuItemIndex >= 0) {
            listState.scrollToItem(selectedMenuItemIndex, scrollOffset = 1)
            onMenuEvent(MenuEvent.ResetSelectedMenuItemIndex)
        }
    }

    // Отслеживание направления скролла
    LaunchedEffect(listState) {
        snapshotFlow {
            listState.firstVisibleItemIndex to listState.firstVisibleItemScrollOffset
        }.collect { (index, offset) ->
            val deltaIndex = index - previousIndex
            val deltaOffset = offset - previousOffset

            val isScrollingDownNow = when {
                deltaIndex > 0 -> true
                deltaIndex < 0 -> false
                else -> deltaOffset > 0
            }

            val isScrollingUpNow = when {
                deltaIndex < 0 -> true
                deltaIndex > 0 -> false
                else -> deltaOffset < 0
            }

            isScrollingDown = isScrollingDownNow
            isScrollingUp = isScrollingUpNow

            previousIndex = index
            previousOffset = offset
        }
    }

    // Отслеживание скролла для обновления активного таба
    LaunchedEffect(listState) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo }
            .collect { visibleItems ->
                val (newCategoryIndex, newSubIndex) = getVisibleCategoryIndexes(
                    visibleItems = visibleItems,
                    menuItems = menuItems,
                    categoriesNames = categoriesNames
                )

                if (newCategoryIndex != null && newCategoryIndex != selectedTabIndex) {
                    onMenuEvent(MenuEvent.ScrollToCategory(newCategoryIndex))
                }
                if (newSubIndex != null && newSubIndex != selectedSubTabIndex) {
                    onMenuEvent(MenuEvent.ScrollToSubCategory(newSubIndex))
                }
            }
    }

    fun onBannerClick(banner: Banner) {
        coroutineScope.launch {
            onMenuEvent(MenuEvent.BannerClick(banner))
            forceShowBackToTopFAB = true
            delay(FORCE_SHOW_FAB_DURATION_MS)
            forceShowBackToTopFAB = false
        }
    }

    fun onBackToTopClick() {
        coroutineScope.launch {
            listState.scrollToItem(index = 0)
            onMenuEvent(MenuEvent.ScrollToTop)
        }
    }

    fun scrollToCategory(index: Int) {
        onMenuEvent(MenuEvent.ScrollToCategory(index))
        coroutineScope.launch {
            val targetIndex = menuItems.indexOfFirst {
                it is MenuItem.HeaderItem && it.categoryName == categoriesNames[index]
            }
            if (targetIndex >= 0) {
                listState.scrollToItem(index = targetIndex, scrollOffset = 1)
            }
        }
    }

    fun scrollToSubCategory(index: Int, currentSubCategories: List<String>) {
        onMenuEvent(MenuEvent.ScrollToSubCategory(index))
        coroutineScope.launch {
            val targetIndex = menuItems.indexOfFirst {
                it is MenuItem.SubHeaderItem && it.categoryName == currentSubCategories[index]
            }
            if (targetIndex >= 0) {
                listState.scrollToItem(
                    index = targetIndex,
                    scrollOffset = 1
                )
            }
        }
    }

    return ScrollUi(
        isAtTop = isAtTop,
        showMenuTopBar = showMenuTopBar,
        showBackToTopFAB = showBackToTopFAB,
        onBannerClick = ::onBannerClick,
        onBackToTopClick = ::onBackToTopClick,
        scrollToCategory = ::scrollToCategory,
        scrollToSubCategory = ::scrollToSubCategory
    )
}

internal data class ScrollUi(
    val isAtTop: Boolean,
    val showMenuTopBar: Boolean,
    val showBackToTopFAB: Boolean,
    val onBannerClick: (Banner) -> Unit,
    val onBackToTopClick: () -> Unit,
    val scrollToCategory: (Int) -> Unit,
    val scrollToSubCategory: (Int, currentSubCategories: List<String>) -> Unit
)