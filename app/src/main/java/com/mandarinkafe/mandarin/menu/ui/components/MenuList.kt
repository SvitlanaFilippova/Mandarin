package com.mandarinkafe.mandarin.menu.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.mandarinkafe.mandarin.core.ui.RVItem
import com.mandarinkafe.mandarin.core.ui.theme.Dimens
import com.mandarinkafe.mandarin.core.ui.theme.Typography
import com.mandarinkafe.mandarin.menu.domain.models.MenuRVItem

@Composable
fun MenuList(menuItems: List<RVItem>, listState: LazyListState, modifier: Modifier) {
    LazyColumn(
        state = listState,
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(Dimens.MarginSmall8)
    ) {
        items(menuItems) { item ->
            when (item) {
                is MenuRVItem.HeaderItem -> Text(
                    text = item.categoryName,
                    style = Typography.MealTitleStyle,
                    modifier = Modifier.padding(
                        start = Dimens.MarginSmall8,
                        top = Dimens.MarginSmall8
                    )
                )

                is MenuRVItem.SubHeaderItem -> Text(
                    text = item.categoryName,
                    style = Typography.MealTitleStyle,
                    modifier = Modifier.padding(
                        start = Dimens.MarginSmall8,
                        bottom = Dimens.MarginSmall8
                    )
                )

                is MenuRVItem.MealItem -> MenuMealItem(meal = item.meal)
            }
        }
    }
}