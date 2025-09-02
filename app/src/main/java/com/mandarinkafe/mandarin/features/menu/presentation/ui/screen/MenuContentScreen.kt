package com.mandarinkafe.mandarin.features.menu.presentation.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import com.mandarinkafe.mandarin.core.domain.models.CartItem
import com.mandarinkafe.mandarin.core.domain.models.Meal
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.features.menu.domain.models.Banner
import com.mandarinkafe.mandarin.features.menu.presentation.models.MenuItem
import com.mandarinkafe.mandarin.features.menu.presentation.ui.components.BackToTopFAB
import com.mandarinkafe.mandarin.features.menu.presentation.ui.components.BannersSection
import com.mandarinkafe.mandarin.features.menu.presentation.ui.components.MenuItemCard
import com.mandarinkafe.mandarin.features.menu.presentation.ui.components.categorytabs.TabsSection
import com.mandarinkafe.mandarin.features.menu.presentation.ui.components.rememberScrollUiState
import com.mandarinkafe.mandarin.util.Constants.DEFAULT_UNSELECTED_INDEX
import com.mandarinkafe.mandarin.util.Constants.FORCE_SHOW_FAB_DURATION_MS
import com.mandarinkafe.mandarin.util.Constants.MENU_IMAGE_COLUMN_COUNT
import com.mandarinkafe.mandarin.util.Constants.MENU_IMAGE_SPACING_COUNT
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun MenuContentScreen(
    menuItems: List<MenuItem>,
    favoriteIds: Set<String>,
    inProgressItems: Set<String>,
    cartItems: List<CartItem>,
    onAddToCart: (Meal) -> Unit,
    onRemoveFromCart: (Meal) -> Unit,
    onBannersClick: (Banner) -> Unit,
    onToggleFavorite: (Meal) -> Unit,
    onMealDetailsClick: (Meal) -> Unit,
    onSearchClick: () -> Unit,
    bannersAreLoading: Boolean,
    selectedMenuItemIndex: Int,
    banners: List<Banner>,
) {
    val categoryPositions = remember(menuItems) {
        menuItems.mapIndexedNotNull { index, item ->
            if (item is MenuItem.HeaderItem) index else null
        }
    }
    val subCategoryPositionsMap = remember(menuItems) {
        categoryPositions.associateWith { headerIndex ->
            menuItems.mapIndexedNotNull { index, item ->
                if (index > headerIndex && item is MenuItem.SubHeaderItem) index else null
            }
        }
    }

    val scrollUi = rememberScrollUiState(categoryPositions, subCategoryPositionsMap)
    val scope = rememberCoroutineScope()
    val activeTabIndex = remember { mutableStateOf(0) }
    val activeSubTabIndex = remember { mutableStateOf(-1) }
    var fabVisible by remember { mutableStateOf(false) }
    fun CoroutineScope.showFabTemporarily() = launch {
        fabVisible = true
        delay(FORCE_SHOW_FAB_DURATION_MS)
        fabVisible = false
    }

    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val imageSize = remember(screenWidth) {
        (screenWidth - Dimens.MarginSmall8 * MENU_IMAGE_SPACING_COUNT) / MENU_IMAGE_COLUMN_COUNT
    }
    // Обновляем активную табу при скролле
    LaunchedEffect(scrollUi.listState) {
        snapshotFlow { scrollUi.listState.firstVisibleItemIndex }
            .collect {
                val newIndex = scrollUi.getActiveTabIndex()
                if (activeTabIndex.value != newIndex) {
                    activeTabIndex.value = newIndex
                    activeSubTabIndex.value = 0
                }
                val newSubIndex = scrollUi.getActiveSubTabIndexForHeader(activeTabIndex.value)
                if (activeSubTabIndex.value != newSubIndex) {
                    activeSubTabIndex.value = newSubIndex
                }
            }
    }

    // Скроллим к баннеру и показываем FAB
    LaunchedEffect(selectedMenuItemIndex) {
        if (selectedMenuItemIndex != DEFAULT_UNSELECTED_INDEX) {
            scrollUi.listState.scrollToItem(selectedMenuItemIndex)
            scope.showFabTemporarily()
        }
    }
    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            state = scrollUi.listState,
            verticalArrangement = Arrangement.spacedBy(Dimens.MarginSmall8),
        ) {
            // 1. Баннеры (скроллятся вместе с контентом)
            item {
                BannersSection(
                    banners = banners,
                    bannersAreLoading = bannersAreLoading,
                    onBannerClick = onBannersClick
                )
            }
            // Табы категорий
            val headers = menuItems.filterIsInstance<MenuItem.HeaderItem>()
            stickyHeader {
                TabsSection(
                    headers = headers,
                    activeTabIndex = activeTabIndex.value,
                    activeSubTabIndex = activeSubTabIndex.value,
                    onCategorySelected = { index ->
                        activeTabIndex.value = index
                        activeSubTabIndex.value = 0
                        scope.launch {
                            scrollUi.scrollToCategory(index)
                            scope.showFabTemporarily()
                        }
                    },
                    onSubCategorySelected = { subIndex ->
                        activeSubTabIndex.value = subIndex
                        scope.launch {
                            scrollUi.scrollToSubCategory(
                                activeTabIndex.value,
                                subIndex
                            )
                            scope.showFabTemporarily()
                        }
                    },
                    onSearchClick = onSearchClick
                )
            }
            itemsIndexed(
                items = menuItems,
                key = { _, item -> item.id }
            ) { index, item ->
                val previousItem = menuItems.getOrNull(index - 1)
                MenuItemCard(
                    item = item,
                    previousItem = previousItem,
                    cartItems = cartItems,
                    favoriteIds = favoriteIds,
                    inProgressItems = inProgressItems,
                    imageSize = imageSize,
                    onAddToCart = onAddToCart,
                    onRemoveFromCart = onRemoveFromCart,
                    onToggleFavorite = onToggleFavorite,
                    onMealDetailsClick = onMealDetailsClick
                )
            }
            // Отступ внизу
            item { Spacer(modifier = Modifier.height(Dimens.MarginBig32)) }
        }
        BackToTopFAB(
            modifier = Modifier
                .align(Alignment.BottomStart),
            visible = fabVisible,
            onClick = {
                scope.launch {
                    scrollUi.listState.animateScrollToItem(0)
                }
            }
        )
    }
}
