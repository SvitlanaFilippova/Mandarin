package com.mandarinkafe.mandarin.features.menu.ui.components.category_tabs

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.TabRowDefaults.SecondaryIndicator
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.mandarinkafe.mandarin.core.ui.theme.Colors
import com.mandarinkafe.mandarin.core.ui.theme.Dimens
import com.mandarinkafe.mandarin.features.menu.ui.models.MenuItem

@Composable
fun CategoryTabsRow(
    categories: List<MenuItem.HeaderItem>,
    selectedTabIndex: Int,
    onTabSelected: (Int) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        ScrollableTabRow(
            containerColor = Colors.AppBlack,
            selectedTabIndex = selectedTabIndex,
            edgePadding = Dimens.ZeroDp0,
            indicator = { tabPositions ->
                if (selectedTabIndex >= 0) {
                    SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                        color = Colors.Orange,
                        height = Dimens.TabActivatedIndicatorHeight2
                    )
                }
            }, divider = { }
        ) {
            categories.forEachIndexed { index, category ->
                CategoryTabItem(
                    category = category,
                    isSelected = selectedTabIndex == index,
                    onClick = { onTabSelected(index) }
                )
            }
        }

        HorizontalDivider(
            modifier = Modifier.fillMaxWidth(),
            thickness = Dimens.DividerHeight1,
            color = Colors.Grey.copy(alpha = 0.3f)
        )
    }
}