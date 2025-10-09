package com.mandarinkafe.mandarin.util.presentation.ui.components

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.Switch
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
fun SwitchWithTextRow(
    modifier: Modifier = Modifier,
    value: Boolean,
    onValueChange: (Boolean) -> Unit,
    @StringRes textRes: Int
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .toggleable(
                value = value,
                role = Role.Switch,
                onValueChange = { onValueChange(it) }
            ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Switch(
            checked = value,
            onCheckedChange = null
        )
        Text(
            text = stringResource(textRes),
            style = Typography.RegularLightTextStyle,
            color = Colors.White,
            modifier = Modifier.padding(
                start = Dimens.MarginSmall8,
            ),
        )
    }
}