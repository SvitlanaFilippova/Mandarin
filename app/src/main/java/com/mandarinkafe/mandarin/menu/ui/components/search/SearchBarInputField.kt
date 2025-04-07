package com.mandarinkafe.mandarin.menu.ui.components.search

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.mandarinkafe.mandarin.R
import com.mandarinkafe.mandarin.core.ui.theme.Colors
import com.mandarinkafe.mandarin.core.ui.theme.Dimens

@Composable
fun SearchBarInputField(
    query: String,
    onQueryChange: (String) -> Unit,
    onClear: () -> Unit
) {
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
            if (query.isNotEmpty()) {
                IconButton(onClick = onClear) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = stringResource(id = R.string.clear_text),
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
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(Dimens.RadiusSearchField8),
    )
}