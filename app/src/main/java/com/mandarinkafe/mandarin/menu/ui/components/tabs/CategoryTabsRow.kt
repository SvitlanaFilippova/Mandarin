package com.mandarinkafe.mandarin.menu.ui.components.tabs

import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.TabRowDefaults.SecondaryIndicator
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.mandarinkafe.mandarin.core.ui.theme.Colors
import com.mandarinkafe.mandarin.core.ui.theme.Dimens
import com.mandarinkafe.mandarin.menu.domain.models.MenuRVItem

@Composable
fun CategoryTabsRow(
    categories: List<MenuRVItem.HeaderItem>,
    selectedTabIndex: Int,
    onTabSelected: (Int) -> Unit
) {
    ScrollableTabRow(
        containerColor = Colors.AppBlack,
        selectedTabIndex = selectedTabIndex,
        edgePadding = Dimens.ZeroDp0,
        indicator = { tabPositions ->
            if (selectedTabIndex >= 0) {
                SecondaryIndicator(
                    Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                    color = Colors.Orange
                )
            }
        }
    ) {
        categories.forEachIndexed { index, category ->
            CategoryTabItem(
                category = category,
                isSelected = selectedTabIndex == index,
                onClick = { onTabSelected(index) }
            )
        }
    }
}