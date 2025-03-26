package com.mandarinkafe.mandarin.menu.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mandarinkafe.mandarin.core.ui.theme.Dimens
import com.mandarinkafe.mandarin.core.ui.theme.Typography
import com.mandarinkafe.mandarin.menu.domain.models.MenuRVItem
import com.mandarinkafe.mandarin.util.RVItem

@Composable
fun MenuList(menuItems: List<RVItem>, listState: LazyListState, modifier: Modifier) {
    LazyColumn(
        state = listState,
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(Dimens.MarginSmall8)
    ) {
        itemsIndexed(menuItems) { index, item ->
            when (item) {
                is MenuRVItem.HeaderItem -> Text(
                    text = item.categoryName,
                    style = Typography.MenuCategoryStyle,
                    modifier = Modifier.padding(
                        start = Dimens.MarginSmall8,
                        top = Dimens.MarginBig32
                    )
                )

                is MenuRVItem.SubHeaderItem -> {
                    val previousItem = if (index > 0) menuItems[index - 1] else null
                    val hasHeaderBefore = previousItem is MenuRVItem.HeaderItem

                    Text(
                        text = item.categoryName,
                        style = Typography.MenuSubCategoryStyle,
                        modifier = Modifier.padding(
                            start = Dimens.MarginSmall8,
                            top = if (!hasHeaderBefore) Dimens.MarginStandard16 else 0.dp,
                            bottom = Dimens.MarginSmall8
                        )
                    )
                }

                is MenuRVItem.MealItem -> MenuMealItem(meal = item.meal)
            }
        }
    }
}