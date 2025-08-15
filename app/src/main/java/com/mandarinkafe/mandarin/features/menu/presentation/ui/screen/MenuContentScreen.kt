package com.mandarinkafe.mandarin.features.menu.presentation.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import com.mandarinkafe.mandarin.core.domain.models.CartItem
import com.mandarinkafe.mandarin.core.domain.models.Meal
import com.mandarinkafe.mandarin.core.presentation.theme.Colors
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.features.menu.domain.models.Banner
import com.mandarinkafe.mandarin.features.menu.presentation.models.MenuItem
import com.mandarinkafe.mandarin.features.menu.presentation.ui.components.BannersSection
import com.mandarinkafe.mandarin.features.menu.presentation.ui.components.MenuHeaderItem
import com.mandarinkafe.mandarin.features.menu.presentation.ui.components.MenuSubHeaderItem
import com.mandarinkafe.mandarin.features.menu.presentation.ui.components.categorytabs.CategoryTabsRow
import com.mandarinkafe.mandarin.features.menu.presentation.ui.components.categorytabs.SubCategoryTabsRow
import com.mandarinkafe.mandarin.features.menu.presentation.ui.components.mealitem.MenuCompactMealItem
import com.mandarinkafe.mandarin.features.menu.presentation.ui.components.mealitem.MenuMealItem
import com.mandarinkafe.mandarin.features.menu.presentation.ui.components.rememberScrollUiState
import com.mandarinkafe.mandarin.util.Constants.MENU_IMAGE_COLUMN_COUNT
import com.mandarinkafe.mandarin.util.Constants.MENU_IMAGE_SPACING_COUNT
import kotlinx.coroutines.launch

@Composable
fun MenuContentScreen(
    menuItems: List<MenuItem>,
    favoriteIds: Set<String>,
    inProgressItems: Set<String>,
    cartItems: List<CartItem>,
    onAddToCart: (Meal) -> Unit,
    onRemoveFromCart: (Meal) -> Unit,
    onToggleFavorite: (Meal) -> Unit,
    onMealDetailsClick: (Meal) -> Unit,
    onSearchClick: () -> Unit,
    bannersAreLoading: Boolean,
    banners: List<Banner>,
) {
    // 1. Вычисляем позиции категорий
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

    // Обновляем активную табу при скролле
    LaunchedEffect(scrollUi.listState) {
        snapshotFlow { scrollUi.listState.firstVisibleItemIndex }
            .collect {
                val newIndex = scrollUi.getActiveTabIndex()
                if (activeTabIndex.value != newIndex) {
                    activeTabIndex.value = newIndex
                    // сбрасываем активную субтабку при смене Header
                    activeSubTabIndex.value = 0
                }
                val newSubIndex = scrollUi.getActiveSubTabIndexForHeader(activeTabIndex.value)
                if (activeSubTabIndex.value != newSubIndex) {
                    activeSubTabIndex.value = newSubIndex
                }
            }
    }

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
                onBannerClick = { } // TODO
            )
        }
        // Табы категорий
        val headers = menuItems.filterIsInstance<MenuItem.HeaderItem>()
        stickyHeader {
            Column(Modifier.background(Colors.AppBlack)) {
                CategoryTabsRow(
                    categories = headers,
                    selectedTabIndex = activeTabIndex.value,
                    onTabSelected = { index ->
                        activeTabIndex.value = index
                        activeSubTabIndex.value = 0
                        scope.launch { scrollUi.scrollToCategory(index) }
                    },
                    onSearchClick = onSearchClick
                )

                // 2. Табы субкатегорий
                val activeHeaderIndex = activeTabIndex.value
                val subCategoriesForActive =
                    headers.getOrNull(activeHeaderIndex)?.subCategoriesNames.orEmpty()
                if (subCategoriesForActive.isNotEmpty()) {
                    SubCategoryTabsRow(
                        categories = subCategoriesForActive,
                        selectedTabIndex = activeSubTabIndex.value,
                        onTabSelected = { subIndex ->
                            activeSubTabIndex.value = subIndex
                            scope.launch {
                                scrollUi.scrollToSubCategory(
                                    activeHeaderIndex,
                                    subIndex
                                )
                            }
                        }
                    )
                }
            }
        }

        itemsIndexed(
            items = menuItems,
            key = { _, item -> item.id }
        ) { index, item ->
            val configuration = LocalConfiguration.current
            val screenWidth = configuration.screenWidthDp.dp
            val imageSize = remember(screenWidth) {
                (screenWidth - Dimens.MarginSmall8 * MENU_IMAGE_SPACING_COUNT) / MENU_IMAGE_COLUMN_COUNT
            }
            when (item) {
                is MenuItem.HeaderItem -> MenuHeaderItem(item)
                is MenuItem.SubHeaderItem -> {
                    val previousItem = if (index > 0) menuItems[index - 1] else null
                    val hasHeaderBefore = previousItem is MenuItem.HeaderItem
                    MenuSubHeaderItem(item, hasHeaderBefore)
                }

                is MenuItem.MealItem.SingleMealItem -> {
                    val isInProgress = item.meal.id in inProgressItems
                    // Одинарный формат
                    MenuMealItem(
                        meal = item.meal,
                        onToggleFavorite = onToggleFavorite,
                        onAddToCart = onAddToCart,
                        onRemoveFromCart = onRemoveFromCart,
                        cartItems = cartItems,
                        imageSize = imageSize,
                        onMealDetailsClick = onMealDetailsClick,
                        favoriteIds = favoriteIds,
                        isInProgress = isInProgress,
                    )
                }

                is MenuItem.MealItem.MealRow -> {
                    // Пара компактных карточек
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(Dimens.MarginSmall8),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(IntrinsicSize.Min)
                            .padding(horizontal = Dimens.MarginSmall8)
                    ) {
                        MenuCompactMealItem(
                            meal = item.left,
                            onToggleFavorite = onToggleFavorite,
                            onAddToCart = onAddToCart,
                            onRemoveFromCart = onRemoveFromCart,
                            cartItems = cartItems,
                            imageSize = imageSize,
                            modifier = Modifier.weight(1f),
                            onMealDetailsClick = onMealDetailsClick,
                            favoriteIds = favoriteIds,
                            isInProgress = item.left.id in inProgressItems,
                        )
                        MenuCompactMealItem(
                            meal = item.right,
                            onToggleFavorite = onToggleFavorite,
                            onAddToCart = onAddToCart,
                            onRemoveFromCart = onRemoveFromCart,
                            cartItems = cartItems,
                            imageSize = imageSize,
                            modifier = Modifier.weight(1f),
                            onMealDetailsClick = onMealDetailsClick,
                            favoriteIds = favoriteIds,
                            isInProgress = item.right.id in inProgressItems,
                        )
                    }

                }
            }
        }
        // Отступ внизу
        item { Spacer(modifier = Modifier.height(Dimens.MarginBig32)) }
    }
}
