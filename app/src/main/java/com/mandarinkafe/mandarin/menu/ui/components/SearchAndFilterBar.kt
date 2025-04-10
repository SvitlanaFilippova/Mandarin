package com.mandarinkafe.mandarin.menu.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.mandarinkafe.mandarin.R
import com.mandarinkafe.mandarin.core.ui.theme.Colors
import com.mandarinkafe.mandarin.core.ui.theme.Dimens

@Composable
fun SearchAndFilterBar(
    onSearchClick: () -> Unit,
    onFilterClick: () -> Unit,
    onFavoriteClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Colors.Transparent)
            .padding(Dimens.MarginSmall8),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        FakeSearchBarField(modifier = Modifier.weight(1f), onClick = onSearchClick)
        Spacer(modifier = Modifier.width(Dimens.MarginSmall8))
        FilterButton(onClick = onFilterClick)
        Spacer(modifier = Modifier.width(Dimens.MarginSmall8))
        FavoriteButton(onClick = onFavoriteClick)
    }
}

@Composable
fun FakeSearchBarField(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start,
        modifier = modifier
            .clip(RoundedCornerShape(Dimens.RadiusSearchField8))
            .background(Colors.GreyTransparent10)
            .clickable { onClick() }
            .height(Dimens.FakeSearchBarHeight48),

        ) {
        Icon(
            modifier = Modifier.padding(horizontal = Dimens.MarginSmall8),
            imageVector = Icons.Default.Search,
            contentDescription = stringResource(id = R.string.search_in_menu),
            tint = Colors.White
        )
        Text(
            modifier = Modifier.padding(end = Dimens.MarginSmall8),
            text = stringResource(id = R.string.search_in_menu),
            color = Colors.White,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
fun FilterButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    IconButton(
        onClick = onClick,
        modifier = modifier
            .clip(RoundedCornerShape(Dimens.RadiusSearchField8))
            .background(Colors.GreyTransparent10)
            .size(Dimens.FakeSearchBarHeight48)
    ) {
        Icon(
            painterResource(R.drawable.ic_label_24),
            contentDescription = stringResource(id = R.string.filter_by_label), tint = Colors.White
        )
    }

}

@Composable
fun FavoriteButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    IconButton(
        onClick = onClick,
        modifier = modifier
            .clip(
                RoundedCornerShape(Dimens.RadiusSearchField8)
            )
            .background(Colors.GreyTransparent10)
            .size(Dimens.FakeSearchBarHeight48)

    ) {
        Icon(
            imageVector = Icons.Default.Favorite,
            contentDescription = stringResource(id = R.string.favorite), tint = Colors.White
        )
    }
}