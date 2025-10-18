package com.mandarinkafe.mandarin.util.presentation.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.ImeAction
import com.mandarinkafe.mandarin.MR
import com.mandarinkafe.mandarin.core.presentation.theme.Colors
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.core.presentation.theme.Typography
import dev.icerock.moko.resources.compose.painterResource
import dev.icerock.moko.resources.compose.stringResource

@Composable
fun MealCommentTextField(
    modifier: Modifier = Modifier,
    initialValue: String,
    labelRes: String,
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
                text = labelRes,
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
                                painter = painterResource(MR.images.ic_close),
                                contentDescription = stringResource(MR.strings.clear_text),
                                tint = Colors.WhiteTransparent75
                            )
                        }
                        Spacer(modifier = Modifier.size(Dimens.MarginSuperSmall4))
                    }
                    IconButton(onClick = { onCommentSubmitted(text) }) {
                        Icon(
                            painter = painterResource(MR.images.ic_check),
                            contentDescription = stringResource(MR.strings.ok),
                            tint = Colors.Green
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
            errorIndicatorColor = Colors.Red,
            errorContainerColor = Colors.DarkGrey,
            disabledTextColor = Colors.White,
            disabledContainerColor = Colors.DarkGrey,
            disabledIndicatorColor = Colors.Transparent,
        ),
    )
}
