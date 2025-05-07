package com.mandarinkafe.mandarin.search.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.mandarinkafe.mandarin.R
import com.mandarinkafe.mandarin.core.ui.theme.Colors
import com.mandarinkafe.mandarin.core.ui.theme.Dimens
import com.mandarinkafe.mandarin.util.Constants.MAX_LINES_FOR_FLOW_ROW

@Composable
fun LabelChipsRow(
    labels: List<String>,
    checkedLabels: List<String>,
    onLabelClick: (String, Boolean) -> Unit
) {

    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        maxLines = MAX_LINES_FOR_FLOW_ROW
    ) {
        labels.forEach {
            MenuFilterChip(
                label = it,
                selected = checkedLabels.contains(it),
                onLabelClick = onLabelClick
            )
        }
    }
}

@Composable
fun MenuFilterChip(
    label: String,
    selected: Boolean,
    onLabelClick: (String, Boolean) -> Unit
) {

    Box(modifier = Modifier.padding(horizontal = Dimens.MarginSuperSmall4)) {
        FilterChip(
            onClick = { onLabelClick(label, !selected) },
            label = {
                Text(label)
            },
            selected = selected,
            colors = FilterChipDefaults.filterChipColors()
                .copy(selectedContainerColor = Colors.OrangeTransparent75),

            leadingIcon = if (selected) {
                {
                    Icon(
                        imageVector = Icons.Filled.Done,
                        contentDescription = stringResource(id = R.string.is_selected),
                        modifier = Modifier.size(FilterChipDefaults.IconSize)
                    )
                }
            } else {
                null
            },
        )
    }
}