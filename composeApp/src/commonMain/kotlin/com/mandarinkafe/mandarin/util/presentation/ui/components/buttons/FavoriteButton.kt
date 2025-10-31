package com.mandarinkafe.mandarin.util.presentation.ui.components.buttons

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import com.mandarinkafe.mandarin.MR
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.util.Constants.ANIMATION_DURATION_FAST
import dev.icerock.moko.resources.compose.painterResource
import dev.icerock.moko.resources.compose.stringResource

@Composable
fun FavoriteButton(
    modifier: Modifier = Modifier,
    isFavorite: Boolean,
    onClick: () -> Unit,
) {
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
                ),
            contentAlignment = Alignment.Center
        ) {
            Crossfade(
                targetState = isFavorite,
                animationSpec = tween(durationMillis = ANIMATION_DURATION_FAST),
                label = "favoriteIcon"
            ) { isFav ->
                Icon(
                    modifier = Modifier.shadow(
                        elevation = Dimens.Elevation4,
                        shape = CircleShape,
                        clip = false
                    ),
                    painter = painterResource(
                        if (isFav) {
                            MR.images.ic_favorite_in_circle_active
                        } else {
                            MR.images.ic_favorite_in_circle_inactive
                        }
                    ),
                    contentDescription = stringResource(MR.strings.add_to_favorite),
                    tint = Color.Unspecified
                )
            }
        }
    }
}