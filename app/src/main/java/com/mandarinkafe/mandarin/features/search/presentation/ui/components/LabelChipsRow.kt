package com.mandarinkafe.mandarin.features.search.presentation.ui.components

import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.mandarinkafe.mandarin.features.search.presentation.model.LabelUiModel
import com.mandarinkafe.mandarin.util.Constants.MAX_LINES_FOR_LABEL_FLOW_ROW

@Composable
fun LabelChipsRow(
    labels: List<LabelUiModel>,
    checkedLabels: List<String>,
    onLabelClick: (String, Boolean) -> Unit
) {
    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        maxLines = MAX_LINES_FOR_LABEL_FLOW_ROW,
    ) {
        labels.forEach {
            MenuFilterChip(
                label = it,
                selected = checkedLabels.contains(it.name),
                onLabelClick = onLabelClick
            )
        }
    }
}
