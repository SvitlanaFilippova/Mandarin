package com.mandarinkafe.mandarin.menu.ui.components.meal_details_bottom_sheet.pizza_ads

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.mandarinkafe.mandarin.core.ui.theme.Dimens
import com.mandarinkafe.mandarin.menu.domain.models.MealAdditional

@Composable
fun AddsList(
    addsItems: List<MealAdditional>?,
    chosenAdds: List<MealAdditional>,
    listState: LazyListState,
    modifier: Modifier = Modifier,
    onCheckedChange: (Boolean, MealAdditional) -> Unit
) {
    if (!addsItems.isNullOrEmpty())
        LazyColumn(
            state = listState,
            modifier = modifier.padding(vertical = Dimens.MarginSmall8),
            verticalArrangement = Arrangement.spacedBy(Dimens.MarginSmall8)
        ) {
            itemsIndexed(addsItems) { index, item ->
                AddsItem(
                    add = item,
                    onCheckedChange = onCheckedChange,
                    isAdded = chosenAdds.contains(item)
                )

            }
        }
}