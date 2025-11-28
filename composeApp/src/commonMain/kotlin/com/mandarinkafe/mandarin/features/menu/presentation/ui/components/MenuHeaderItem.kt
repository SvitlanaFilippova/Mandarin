package com.mandarinkafe.mandarin.features.menu.presentation.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.core.presentation.theme.Typography
import com.mandarinkafe.mandarin.features.menu.presentation.models.MenuItem
import com.mandarinkafe.mandarin.util.Constants.MAX_LINES_FOR_COLLAPSED_EXPANDABLE_TEXT
import com.mandarinkafe.mandarin.util.presentation.ui.components.ExpandableText

@Composable
fun MenuHeaderItem(item: MenuItem.HeaderItem) {
    var isDescriptionExpanded by remember {
        mutableStateOf(false)
    }
    Column(
        modifier = Modifier.padding(
            start = Dimens.MarginSmall8,
            top = Dimens.MarginBig32,
            bottom = Dimens.MarginSmall8
        )
    ) {
        Text(
            text = item.categoryName,
            style = Typography.MenuCategoryStyle,
        )
        if (item.description.isNotEmpty()) {
            ExpandableText(
                text = item.description,
                style = Typography.SmallLightTextStyle,
                isExpanded = isDescriptionExpanded,
                onClick = { isDescriptionExpanded = !isDescriptionExpanded },
                maxLinesCollapsed = MAX_LINES_FOR_COLLAPSED_EXPANDABLE_TEXT
            )
        }
    }
}
