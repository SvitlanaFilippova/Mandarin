package com.mandarinkafe.mandarin.util.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import com.mandarinkafe.mandarin.core.presentation.theme.Colors
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.core.presentation.theme.Typography

@Composable
fun RadiobuttonWithTextRow(
    label: String,
    selected: Boolean,
    onItemSelected: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Transparent)
            .padding(vertical = Dimens.MarginSuperSmall2)
            .toggleable(
                value = selected,
                onValueChange = { onItemSelected() },
                role = Role.RadioButton
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        RadioButton(
            modifier = Modifier.padding(horizontal = Dimens.Margin12),
            colors = RadioButtonDefaults.colors(selectedColor = Colors.Orange),
            selected = selected,
            onClick = null // обработка клика происходит в Row
        )

        Text(
            modifier = Modifier.padding(horizontal = Dimens.MarginSmall8),
            text = label,
            style = Typography.RegularLightTextStyle,
            color = Colors.White
        )

    }
}

