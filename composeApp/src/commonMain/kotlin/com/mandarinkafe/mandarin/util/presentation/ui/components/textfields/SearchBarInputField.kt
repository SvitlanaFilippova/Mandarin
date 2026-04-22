package com.mandarinkafe.mandarin.util.presentation.ui.components.textfields

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import com.mandarinkafe.mandarin.MR
import com.mandarinkafe.mandarin.core.presentation.theme.Colors
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import dev.icerock.moko.resources.compose.painterResource
import dev.icerock.moko.resources.compose.stringResource

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

    TextField(
        modifier = modifier
            .fillMaxWidth()
            .focusRequester(focusRequester),
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
            DefaultTrailingIcon(
                value = query,
                onClick = onClear
            )
        },
    )
}


@Composable
private fun DefaultTrailingIcon(value: String, onClick: () -> Unit) {
    val focusManager = LocalFocusManager.current

    val onClick: () -> Unit = when {
        value.isNotEmpty() -> {
            {
                onClick()
            }
        }

        else -> {
            { focusManager.clearFocus() }
        }
    }
    IconButton(onClick = onClick) {
        Icon(
            painter = painterResource(MR.images.ic_close),
            contentDescription = stringResource(MR.strings.clear_text),
            tint = Colors.LightGrey
        )
    }
}
