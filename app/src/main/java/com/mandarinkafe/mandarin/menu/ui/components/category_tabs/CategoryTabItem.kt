package com.mandarinkafe.mandarin.menu.ui.components.category_tabs

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import coil.compose.AsyncImage
import com.mandarinkafe.mandarin.R
import com.mandarinkafe.mandarin.core.ui.theme.Colors
import com.mandarinkafe.mandarin.core.ui.theme.Dimens
import com.mandarinkafe.mandarin.menu.domain.models.MenuRVItem

@Composable
fun CategoryTabItem(category: MenuRVItem.HeaderItem, isSelected: Boolean, onClick: () -> Unit) {
    Tab(
        selected = isSelected,
        onClick = onClick,
        text = {
            Text(
                category.categoryName,
                color = if (isSelected) Colors.Orange else Color.White
            )
        },
        icon = {
            AsyncImage(
                model = category.tabIcon,
                contentDescription = stringResource(
                    R.string.icon_of_category,
                    category.categoryName
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