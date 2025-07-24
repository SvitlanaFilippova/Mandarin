package com.mandarinkafe.mandarin.features.address.presentation.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import com.mandarinkafe.mandarin.core.presentation.theme.Typography

@Composable
fun AddressSearchTextField(
    modifier: Modifier = Modifier,
    query: String,
    labelRes: Int,
    enabled: Boolean,
    autoFocus: Boolean = false,
    onClear: () -> Unit,
    onQueryChange: (String) -> Unit? = { },
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
        value = query,
        isError = false,
        singleLine = true,
        shape = RoundedCornerShape(Dimens.CornerRadius8),
        onValueChange = { onQueryChange(it) },
        enabled = enabled,
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

        label = {
            Text(
                text = stringResource(id = labelRes),
                style = Typography.RegularLightTextStyle
            )
        },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = stringResource(id = R.string.clear_text),
                        tint = Colors.LightGrey
                    )
                }
            }
        }
    )
}