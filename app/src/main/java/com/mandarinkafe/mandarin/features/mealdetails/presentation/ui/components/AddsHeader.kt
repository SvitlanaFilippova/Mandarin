package com.mandarinkafe.mandarin.features.mealdetails.presentation.ui.components

import androidx.compose.runtime.Composable
import com.mandarinkafe.mandarin.features.mealdetails.presentation.ui.components.additionals.AddsCategoryTabsRow

@Composable
fun AddsHeader(
    selectedTabIndex: Int,
    categories: List<String>,
    onTabSelected: (Int) -> Unit
) {
    // Категории добавок
    AddsCategoryTabsRow(
        categories = categories,
        selectedTabIndex = selectedTabIndex,
        onTabSelected = { index ->
            onTabSelected(index)
        }
    )

}
