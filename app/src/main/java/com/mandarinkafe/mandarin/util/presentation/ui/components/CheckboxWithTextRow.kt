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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import com.mandarinkafe.mandarin.core.presentation.theme.Colors
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.core.presentation.theme.Typography

@Composable
fun CheckboxWithTextRow(
    checked: Boolean,
    labelRes: Int? = null,
    text: String? = null,
    onCheckedChange: (Boolean) -> Unit
) {

    val displayText = text ?: labelRes?.let { stringResource(it) } ?: ""

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .toggleable(
                value = checked,
                onValueChange = onCheckedChange,
                role = Role.Checkbox
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
            text = displayText,
            modifier = Modifier.padding(start = Dimens.MarginSmall8),
            style = Typography.RegularLightTextStyle,
            color = Colors.White
        )
    }
}