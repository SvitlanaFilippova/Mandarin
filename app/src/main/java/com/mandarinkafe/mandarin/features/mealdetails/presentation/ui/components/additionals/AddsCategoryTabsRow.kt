package com.mandarinkafe.mandarin.features.mealdetails.presentation.ui.components.additionals

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.mandarinkafe.mandarin.core.presentation.theme.Colors
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens


@Composable
fun AddsCategoryTabsRow(
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

@Composable
private fun SubCategoryTabItem(category: String, isSelected: Boolean, onClick: () -> Unit) {
    Tab(
        selected = isSelected,
        onClick = onClick,
        text = {
            Text(
                category,
                color = if (isSelected) Colors.Orange else Color.White
            )
        },
        selectedContentColor = Colors.Orange,
    )
}