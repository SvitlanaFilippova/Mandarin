package com.mandarinkafe.mandarin.features.menu.presentation.ui.components.categorytabs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.mandarinkafe.mandarin.core.presentation.theme.Colors
import com.mandarinkafe.mandarin.features.menu.presentation.models.MenuItem

@Composable
fun TabsSection(
    headers: List<MenuItem.HeaderItem>,
    activeTabIndex: Int,
    activeSubTabIndex: Int,
    onCategorySelected: (Int) -> Unit,
    onSubCategorySelected: (Int) -> Unit,
) {
    Column(Modifier.background(Colors.AppBlack)) {
        CategoryTabsRow(
            categories = headers,
            selectedTabIndex = activeTabIndex,
            onTabSelected = { index -> onCategorySelected(index) },
        )

        val subCategoriesForActive =
            headers.getOrNull(activeTabIndex)?.subCategoriesNames.orEmpty()
        if (subCategoriesForActive.isNotEmpty()) {
            SubCategoryTabsRow(
                categories = subCategoriesForActive,
                selectedTabIndex = activeSubTabIndex,
                onTabSelected = { subIndex -> onSubCategorySelected(subIndex) }
            )
        }
    }
}