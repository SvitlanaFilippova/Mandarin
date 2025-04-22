package com.mandarinkafe.mandarin.menu.ui.components.mealitem.buttons

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.mandarinkafe.mandarin.R
import com.mandarinkafe.mandarin.core.ui.theme.Dimens

@Composable
fun FavoriteButton(
    isFavorite: Boolean,
    onClick: () -> Unit
) {
    IconButton(
        onClick = onClick,
        modifier = Modifier.size(Dimens.ButtonBox32)
    ) {
        Icon(
            painter = painterResource(
                if (isFavorite) R.drawable.ic_favorite_active
                else R.drawable.ic_favorite_inactive
            ),
            contentDescription = stringResource(R.string.add_to_favorite),
            modifier = Modifier.size(Dimens.ButtonToggleFavorite28),
            tint = Color.Unspecified
        )
    }
}