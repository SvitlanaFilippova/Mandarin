package com.mandarinkafe.mandarin.features.menu.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.mandarinkafe.mandarin.R
import com.mandarinkafe.mandarin.core.presentation.theme.Colors
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.util.presentation.ui.components.SearchBarInputField

@Composable
fun MenuSearchBar(
    onSearchClick: () -> Unit
) {
    SearchBarInputField(
        modifier = Modifier
            .padding(horizontal = Dimens.MarginSmall8)
            .clickable(onClick = onSearchClick)
            .background(color = Colors.AppBlack),
        query = "",
        placeholderRes = R.string.search_in_menu,
        enabled = false,
        leadingIcon = {
            Icon(
                Icons.Default.Search,
                contentDescription = stringResource(R.string.search_in_menu),
                tint = Colors.White
            )
        }
    )
}