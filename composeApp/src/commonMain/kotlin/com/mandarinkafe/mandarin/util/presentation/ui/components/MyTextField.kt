package com.mandarinkafe.mandarin.util.presentation.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.VisualTransformation
import com.mandarinkafe.mandarin.MR
import com.mandarinkafe.mandarin.core.presentation.theme.Colors
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.core.presentation.theme.Typography
import dev.icerock.moko.resources.StringResource
import dev.icerock.moko.resources.compose.painterResource
import dev.icerock.moko.resources.compose.stringResource

@Composable
fun MyTextField(
    modifier: Modifier = Modifier,
    value: String,
    labelRes: StringResource,
    isError: Boolean = false,
    onValueChange: (String) -> Unit = {},
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    enabled: Boolean = true,
    minLines: Int = 1,
    prefix: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    placeholder: @Composable (() -> Unit)? = null,
    colors: TextFieldColors? = null,
) {
    val colorsFinal = colors ?: TextFieldDefaults.colors(
        cursorColor = Colors.Orange,
        focusedTextColor = Colors.White,
        focusedContainerColor = Colors.DarkGrey,
        focusedIndicatorColor = Colors.White,
        unfocusedTextColor = Colors.White,
        unfocusedContainerColor = Colors.DarkGrey,
        unfocusedIndicatorColor = Colors.Transparent,
        errorIndicatorColor = Colors.Red,
        errorContainerColor = Colors.DarkGrey,
        disabledTextColor = Colors.White,
        disabledContainerColor = Colors.DarkGrey,
        disabledIndicatorColor = Colors.Transparent,
    )
    TextField(
        modifier = modifier
            .fillMaxWidth(),
        value = value,
        enabled = enabled,
        minLines = minLines,
        shape = RoundedCornerShape(Dimens.CornerRadius8),
        onValueChange = { onValueChange(it) },
        colors = colorsFinal,
        placeholder = placeholder,
        label = {
            Text(
                text = stringResource(resource = labelRes),
                style = Typography.RegularLightTextStyle
            )
        },
        isError = isError,
        keyboardOptions = keyboardOptions.copy(imeAction = ImeAction.Done),
        visualTransformation = visualTransformation,
        trailingIcon = {
            if (enabled && value.isNotEmpty()) {
                IconButton(onClick = { onValueChange("") }) {
                    Icon(
                        painter = painterResource(MR.images.ic_close),
                        contentDescription = stringResource(MR.strings.clear_text),
                        tint = Colors.LightGrey
                    )
                }
            }
        },
        leadingIcon = leadingIcon,
        prefix = prefix
    )
}


