package com.mandarinkafe.mandarin.menu.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.mandarinkafe.mandarin.core.ui.theme.Colors
import com.mandarinkafe.mandarin.menu.domain.models.MenuRVItem
import com.mandarinkafe.mandarin.menu.ui.MenuContract
import com.mandarinkafe.mandarin.menu.ui.components.MenuList
import com.mandarinkafe.mandarin.menu.ui.components.tabs.CategoryTabsRow
import com.mandarinkafe.mandarin.menu.ui.components.tabs.SubCategoryTabsRow
import com.mandarinkafe.mandarin.util.RVItem

@Composable
fun MenuContentScreen(
    menuItems: List<RVItem>,
    listState: LazyListState,
    selectedTabIndex: Int,
    selectedSubTabIndex: Int,
    onEvent: (MenuContract.Event) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Colors.AppBlack)
    ) {
        val categories = menuItems.filterIsInstance<MenuRVItem.HeaderItem>()

        // Категории
        CategoryTabsRow(
            categories = menuItems.filterIsInstance<MenuRVItem.HeaderItem>(),
            selectedTabIndex = selectedTabIndex,
            onTabSelected = { index -> onEvent(MenuContract.Event.ScrollToCategory(categories[index].id)) }
        )

        // Подкатегории
        if (selectedTabIndex >= 0) {
            val subCategories = menuItems
                .filterIsInstance<MenuRVItem.HeaderItem>()[selectedTabIndex]
                .subCategoriesNames

            if (!subCategories.isNullOrEmpty()) {
                SubCategoryTabsRow(
                    categories = subCategories,
                    selectedTabIndex = selectedSubTabIndex,
                    onTabSelected = { index ->
                        onEvent(
                            MenuContract.Event.ScrollToSubCategory(
                                subCategories[index]
                            )
                        )
                    }
                )
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
