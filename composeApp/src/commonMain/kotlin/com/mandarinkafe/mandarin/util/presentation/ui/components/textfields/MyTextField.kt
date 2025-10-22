package com.mandarinkafe.mandarin.util.presentation.ui.components.textfields

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.VisualTransformation
import com.mandarinkafe.mandarin.MR
import com.mandarinkafe.mandarin.core.presentation.theme.Colors
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.core.presentation.theme.Typography
import com.mandarinkafe.mandarin.util.Constants.DELAY_FOR_FOCUS_ENABLING
import dev.icerock.moko.resources.StringResource
import dev.icerock.moko.resources.compose.painterResource
import dev.icerock.moko.resources.compose.stringResource
import kotlinx.coroutines.delay

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
    autofocus: Boolean = false,
    prefix: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    placeholder: @Composable (() -> Unit)? = null,
    colors: TextFieldColors? = null,
) {
    val focusRequester = remember { FocusRequester() }

    var focusEnabled by remember { mutableStateOf(autofocus) } // изначально нельзя фокусироваться, если не передано autofocus=true

    // Разрешаем фокус только после небольшой задержки, когда всё отрисуется
    LaunchedEffect(Unit) {
        delay(DELAY_FOR_FOCUS_ENABLING) // иначе TextField успевает сам запросить фокус
        focusEnabled = true
    }

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

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clickable {
                if (focusEnabled) {
                    focusRequester.requestFocus()
                }
            }
    ) {
        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester)
                .focusProperties {
                    canFocus = focusEnabled
                },
            value = value,
            enabled = enabled,
            minLines = minLines,
            shape = RoundedCornerShape(Dimens.CornerRadius8),
            onValueChange = onValueChange,
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
}
