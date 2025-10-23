package com.mandarinkafe.mandarin.features.mealdetails.presentation.ui.components.additionals

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.InputChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.mandarinkafe.mandarin.MR
import com.mandarinkafe.mandarin.core.presentation.theme.Colors
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import dev.icerock.moko.resources.compose.painterResource
import dev.icerock.moko.resources.compose.stringResource

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
                        painter = painterResource(MR.images.ic_close),
                        contentDescription = stringResource(MR.strings.cancel_choice),
                        modifier = Modifier.size(FilterChipDefaults.IconSize),
                        tint = Colors.LightGrey
                    )
                }
        )
    }
}
