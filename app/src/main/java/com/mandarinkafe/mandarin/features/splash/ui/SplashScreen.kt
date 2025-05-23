package com.mandarinkafe.mandarin.features.splash.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mandarinkafe.mandarin.R
import com.mandarinkafe.mandarin.core.ui.theme.Colors
import com.mandarinkafe.mandarin.core.ui.theme.Dimens
import com.mandarinkafe.mandarin.features.splash.ui.model.SplashElement
import com.mandarinkafe.mandarin.features.splash.ui.view_model.SplashViewModel
import com.mandarinkafe.mandarin.util.Constants.ANIMATION_DURATION_FAST
import com.mandarinkafe.mandarin.util.Constants.SPLASH_ANIMATION_DURATION
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SplashScreen(
    viewModel: SplashViewModel = hiltViewModel(),
    onFinished: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    val globalAlpha = remember { Animatable(1f) }
    val logoAlpha = remember { Animatable(1f) }
    // Анимация появления лого
    LaunchedEffect(Unit) {
        launch {
            logoAlpha.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = SPLASH_ANIMATION_DURATION)
            )
        }
    }

    // Анимация скрытия всего контента
    LaunchedEffect(state.isVisible) {
        if (!state.isVisible) {
            globalAlpha.animateTo(
                targetValue = 0f,
                animationSpec = tween(durationMillis = ANIMATION_DURATION_FAST)
            )
            logoAlpha.animateTo(
                targetValue = 0f,
                animationSpec = tween(durationMillis = ANIMATION_DURATION_FAST)
            )
            onFinished()
        }
    }

    val elements = listOf(
        SplashElement(
            R.drawable.splash_wave2, Alignment.TopEnd, offsetX = 200f, offsetY = 500f,
            targetOffsetX = 0f, targetOffsetY = 400f
        ),
        SplashElement(
            R.drawable.splash_pizza, Alignment.TopEnd, offsetX = 400f, offsetY = 300f,
            targetOffsetX = 100f, targetOffsetY = -100f
        ),

        SplashElement(
            R.drawable.splash_wave2, Alignment.BottomStart, offsetX = 200f, offsetY = 300f,
            targetOffsetX = -100f, targetOffsetY = 100f
        ),

        SplashElement(
            R.drawable.splash_pizza3, Alignment.BottomStart, offsetX = 100f, offsetY = 000f,
            targetOffsetX = 100f, targetOffsetY = -100f
        ),

        SplashElement(
            R.drawable.splash_wave3, Alignment.TopStart, offsetX = -400f, offsetY = -600f,
            targetOffsetX = 00f, targetOffsetY = 300f
        ),

        SplashElement(
            R.drawable.splash_olives, Alignment.CenterStart, offsetX = -400f, offsetY = -600f,
            targetOffsetX = -150f, targetOffsetY = -400f
        ),

        SplashElement(
            R.drawable.splash_mushroom, Alignment.CenterStart, offsetX = -200f, offsetY = -400f,
            targetOffsetX = 0f, targetOffsetY = 200f
        ),

        SplashElement(
            R.drawable.splash_wave3, Alignment.BottomEnd, offsetX = 200f, offsetY = 500f,
            targetOffsetX = 150f, targetOffsetY = 200f
        ),

        SplashElement(
            R.drawable.splash_board, Alignment.BottomEnd, offsetX = 200f, offsetY = -300f,
            targetOffsetX = 100f, targetOffsetY = 0f
        ),
        SplashElement(
            R.drawable.splash_fork, Alignment.BottomEnd, offsetX = -100f, offsetY = 200f,
            targetOffsetX = 0f, targetOffsetY = 100f
        ),

        SplashElement(
            R.drawable.splash_tomato, Alignment.CenterEnd, offsetX = 200f, offsetY = 500f,
            targetOffsetX = 0f, targetOffsetY = -200f
        ),

        SplashElement(
            R.drawable.splash_wave4, Alignment.CenterEnd, offsetX = 500f, offsetY = 200f,
            targetOffsetX = 100f, targetOffsetY = 0f
        ),
        SplashElement(
            R.drawable.splash_pepper, Alignment.CenterEnd, offsetX = 200f, offsetY = 500f,
            targetOffsetX = -80f, targetOffsetY = 300f
        ),
        SplashElement(
            R.drawable.splash_cheese4, Alignment.TopStart, offsetX = -400f, offsetY = -300f,
            targetOffsetX = -50f, targetOffsetY = -50f
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Colors.AppBlack)
    ) {
        elements.forEachIndexed { index, element ->
            val offsetX = remember { Animatable(element.offsetX) }
            val offsetY = remember { Animatable(element.offsetY) }

            LaunchedEffect(Unit) {
                launch {
                    delay(index * 80L)
                    offsetX.animateTo(
                        element.targetOffsetX,
                        tween(SPLASH_ANIMATION_DURATION, easing = FastOutSlowInEasing)
                    )
                }
                launch {
                    delay(index * 80L)
                    offsetY.animateTo(
                        element.targetOffsetY,
                        tween(SPLASH_ANIMATION_DURATION, easing = FastOutSlowInEasing)
                    )
                }
            }

            Image(
                painter = painterResource(id = element.resId),
                contentDescription = null,
                modifier = Modifier
                    .align(element.alignment)
                    .graphicsLayer {
                        translationX = offsetX.value
                        translationY = offsetY.value
                        alpha = globalAlpha.value
                    }
            )
        }

        // Фон под лого
        Image(
            painter = painterResource(id = R.drawable.background_for_logo),
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.Center)
                .size(200.dp)
                .graphicsLayer { alpha = logoAlpha.value },

            )

        // Логотип
        Image(
            painter = painterResource(id = R.drawable.logo_orange),
            contentDescription = stringResource(R.string.logo_cafe),
            modifier = Modifier
                .align(Alignment.Center)
                .size(Dimens.SplashScreenLogoSize150)
                .graphicsLayer { alpha = logoAlpha.value }
        )
    }
}
