package com.mandarinkafe.mandarin.menu.ui.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.mandarinkafe.mandarin.core.ui.theme.Colors
import com.mandarinkafe.mandarin.menu.domain.models.MenuRVItem
import com.mandarinkafe.mandarin.menu.ui.MenuContract.Event
import com.mandarinkafe.mandarin.menu.ui.components.BannerCarousel
import com.mandarinkafe.mandarin.menu.ui.components.MenuList
import com.mandarinkafe.mandarin.menu.ui.components.MenuTopBar
import com.mandarinkafe.mandarin.menu.ui.components.MySearchBar
import com.mandarinkafe.mandarin.menu.ui.components.tabs.CategoryTabsRow
import com.mandarinkafe.mandarin.menu.ui.components.tabs.SubCategoryTabsRow
import kotlinx.coroutines.launch

@Composable
fun MenuContentScreen(
    menuItems: List<MenuRVItem>,
    filteredMenuItems: List<MenuRVItem>,
    listState: LazyListState,
    selectedTabIndex: Int,
    selectedSubTabIndex: Int,
    selectedBannerIndex: Int,
    onEvent: (Event) -> Unit
) {
    val categories = menuItems.filterIsInstance<MenuRVItem.HeaderItem>()
    val categoriesNames = categories.map { it.categoryName }
    val coroutineScope = rememberCoroutineScope()
    val isTopPartVisible by remember {
        derivedStateOf {
            listState.firstVisibleItemScrollOffset == 0
        }
    }

    val handleBannerClick = { targetName: String ->
        coroutineScope.launch {
            onEvent(
                Event.BannerClick(targetName)
            )
        }
    }
    // Отслеживание изменения selectedBannerIndex и скролл при обновлении
    LaunchedEffect(selectedBannerIndex) {
        if (selectedBannerIndex >= 0) {
            listState.scrollToItem(selectedBannerIndex)
        }
    }

    // Отслеживание скролла для обновления активного таба
    LaunchedEffect(listState) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo }
            .collect { visibleItems ->
                visibleItems.firstOrNull { it.offset >= 0 }?.index?.let { index ->
                    (menuItems.getOrNull(index) as? MenuRVItem.MealItem)?.let { mealItem ->
                        val parentCategory = menuItems
                            .takeWhile { it !== mealItem }
                            .lastOrNull { it is MenuRVItem.HeaderItem } as? MenuRVItem.HeaderItem

                        parentCategory?.let { category ->
                            val newIndex = categoriesNames.indexOf(category.categoryName)
                            if (newIndex != selectedTabIndex) {
                                onEvent(Event.ScrollToCategory(newIndex))
                            }
                            // Ищем подкатегорию
                            val parentSubCategory = menuItems
                                .takeWhile { it !== mealItem }
                                .lastOrNull { it is MenuRVItem.SubHeaderItem } as? MenuRVItem.SubHeaderItem

                            parentSubCategory?.let { subCategory ->
                                val newSubIndex =
                                    parentCategory.subCategoriesNames?.indexOf(subCategory.categoryName)
                                        ?: -1
                                if (newSubIndex != selectedSubTabIndex) {
                                    onEvent(Event.ScrollToSubCategory(newSubIndex))
                                }
                            }
                        }
                    }
                }
            }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Colors.AppBlack)
    ) {
        AnimatedVisibility(
            visible = isTopPartVisible,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            // Эта часть экрана видна только до начала скролла
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                MenuTopBar()
                MySearchBar(
                    filteredMenuItems = filteredMenuItems,
                    onSearch = { text -> onEvent(Event.SearchMealsByText(text)) },
                    onAddToCart = { meal -> onEvent(Event.AddToCart(meal)) },
                    onToggleFavorite = { meal -> onEvent(Event.ToggleFavorite(meal)) },
                    onRemoveFromCart = { meal -> onEvent(Event.RemoveFromCart(meal)) },
                    onCustomizeClick = { meal -> onEvent(Event.OpenMealCustomization(meal)) },
                    onClearSearch = { onEvent(Event.ClearSearchInput) }
                )
                BannerCarousel(onBannerClick = handleBannerClick)
            }
        }

        // Табы-категории, видно всегда
        CategoryTabsRow(
            categories = categories,
            selectedTabIndex = selectedTabIndex,
            onTabSelected = { index ->
                onEvent(Event.ScrollToCategory(index))
                coroutineScope.launch {
                    val targetIndex = menuItems.indexOfFirst {
                        it is MenuRVItem.HeaderItem && it.categoryName == categories[index].categoryName
                    }
                    if (targetIndex >= 0) {
                        listState.scrollToItem(index = targetIndex, scrollOffset = 1)
                    }
                }
            }
        )

        if (selectedTabIndex >= 0) {
            val currentSubCategories = categories[selectedTabIndex].subCategoriesNames

            if (!currentSubCategories.isNullOrEmpty()) {
                AnimatedVisibility(
                    visible = currentSubCategories.isNotEmpty(),
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {

                    // Табы-подкатегории, появляются при наличии в текущей категории
                    SubCategoryTabsRow(
                        categories = currentSubCategories,
                        selectedTabIndex = selectedSubTabIndex,
                        onTabSelected = { index ->
                            onEvent(Event.ScrollToSubCategory(index))
                            coroutineScope.launch {
                                val targetIndex = menuItems.indexOfFirst {
                                    it is MenuRVItem.SubHeaderItem && it.categoryName == currentSubCategories[index]
                                }
                                if (targetIndex >= 0) {
                                    listState.scrollToItem(index = targetIndex, scrollOffset = 1)
                                }
                            }
                        }
                    )
                }
            }
        }

        // Основное меню
        MenuList(
            menuItems = menuItems,
            listState = listState,
            modifier = Modifier.weight(1f),
            onToggleFavorite = { meal -> onEvent(Event.ToggleFavorite(meal)) },
            onAddToCart = { meal -> onEvent(Event.AddToCart(meal)) },
            onRemoveFromCart = { meal -> onEvent(Event.RemoveFromCart(meal)) },
            onCustomizeClick = { meal -> onEvent(Event.OpenMealCustomization(meal)) }
        )
    }
}
