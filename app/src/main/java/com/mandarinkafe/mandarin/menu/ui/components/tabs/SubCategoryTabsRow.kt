package com.mandarinkafe.mandarin.menu.ui.components.tabs

import androidx.compose.material3.ScrollableTabRow
import androidx.compose.runtime.Composable
import com.mandarinkafe.mandarin.core.ui.theme.Colors
import com.mandarinkafe.mandarin.core.ui.theme.Dimens

@Composable
fun SubCategoryTabsRow(
    categories: List<String>,
    selectedTabIndex: Int,
    onTabSelected: (Int) -> Unit
) {
    ScrollableTabRow(
        containerColor = Colors.AppBlack,
        edgePadding = Dimens.ZeroDp0,
        selectedTabIndex = selectedTabIndex,
        indicator = { }
    ) {
        categories.forEachIndexed { index, category ->
            SubCategoryTabItem(
                category = category,
                isSelected = selectedTabIndex == index,
                onClick = { onTabSelected(index) }
            )
        }
    }
}