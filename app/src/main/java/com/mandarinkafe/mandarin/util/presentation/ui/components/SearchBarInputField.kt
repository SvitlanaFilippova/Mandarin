package com.mandarinkafe.mandarin.util.presentation.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
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
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import com.mandarinkafe.mandarin.R
import com.mandarinkafe.mandarin.core.presentation.theme.Colors
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens

@Composable
fun SearchBarInputField(
    modifier: Modifier = Modifier,
    query: String,
    placeholderRes: Int,
    enabled: Boolean = true,
    autoFocus: Boolean = false,
    onQueryChange: (String) -> Unit = { },
    onClear: () -> Unit = { },
    onDismiss: (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null
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
                text = stringResource(placeholderRes),
                color = Colors.White
            )
        },

        leadingIcon = leadingIcon,

        trailingIcon = {
            if (enabled) {
                if (query.isNotEmpty()) { // если в поле есть текст - очистить его
                    IconButton(onClick = { onClear() }) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = stringResource(id = R.string.clear_text),
                            tint = Colors.White
                        )
                    }
                } else {
                    onDismiss?.let {
                        IconButton(onClick = onDismiss) { // если поле пустое - возврат назад
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = stringResource(id = R.string.back),
                                tint = Colors.White
                            )
                        }
                    }
                }
            }
        },

        )
}