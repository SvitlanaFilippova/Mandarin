package com.mandarinkafe.mandarin.features.menu.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.mandarinkafe.mandarin.core.ui.theme.Dimens
import com.mandarinkafe.mandarin.core.ui.theme.Typography
import com.mandarinkafe.mandarin.features.menu.ui.models.MenuItem
import com.mandarinkafe.mandarin.util.Constants.MAX_LINES_FOR_COLLAPSED_EXPANDABLE_TEXT
import com.mandarinkafe.mandarin.util.ui.components.ExpandableText

@Composable
fun MenuSubHeaderItem(item: MenuItem.SubHeaderItem, hasHeaderBefore: Boolean) {
    var isDescriptionExpanded by remember {
        mutableStateOf(false)
    }
    Column(
        modifier = Modifier.padding(
            start = Dimens.MarginSmall8,
            top = if (!hasHeaderBefore) Dimens.MarginStandard16 else Dimens.MarginSuperSmall4,
            bottom = Dimens.MarginSmall8
        )
    ) {
        Text(
            text = item.categoryName,
            style = Typography.MenuSubCategoryStyle
        )
        if (item.description.isNotEmpty()) {
            ExpandableText(
                text = item.description,
                style = Typography.MealSmallTextStyle,
                isExpanded = isDescriptionExpanded,
                onClick = { isDescriptionExpanded = !isDescriptionExpanded },
                maxLinesCollapsed = MAX_LINES_FOR_COLLAPSED_EXPANDABLE_TEXT
            )
        }
    }
}