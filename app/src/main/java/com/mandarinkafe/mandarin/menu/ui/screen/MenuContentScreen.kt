package com.mandarinkafe.mandarin.menu.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.mandarinkafe.mandarin.core.ui.theme.Colors
import com.mandarinkafe.mandarin.menu.domain.models.Meal
import com.mandarinkafe.mandarin.menu.domain.models.MenuRVItem
import com.mandarinkafe.mandarin.menu.ui.components.MenuList
import com.mandarinkafe.mandarin.menu.ui.components.tabs.CategoryTabsRow
import com.mandarinkafe.mandarin.menu.ui.components.tabs.SubCategoryTabsRow
import com.mandarinkafe.mandarin.util.RVItem

@Composable
fun MenuContentScreen(
    menuItems: List<RVItem>,
    selectedTabIndex: Int,
    selectedSubTabIndex: Int,
    onCategorySelected: (Int) -> Unit,
    onSubCategorySelected: (Int) -> Unit,
    onToggleFavorite: (Meal) -> Unit,
    onAddToCart: (Meal) -> Unit,
    onRemoveFromCart: (Meal) -> Unit,
) {
    val listState = rememberLazyListState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Colors.AppBlack)
    ) {
        CategoryTabsRow(
            categories = menuItems.filterIsInstance<MenuRVItem.HeaderItem>(),
            selectedTabIndex = selectedTabIndex,
            onTabSelected = { index -> onCategorySelected(index) }
        )

        if (selectedTabIndex >= 0) {
            val subCategories = menuItems
                .filterIsInstance<MenuRVItem.HeaderItem>()[selectedTabIndex]
                .subCategoriesNames

            if (!subCategories.isNullOrEmpty()) {
                SubCategoryTabsRow(
                    categories = subCategories,
                    selectedTabIndex = selectedSubTabIndex,
                    onTabSelected = { index -> onSubCategorySelected(index) }
                )
            }
        }

        MenuList(
            menuItems = menuItems,
            listState = listState,
            modifier = Modifier.weight(1f),
            onToggleFavorite = onToggleFavorite,
            onAddToCart = onAddToCart,
            onRemoveFromCart = onRemoveFromCart
        )
    }
}
