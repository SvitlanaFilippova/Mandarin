package com.mandarinkafe.mandarin.features.meal_details.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.mandarinkafe.mandarin.R
import com.mandarinkafe.mandarin.core.ui.theme.Colors
import com.mandarinkafe.mandarin.core.ui.theme.Dimens
import com.mandarinkafe.mandarin.core.ui.theme.Typography
import kotlinx.coroutines.launch

@Composable
fun MakeMoreDeliciousBlock(
    onClick: () -> Unit
) {
    val offsetY = remember { Animatable(initialValue = 40f) }
    val alpha = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        launch {
            alpha.animateTo(1f, animationSpec = tween(durationMillis = 300))
        }

        repeat(5) { i ->
            val bounceHeight = 25f - i * 4
            offsetY.animateTo(
                targetValue = -bounceHeight,
                animationSpec = tween(durationMillis = 250, easing = FastOutSlowInEasing)
            )
            offsetY.animateTo(
                targetValue = 0f,
                animationSpec = tween(durationMillis = 250, easing = LinearOutSlowInEasing)
            )
        }
    }

    Column(
        modifier = Modifier
            .padding(top = Dimens.MarginSmall8)
            .fillMaxWidth()
            .clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(id = R.string.make_more_delicious_description),
            style = Typography.TitleStyle,
            fontWeight = FontWeight.Medium,
            color = Colors.White,
            textAlign = TextAlign.Center
        )

        Box(
            modifier = Modifier.graphicsLayer {
                translationY = offsetY.value
                this.alpha = alpha.value
            },
            contentAlignment = Alignment.Center
        )
        {
            IconButton(
                onClick = onClick,
                modifier = Modifier.size(Dimens.ButtonBox32)
            ) {
                Icon(
                    modifier = Modifier.size(Dimens.IconSize24),
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = stringResource(id = R.string.make_more_delicious_description),
                    tint = Colors.White
                )
            }
        }
    }
}