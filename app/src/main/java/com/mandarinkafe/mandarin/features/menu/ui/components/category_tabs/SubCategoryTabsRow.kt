package com.mandarinkafe.mandarin.features.menu.ui.components.category_tabs

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.mandarinkafe.mandarin.core.ui.theme.Colors
import com.mandarinkafe.mandarin.core.ui.theme.Dimens

@Composable
fun SubCategoryTabsRow(
    categories: List<String>,
    selectedTabIndex: Int,
    onTabSelected: (Int) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
    ScrollableTabRow(
        containerColor = Colors.AppBlack,
        edgePadding = Dimens.ZeroDp0,
        selectedTabIndex = selectedTabIndex,
        indicator = { },
        divider = { },
    ) {
        categories.forEachIndexed { index, category ->
            SubCategoryTabItem(
                category = category,
                isSelected = selectedTabIndex == index,
                onClick = { onTabSelected(index) }
            )
        }
    }
        HorizontalDivider(
            modifier = Modifier.fillMaxWidth(),
            thickness = Dimens.DividerHeight1,
            color = Colors.LightGrey.copy(alpha = 0.3f)
        )
    }
}