package com.mandarinkafe.mandarin.util.presentation.ui.components.textfields

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import com.mandarinkafe.mandarin.MR
import com.mandarinkafe.mandarin.core.presentation.theme.Colors
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.core.presentation.theme.Typography
import com.mandarinkafe.mandarin.util.presentation.ui.components.MaskVisualTransformation
import dev.icerock.moko.resources.compose.stringResource


@Composable
fun PhoneField(
    value: String,
    isError: Boolean,
    onValueChange: (String) -> Unit,
    colors: TextFieldColors? = null,
) {
    val defaultColors = TextFieldDefaults.colors(
        cursorColor = Colors.Orange,
        focusedTextColor = Colors.White,
        focusedContainerColor = Colors.LightGrey.copy(alpha = 0.2f),
        focusedIndicatorColor = Colors.White,
        unfocusedTextColor = Colors.White,
        unfocusedContainerColor = Colors.LightGrey.copy(alpha = 0.2f),
        unfocusedIndicatorColor = Colors.Transparent,
        errorIndicatorColor = Colors.Red,
        errorContainerColor = Colors.LightGrey.copy(alpha = 0.2f),
        disabledTextColor = Colors.White,
        disabledContainerColor = Colors.DarkGrey,
        disabledIndicatorColor = Colors.Transparent,
    )

    val mask = MaskVisualTransformation(stringResource(MR.strings.phone_mask))

    MyTextField(
        value = value,
        labelRes = MR.strings.your_phone,
        isError = isError,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
        onValueChange = { onValueChange(it) },
        visualTransformation = mask,

        placeholder = {
            Text(
                text = stringResource(MR.strings.phone_placeholder),
                style = Typography.RegularLightTextStyle
            )
        },
        prefix = {
            Text(
                text = stringResource(MR.strings.phone_prefix),
                style = Typography.RegularTextStyle
            )
        },
        colors = colors ?: defaultColors
    )

    Spacer(Modifier.height(Dimens.MarginSmall8))
}