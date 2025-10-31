package com.mandarinkafe.mandarin.features.search.presentation.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.mandarinkafe.mandarin.MR
import com.mandarinkafe.mandarin.core.presentation.theme.Colors
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.features.search.presentation.model.LabelUiModel
import dev.icerock.moko.resources.compose.painterResource
import dev.icerock.moko.resources.compose.stringResource

@Composable
fun MenuFilterChip(
    label: LabelUiModel,
    selected: Boolean,
    onLabelClick: (String, Boolean) -> Unit,
) {
    val textColor = if (selected) Colors.White else Colors.LightGrey
    Box(modifier = Modifier.padding(horizontal = Dimens.MarginSuperSmall4)) {
        FilterChip(
            onClick = { onLabelClick(label.name, !selected) },
            label = {
                Text(text = label.name, color = textColor)
            },
            selected = selected,
            colors = FilterChipDefaults.filterChipColors()
                .copy(selectedContainerColor = label.backgroundColor),

            leadingIcon = if (selected) {
                {
                    Icon(
                        painter = painterResource(MR.images.ic_check),
                        contentDescription = stringResource(MR.strings.is_selected),
                        modifier = Modifier.size(FilterChipDefaults.IconSize)
                    )
                }
            } else {
                null
            },
        )
    }
}