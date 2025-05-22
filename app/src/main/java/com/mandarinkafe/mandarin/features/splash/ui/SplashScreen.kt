package com.mandarinkafe.mandarin.features.splash.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
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
import androidx.hilt.navigation.compose.hiltViewModel
import com.mandarinkafe.mandarin.R
import com.mandarinkafe.mandarin.core.ui.theme.Colors
import com.mandarinkafe.mandarin.core.ui.theme.Dimens
import com.mandarinkafe.mandarin.features.splash.ui.view_model.SplashViewModel
import com.mandarinkafe.mandarin.util.Constants.ANIMATION_DURATION_FAST
import com.mandarinkafe.mandarin.util.Constants.SPLASH_ANIMATION_DURATION
import kotlinx.coroutines.launch

@Composable
fun SplashScreen(
    viewModel: SplashViewModel = hiltViewModel(),
    onFinished: () -> Unit
) {

    val state by viewModel.state.collectAsState()
    val bgScale = remember { Animatable(2.0f) }
    val bgAlpha = remember { Animatable(0f) }
    val bgRotation = remember { Animatable(0f) }
    val logoAlpha = remember { Animatable(1f) }

    // Анимация появления (фон крутится и уменьшается)
    LaunchedEffect(Unit) {
        launch {
            bgScale.animateTo(
                targetValue = 1f,
                animationSpec = tween(
                    durationMillis = SPLASH_ANIMATION_DURATION,
                    easing = FastOutSlowInEasing
                )
            )
        }
        launch {
            bgAlpha.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = SPLASH_ANIMATION_DURATION)
            )
        }
        launch {
            bgRotation.animateTo(
                targetValue = 360 * 2f,
                animationSpec = tween(
                    durationMillis = SPLASH_ANIMATION_DURATION,
                    easing = FastOutSlowInEasing
                )
            )
        }
    }
    // Анимация исчезновения логотипа перед завершением
    LaunchedEffect(state.isVisible) {
        if (!state.isVisible) {
            bgAlpha.animateTo(
                targetValue = 0f,
                animationSpec = tween(durationMillis = ANIMATION_DURATION_FAST)
            )
            onFinished()
        }
    }


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Colors.AppBlack)
    ) {
        // Фон
        Icon(
            painter = painterResource(id = R.drawable.background_for_logo),
            contentDescription = stringResource(R.string.logo_cafe),
            modifier = Modifier
                .align(Alignment.Center)
                .size(Dimens.SplashScreenBackSize240)
                .graphicsLayer {
                    scaleX = bgScale.value
                    scaleY = bgScale.value
                    rotationZ = bgRotation.value
                    alpha = bgAlpha.value
                },
            tint = Colors.Orange
        )

        // Логотип
        Icon(
            painter = painterResource(id = R.drawable.logo_orange),
            contentDescription = stringResource(R.string.logo_cafe),
            modifier = Modifier
                .align(Alignment.Center)
                .size(Dimens.SplashScreenLogoSize180)
                .graphicsLayer {
                    alpha = logoAlpha.value
                },
            tint = Colors.AppBlack
        )
    }
}