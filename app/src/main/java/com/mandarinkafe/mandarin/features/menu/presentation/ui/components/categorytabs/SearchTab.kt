package com.mandarinkafe.mandarin.features.menu.presentation.ui.components.categorytabs

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.mandarinkafe.mandarin.R
import com.mandarinkafe.mandarin.core.presentation.theme.Colors
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens

@Composable
fun SearchTab(
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Tab(
        selected = isSelected,
        onClick = onClick,
        text = {
            Text(
                stringResource(R.string.search),
                color = Color.White
            )
        },
        icon = {
            Icon(
                modifier = Modifier.size(Dimens.IconSize24),
                imageVector = Icons.Default.Search,
                contentDescription = stringResource(id = R.string.search_in_menu),
                tint = Colors.White
            )
        },
        selectedContentColor = Colors.Orange,
    )
}