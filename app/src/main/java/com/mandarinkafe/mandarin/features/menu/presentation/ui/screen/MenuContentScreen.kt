package com.mandarinkafe.mandarin.features.menu.presentation.ui.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.mandarinkafe.mandarin.core.presentation.theme.Colors
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.features.menu.domain.models.Banner
import com.mandarinkafe.mandarin.features.menu.presentation.models.MenuItem
import com.mandarinkafe.mandarin.features.menu.presentation.ui.components.BackToTopFAB
import com.mandarinkafe.mandarin.features.menu.presentation.ui.components.BannersSection
import com.mandarinkafe.mandarin.features.menu.presentation.ui.components.MenuItemCard
import com.mandarinkafe.mandarin.features.menu.presentation.ui.components.MenuSearchBar
import com.mandarinkafe.mandarin.features.menu.presentation.ui.components.categorytabs.TabsSection
import com.mandarinkafe.mandarin.features.menu.presentation.ui.components.rememberScrollUiState
import com.mandarinkafe.mandarin.util.Constants
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
    val activeTabIndex = remember { mutableIntStateOf(0) }
    val activeSubTabIndex = remember { mutableIntStateOf(-1) }

    val isScrollingUp by scrollUi.isScrollingUp.collectAsState()
    val isAtTop by scrollUi.isAtTop.collectAsState()

    var showFab by remember { mutableStateOf(false) }
    fun CoroutineScope.showFabTemporarily() = launch {
        showFab = true
        delay(Constants.FORCE_SHOW_FAB_DURATION_MS)
        showFab = false
    }

    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val imageSize = remember(screenWidth) {
        (screenWidth - Dimens.MarginSmall8 * Constants.MENU_IMAGE_SPACING_COUNT) / Constants.MENU_IMAGE_COLUMN_COUNT
    }
    // Обновляем активную табу при скролле
    LaunchedEffect(scrollUi.listState) {
        snapshotFlow { scrollUi.listState.firstVisibleItemIndex }
            .collect {
                val newIndex = scrollUi.getActiveTabIndex()
                if (activeTabIndex.intValue != newIndex) {
                    activeTabIndex.intValue = newIndex
                    activeSubTabIndex.intValue = 0
                }
                val newSubIndex = scrollUi.getActiveSubTabIndexForHeader(activeTabIndex.intValue)
                if (activeSubTabIndex.intValue != newSubIndex) {
                    activeSubTabIndex.intValue = newSubIndex
                }
            }
    }

    // Скроллим к баннеру и показываем FAB
    LaunchedEffect(selectedMenuItemIndex) {
        if (selectedMenuItemIndex != Constants.DEFAULT_UNSELECTED_INDEX) {
            scrollUi.listState.scrollToItem(selectedMenuItemIndex + 1)
            scope.showFabTemporarily()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            state = scrollUi.listState,
            verticalArrangement = Arrangement.spacedBy(Dimens.MarginSmall8),
        ) {
            //  Баннеры (скроллятся вместе с контентом)
            item {
                BannersSection(
                    banners = banners,
                    bannersAreLoading = bannersAreLoading,
                    onBannerClick = onBannersClick
                )
            }

            stickyHeader {
                MenuStickyHeader(
                    isAtTop = isAtTop,
                    isScrollingUp = isScrollingUp,
                    menuItems = menuItems,
                    activeTabIndex = activeTabIndex.intValue,
                    activeSubTabIndex = activeSubTabIndex.intValue,
                    onSearchClick = onSearchClick,
                    onCategorySelected = { index ->
                        activeTabIndex.intValue = index
                        activeSubTabIndex.intValue = 0
                        scope.launch {
                            scrollUi.scrollToCategory(index)
                            scope.showFabTemporarily()
                        }
                    },
                    onSubCategorySelected = { subIndex ->
                        activeSubTabIndex.intValue = subIndex
                        scope.launch {
                            scrollUi.scrollToSubCategory(
                                activeTabIndex.intValue,
                                subIndex
                            )
                            scope.showFabTemporarily()
                        }
                    }
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
            visible = (showFab || isScrollingUp) && !isAtTop,
            onClick = {
                scope.launch {
                    scrollUi.listState.scrollToItem(0)
                }
            }
        )
    }
}

@Composable
private fun MenuStickyHeader(
    isAtTop: Boolean,
    isScrollingUp: Boolean,
    menuItems: List<MenuItem>,
    activeTabIndex: Int,
    activeSubTabIndex: Int,
    onSearchClick: () -> Unit,
    onCategorySelected: (Int) -> Unit,
    onSubCategorySelected: (Int) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(animationSpec = tween(durationMillis = Constants.ANIMATION_DURATION_FAST))
            .background(color = Colors.AppBlack)
    ) {
        AnimatedVisibility(
            visible = isAtTop || isScrollingUp,
            enter = fadeIn(animationSpec = tween(Constants.ANIMATION_DURATION_SUPER_FAST)) + slideInVertically(
                animationSpec = tween(
                    Constants.ANIMATION_DURATION_SUPER_FAST
                )
            ),
            exit = fadeOut(animationSpec = tween(Constants.ANIMATION_DURATION_SUPER_FAST)) + slideOutVertically(
                animationSpec = tween(Constants.ANIMATION_DURATION_SUPER_FAST)
            ),
        ) {
            MenuSearchBar(onSearchClick = onSearchClick)
        }

        // Табы категорий
        val headers = menuItems.filterIsInstance<MenuItem.HeaderItem>()
        TabsSection(
            headers = headers,
            activeTabIndex = activeTabIndex,
            activeSubTabIndex = activeSubTabIndex,
            onCategorySelected = onCategorySelected,
            onSubCategorySelected = onSubCategorySelected
        )
    }
}