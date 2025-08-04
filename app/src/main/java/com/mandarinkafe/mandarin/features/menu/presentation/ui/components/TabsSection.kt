package com.mandarinkafe.mandarin.features.menu.presentation.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.runtime.Composable
import com.mandarinkafe.mandarin.features.menu.presentation.models.MenuItem
import com.mandarinkafe.mandarin.features.menu.presentation.ui.components.categorytabs.CategoryTabsRow
import com.mandarinkafe.mandarin.features.menu.presentation.ui.components.categorytabs.SubCategoryTabsRow

@Composable
fun TabsSection(
    categories: List<MenuItem.HeaderItem>,
    selectedTabIndex: Int,
    selectedSubTabIndex: Int,
    onTabSelected: (Int) -> Unit,
    onSubTabSelected: (Int, List<String>) -> Unit,
    onSearchClick: () -> Unit,
) {
    CategoryTabsRow(
        categories = categories,
        selectedTabIndex = selectedTabIndex,
        onTabSelected = { index -> onTabSelected(index) },
        onSearchClick = onSearchClick
    )

    if (selectedTabIndex >= 0) {
        val currentSubCategories = categories[selectedTabIndex].subCategoriesNames

        // Табы-подкатегории, появляются при наличии в текущей категории
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
                        onSubTabSelected(index, currentSubCategories)
                    }
                )
            }
        }
    }
}