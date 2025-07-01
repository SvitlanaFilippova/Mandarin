package com.mandarinkafe.mandarin.features.mealdetails.presentation.ui.components.pizza_ads

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.InputChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.mandarinkafe.mandarin.R
import com.mandarinkafe.mandarin.core.presentation.theme.Colors
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens

@Composable
fun ChosenItemChip(
    itemName: String,
    onClick: () -> Unit,
) {

    Box(modifier = Modifier.padding(horizontal = Dimens.MarginSuperSmall4)) {
        InputChip(
            onClick = onClick,
            label = {
                Text(text = itemName, color = Colors.LightGrey)
            },
            selected = true,
            colors = FilterChipDefaults.filterChipColors()
                .copy(selectedContainerColor = Colors.DarkGrey),

            trailingIcon =
                {
                    Icon(
                        imageVector = Icons.Filled.Clear,
                        contentDescription = stringResource(id = R.string.cancel_choice),
                        modifier = Modifier.size(FilterChipDefaults.IconSize)
                    )
                }
        )
    }
}
