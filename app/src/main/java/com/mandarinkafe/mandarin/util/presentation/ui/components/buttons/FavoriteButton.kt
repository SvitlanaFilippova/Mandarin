package com.mandarinkafe.mandarin.util.presentation.ui.components.buttons

import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.mandarinkafe.mandarin.R
import com.mandarinkafe.mandarin.core.presentation.theme.Colors
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.util.Constants.ANIMATION_DURATION_FAST

@Composable
fun FavoriteButton(
    modifier: Modifier = Modifier,
    isFavorite: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (isFavorite) Colors.Orange else Colors.LightGrey,
        animationSpec = tween(durationMillis = ANIMATION_DURATION_FAST),
        label = "favoriteBackgroundColor"
    )

    val scaleAnim = remember { Animatable(1f) }

    // Предыдущее значение, чтобы отследить изменение
    var prevFavorite by remember { mutableStateOf(isFavorite) }

    // Анимация при добавлении в избранное
    LaunchedEffect(isFavorite) {
        if (!prevFavorite && isFavorite) {
            scaleAnim.snapTo(1f)
            scaleAnim.animateTo(
                targetValue = 1.2f,
                animationSpec = tween(durationMillis = ANIMATION_DURATION_FAST)
            )
            scaleAnim.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = ANIMATION_DURATION_FAST)
            )
        }
        prevFavorite = isFavorite
    }

    IconButton(
        onClick = onClick,
        modifier = modifier
            .size(Dimens.ButtonBox32)
    ) {
        Box(
            modifier = Modifier
                .size(Dimens.IconSize24)
                .graphicsLayer(
                    scaleX = scaleAnim.value,
                    scaleY = scaleAnim.value
                )
                .shadow(
                    elevation = Dimens.Elevation4,
                    shape = CircleShape,
                    clip = false
                )
                .background(
                    color = backgroundColor,
                    shape = CircleShape
                )
                .clip(CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Crossfade(
                targetState = isFavorite,
                animationSpec = tween(durationMillis = ANIMATION_DURATION_FAST),
                label = "favoriteIcon"
            ) { isFav ->
                Icon(
                    modifier = Modifier.padding(Dimens.MarginSuperSmall4),
                    painter = painterResource(
                        if (isFav) {
                            R.drawable.ic_favorite_active
                        } else {
                            R.drawable.ic_favorite_inactive
                        }
                    ),
                    contentDescription = stringResource(R.string.add_to_favorite),
                    tint = Colors.White
                )
            }
        }
    }
}
