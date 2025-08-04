package com.mandarinkafe.mandarin.features.menu.presentation.ui.components.categorytabs

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import coil3.compose.AsyncImage
import com.mandarinkafe.mandarin.R
import com.mandarinkafe.mandarin.core.presentation.theme.Colors
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens

@Composable
fun CategoryTabItem(
    name: String,
    icon: Any?,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Tab(
        selected = isSelected,
        onClick = onClick,
        text = {
            Text(
                name,
                color = if (isSelected) Colors.Orange else Color.White
            )
        },
        icon = {
            AsyncImage(
                model = icon,
                contentDescription = stringResource(
                    R.string.icon_of_category,
                    name
                ),
                modifier = Modifier.size(Dimens.IconSize24),
                error = painterResource(R.drawable.logo_orange),
                placeholder = painterResource(R.drawable.logo_orange),
                colorFilter = ColorFilter.tint(if (isSelected) Colors.Orange else Color.White)
            )
        },
        selectedContentColor = Colors.Orange,
    )
}