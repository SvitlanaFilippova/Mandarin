package com.mandarinkafe.mandarin.features.cart.presentation.components

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import com.mandarinkafe.mandarin.R
import com.mandarinkafe.mandarin.core.presentation.theme.Colors
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.core.presentation.theme.Typography

@Composable
fun MealCommentTextField(
    modifier: Modifier = Modifier,
    initialValue: String,
    @StringRes labelRes: Int,
    onCommentSubmitted: (String) -> Unit,
    minLines: Int = 1,
) {
    var text by remember { mutableStateOf(initialValue) }
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }
    var isFocused by remember { mutableStateOf(false) }

    TextField(
        modifier = modifier
            .fillMaxWidth()
            .onFocusChanged { focusState ->
                val currentlyFocused = focusState.isFocused
                if (isFocused && !currentlyFocused) {
                    onCommentSubmitted(text)
                }
                isFocused = currentlyFocused
            }
            .focusRequester(focusRequester),
        value = text,
        minLines = minLines,
        shape = RoundedCornerShape(Dimens.CornerRadius8),
        onValueChange = { text = it },
        label = {
            Text(
                text = stringResource(id = labelRes),
                style = Typography.RegularLightTextStyle
            )
        },
        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions(
            onDone = {
                focusManager.clearFocus()
                onCommentSubmitted(text)
            }
        ),
        trailingIcon =
            {

                Row {
                    if (text.isNotEmpty()) {
                        IconButton(onClick = { text = "" }) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = stringResource(id = R.string.clear_text),
                                tint = Colors.WhiteTransparent75
                            )
                        }
                        Spacer(modifier = Modifier.size(Dimens.MarginSuperSmall4))
                    }
                    IconButton(onClick = { onCommentSubmitted(text) }) {
                        Icon(
                            Icons.Default.Check,
                            contentDescription = stringResource(id = R.string.ok),
                            tint = Colors.LabelVegGreen
                        )
                    }
                }
            },

        colors = TextFieldDefaults.colors(
            cursorColor = Colors.Orange,
            focusedTextColor = Colors.White,
            focusedContainerColor = Colors.DarkGrey,
            focusedIndicatorColor = Colors.White,
            unfocusedTextColor = Colors.White,
            unfocusedContainerColor = Colors.DarkGrey,
            unfocusedIndicatorColor = Colors.Transparent,
            errorIndicatorColor = Colors.ErrorRed,
            errorContainerColor = Colors.DarkGrey,
            disabledTextColor = Colors.White,
            disabledContainerColor = Colors.DarkGrey,
            disabledIndicatorColor = Colors.Transparent,
        ),
    )
}
