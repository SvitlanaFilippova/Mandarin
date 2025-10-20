package com.mandarinkafe.mandarin.util.presentation.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.mandarinkafe.mandarin.core.presentation.theme.Colors
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.core.presentation.theme.Typography

@Composable
fun CheckboxWithTextRow(
    checked: Boolean,
    modifier: Modifier = Modifier,
    text: String,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .toggleable(
                value = checked,
                onValueChange = onCheckedChange,
                role = androidx.compose.ui.semantics.Role.Checkbox
            ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Checkbox(
            modifier = Modifier.padding(horizontal = Dimens.Margin12),
            checked = checked,
            colors = CheckboxDefaults.colors(checkedColor = Colors.Orange),
            onCheckedChange = null
        )
        Text(
            text = text,
            modifier = Modifier.padding(start = Dimens.MarginSmall8),
            style = Typography.RegularLightTextStyle,
            color = Colors.White
        )
    }
}


