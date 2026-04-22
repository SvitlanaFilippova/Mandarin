package com.mandarinkafe.mandarin.util.presentation.ui.components.textfields

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import com.mandarinkafe.mandarin.core.presentation.theme.Colors
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.util.presentation.ui.components.TextFieldTrailingIcon

@Composable
fun SearchBarInputField(
    modifier: Modifier = Modifier,
    query: String,
    placeholderText: String,
    enabled: Boolean = true,
    autoFocus: Boolean = false,
    onQueryChange: (String) -> Unit = { },
    onClear: () -> Unit = { },
    leadingIcon: @Composable (() -> Unit)? = null,
) {
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    // Фокус и клавиатура сразу при отображении
    LaunchedEffect(Unit) {
        if (autoFocus) {
            focusRequester.requestFocus()
            keyboardController?.show()
        }
    }
    var isFocused by remember { mutableStateOf(false) }

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Dimens.MarginSuperSmall4)
    ) {
        TextField(
            modifier = Modifier.weight(1f).focusRequester(focusRequester)
                .onFocusChanged { focusState ->
                    isFocused = focusState.isFocused
                },
            shape = RoundedCornerShape(Dimens.CornerRadius8),
            value = query,
            onValueChange = onQueryChange,
            enabled = enabled,
            singleLine = true,
            colors = TextFieldDefaults.colors(
                disabledTextColor = Colors.White,
                disabledContainerColor = Colors.DarkGrey,
                disabledIndicatorColor = Colors.Transparent,
                cursorColor = Colors.Orange,
                focusedTextColor = Colors.White,
                focusedContainerColor = Colors.DarkGrey,
                focusedIndicatorColor = Colors.Orange,
                unfocusedTextColor = Colors.White,
                unfocusedContainerColor = Colors.DarkGrey,
                unfocusedIndicatorColor = Colors.Transparent,
            ),
            placeholder = {
                Text(
                    text = placeholderText,
                    color = Colors.White
                )
            },

            leadingIcon = leadingIcon,

            trailingIcon = {
                TextFieldTrailingIcon(
                    value = query,
                    onClear = onClear,
                )
            },
        )

        CloseKeyboardButton(isFocused)
    }
}



