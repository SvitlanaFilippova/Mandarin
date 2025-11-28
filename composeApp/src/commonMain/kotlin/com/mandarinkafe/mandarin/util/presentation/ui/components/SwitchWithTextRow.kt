package com.mandarinkafe.mandarin.util.presentation.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.mandarinkafe.mandarin.core.presentation.theme.Colors
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.core.presentation.theme.Typography

@Composable
fun SwitchWithTextRow(
    modifier: Modifier = Modifier,
    value: Boolean,
    onValueChange: (Boolean) -> Unit,
    text: String,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .toggleable(
                value = value,
                role = androidx.compose.ui.semantics.Role.Switch,
                onValueChange = { onValueChange(it) }
            ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Switch(
            checked = value,
            onCheckedChange = null
        )
        Text(
            text = text,
            style = Typography.RegularLightTextStyle,
            color = Colors.White,
            modifier = Modifier.padding(
                start = Dimens.MarginSmall8,
            ),
        )
    }
}


