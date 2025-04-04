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
import com.mandarinkafe.mandarin.menu.ui.MenuContract
import com.mandarinkafe.mandarin.menu.ui.components.BannerCarousel
import com.mandarinkafe.mandarin.menu.ui.components.MenuList
import com.mandarinkafe.mandarin.menu.ui.components.MenuTopBar
import com.mandarinkafe.mandarin.menu.ui.components.tabs.CategoryTabsRow
import com.mandarinkafe.mandarin.menu.ui.components.tabs.SubCategoryTabsRow
import com.mandarinkafe.mandarin.util.RVItem
import kotlinx.coroutines.launch

@Composable
fun MenuContentScreen(
    menuItems: List<RVItem>,
    listState: LazyListState,
    selectedTabIndex: Int,
    selectedSubTabIndex: Int,
    onEvent: (MenuContract.Event) -> Unit
) {

    val categories = menuItems.filterIsInstance<MenuRVItem.HeaderItem>()
    val categoriesNames = categories.map { it.categoryName }
    val coroutineScope = rememberCoroutineScope()
    val isTopPartVisible by remember {
        derivedStateOf { listState.firstVisibleItemIndex == 0 }
    }

    val handleBannerClick = { targetId: String ->
        coroutineScope.launch {
            val targetIndex = menuItems.indexOfFirst { item ->
                when (item) {
                    is MenuRVItem.HeaderItem -> item.id == targetId
                    is MenuRVItem.SubHeaderItem -> item.id == targetId
                    is MenuRVItem.MealItem -> item.meal.id == targetId
                    else -> false
                }
            }.takeIf { it >= 0 } ?: 0
            listState.scrollToItem(targetIndex)
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
                                onEvent(MenuContract.Event.ScrollToCategory(newIndex))
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
                                    onEvent(MenuContract.Event.ScrollToSubCategory(newSubIndex))
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
        ) { // Эта часть экрана видна только до начала скролла
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                MenuTopBar()
                BannerCarousel(onBannerClick = handleBannerClick)
            }
        }

        CategoryTabsRow(
            categories = categories,
            selectedTabIndex = selectedTabIndex,
            onTabSelected = { index ->
                onEvent(MenuContract.Event.ScrollToCategory(index))
                coroutineScope.launch {
                    val targetIndex = menuItems.indexOfFirst {
                        it is MenuRVItem.HeaderItem && it.categoryName == categories[index].categoryName
                    }
                    if (targetIndex >= 0) {
                        listState.scrollToItem(targetIndex)
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
                    SubCategoryTabsRow(
                        categories = currentSubCategories,
                        selectedTabIndex = selectedSubTabIndex,
                        onTabSelected = { index ->
                            onEvent(
                                MenuContract.Event.ScrollToCategory(index)
                            )
                            coroutineScope.launch {
                                val targetIndex = menuItems.indexOfFirst {
                                    it is MenuRVItem.SubHeaderItem && it.categoryName == currentSubCategories[index]
                                }
                                if (targetIndex >= 0) {
                                    listState.scrollToItem(targetIndex)
                                }
                            }
                        }
                    )
                }
            }
        }

        MenuList(
            menuItems = menuItems,
            listState = listState,
            modifier = Modifier.weight(1f),
            onToggleFavorite = { mealId -> onEvent(MenuContract.Event.ToggleFavorite(mealId)) },
            onAddToCart = { mealId -> onEvent(MenuContract.Event.AddToCart(mealId)) },
            onRemoveFromCart = { mealId -> onEvent(MenuContract.Event.RemoveFromCart(mealId)) }
        )
    }
}
