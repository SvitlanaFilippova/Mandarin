package com.mandarinkafe.mandarin.util.ui.components.buttons

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
fun FavoriteButton(
    modifier: Modifier = Modifier,
    isFavorite: Boolean,
    onClick: () -> Unit
) {
    IconButton(
        onClick = onClick,
        modifier = modifier
            .size(Dimens.ButtonBox32)
    ) {
        Box(
            modifier = Modifier
                .size(Dimens.IconSize24)
                .background(
                    color = if (isFavorite) Colors.Orange else Colors.LightGrey,
                    shape = CircleShape
                )
                .clip(CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                modifier = Modifier.padding(Dimens.MarginSuperSmall4),
                painter = painterResource(
                    if (isFavorite) R.drawable.ic_favorite_active
                    else R.drawable.ic_favorite_inactive
                ),
                contentDescription = stringResource(R.string.add_to_favorite),
                tint = Colors.White
            )
        }
    }
}
