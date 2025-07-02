package com.mandarinkafe.mandarin.features.menu.presentation.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import com.mandarinkafe.mandarin.R
import com.mandarinkafe.mandarin.core.presentation.theme.Colors
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens

@Composable
fun SearchBar(
    visible: Boolean,
    onSearchClick: () -> Unit,
) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn() + expandVertically(),
        exit = fadeOut() + shrinkVertically()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Colors.Transparent)
                .padding(Dimens.MarginSmall8),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            FakeSearchBarField(modifier = Modifier.weight(1f), onClick = onSearchClick)
        }
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
            .background(Colors.DarkGrey)
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


