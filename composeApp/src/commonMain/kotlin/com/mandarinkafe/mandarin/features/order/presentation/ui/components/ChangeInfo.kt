package com.mandarinkafe.mandarin.features.order.presentation.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import com.mandarinkafe.mandarin.MR
import com.mandarinkafe.mandarin.core.presentation.theme.Colors
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.core.presentation.theme.Typography
import dev.icerock.moko.resources.compose.stringResource

@Composable
fun ChangeInfo(
    noChange: Boolean,
    onNoChangeToggled: (Boolean) -> Unit,
    changeAmount: String,
    onChangeEntered: (String) -> Unit,
) {
    // Свич "без сдачи"
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            modifier = Modifier
                .weight(1f)
                .height(Dimens.BigButtonWithTextHeight)
                .toggleable(
                    value = noChange,
                    role = Role.Switch,
                    onValueChange = { onNoChangeToggled(it) }
                ),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Switch(
                checked = noChange,
                onCheckedChange = null
            )
            Text(
                text = stringResource(MR.strings.no_change_needed),
                style = Typography.RegularLightTextStyle,
                color = Colors.White,
                modifier = Modifier.padding(
                    start = Dimens.MarginSmall8,
                ),
            )
        }
        Spacer(modifier = Modifier.size(Dimens.MarginStandard16))

        // Текстовое поле для ввода купюры, с которой нужна сдача
        if (!noChange) {
            TextField(
                modifier = Modifier
                    .weight(1f),
                value = changeAmount,
                shape = RoundedCornerShape(Dimens.CornerRadius8),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done
                ),
                placeholder = {
                    Text(
                        text = stringResource(MR.strings.payment_default_value),
                        style = Typography.RegularLightTextStyle
                    )
                },
                prefix = {
                    Text(
                        modifier = Modifier.padding(end = Dimens.MarginSmall8),
                        text = stringResource(MR.strings.payment_change),
                        style = Typography.RegularTextStyle
                    )
                },
                suffix = {
                    Text(
                        text = stringResource(MR.strings.rub),
                        style = Typography.RegularLightTextStyle
                    )
                },
                onValueChange = { onChangeEntered(it) },
                colors = TextFieldDefaults.colors(
                    cursorColor = Colors.Orange,
                    focusedTextColor = Colors.White,
                    focusedContainerColor = Colors.DarkGrey,
                    focusedIndicatorColor = Colors.Orange,
                    unfocusedTextColor = Colors.White,
                    unfocusedContainerColor = Colors.DarkGrey,
                    unfocusedIndicatorColor = Colors.Transparent,
                ),
            )
        }
    }
}