package com.mandarinkafe.mandarin.menu.ui.components.meal_details_bottom_sheet.pizza_ads

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.mandarinkafe.mandarin.core.ui.theme.Dimens
import com.mandarinkafe.mandarin.menu.domain.models.Meal
import com.mandarinkafe.mandarin.menu.ui.components.meal_details_bottom_sheet.AddsItem
import com.mandarinkafe.mandarin.menu.ui.view_model.MenuContract.Event

@Composable
fun AddsList(
    addsItems: List<Meal>?,
    listState: LazyListState,
    modifier: Modifier,
    onEvent: (Event) -> Unit,
) {
    if (addsItems != null)
        LazyColumn(
            state = listState,
            modifier = modifier.padding(vertical = Dimens.MarginSmall8),
            verticalArrangement = Arrangement.spacedBy(Dimens.MarginSmall8)
        ) {
            itemsIndexed(addsItems) { index, item ->
                AddsItem(add = item, onEvent = onEvent)

            }
        }
}