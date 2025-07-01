package com.mandarinkafe.mandarin.features.search.presentation.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
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
    query: String,
    onQueryChange: (String) -> Unit,
    onClear: () -> Unit,
    onSearchDismiss: () -> Unit,
    autoFocus: Boolean = false
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
        value = query,
        onValueChange = onQueryChange,
        placeholder = {
            Text(
                text = stringResource(id = R.string.search_in_menu),
                color = Colors.White
            )
        },
        leadingIcon = {
            Icon(
                Icons.Default.Search,
                contentDescription = stringResource(id = R.string.search_in_menu),
                tint = Colors.White
            )
        },

        trailingIcon = {
            if (query.isNotEmpty()) { // если в поле есть текст - очистить его
                IconButton(onClick = { onClear() }) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = stringResource(id = R.string.clear_text),
                        tint = Colors.White
                    )
                }
            } else {
                IconButton(onClick = { onSearchDismiss() }) { // если поле пустое - возврат назад
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(id = R.string.back),
                        tint = Colors.White
                    )
                }
            }
        },
        singleLine = true,
        colors = TextFieldDefaults.colors(
            cursorColor = Colors.Orange,
            focusedTextColor = Colors.White,
            focusedContainerColor = Colors.Transparent,
            focusedIndicatorColor = Colors.Orange,
            unfocusedTextColor = Colors.White,
            unfocusedContainerColor = Colors.Transparent,
            unfocusedIndicatorColor = Colors.Transparent,
        ),
        modifier = Modifier
            .fillMaxWidth()
            .focusRequester(focusRequester),

        shape = RoundedCornerShape(Dimens.RadiusSearchField8),
        enabled = true,
    )
}