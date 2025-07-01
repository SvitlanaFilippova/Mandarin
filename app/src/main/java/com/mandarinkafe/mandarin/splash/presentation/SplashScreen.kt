package com.mandarinkafe.mandarin.splash.presentation

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
import androidx.hilt.navigation.compose.hiltViewModel
import com.mandarinkafe.mandarin.R
import com.mandarinkafe.mandarin.core.presentation.theme.Colors
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.shared.ui.viewmodel.SharedViewModel
import com.mandarinkafe.mandarin.splash.presentation.model.SplashElementsProvider
import com.mandarinkafe.mandarin.util.Constants.ANIMATION_DURATION_FAST
import com.mandarinkafe.mandarin.util.Constants.SPLASH_ANIMATION_DELAY_FOR_ELEMENT
import com.mandarinkafe.mandarin.util.Constants.SPLASH_ANIMATION_DURATION
import com.mandarinkafe.mandarin.util.Constants.SPLASH_APPEARING_DURATION
import com.mandarinkafe.mandarin.util.Constants.SPLASH_GLOBAL_ALPHA_INIT
import com.mandarinkafe.mandarin.util.Constants.SPLASH_LOGO_ALPHA_INIT
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SplashScreen(
    viewModel: SharedViewModel = hiltViewModel(),
    onFinished: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    val globalAlpha = remember { Animatable(SPLASH_GLOBAL_ALPHA_INIT) }
    val logoAlpha = remember { Animatable(SPLASH_LOGO_ALPHA_INIT) }

    // Анимация появления элементов
    LaunchedEffect(Unit) {
        launch {
            logoAlpha.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = SPLASH_APPEARING_DURATION)
            )
            globalAlpha.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = SPLASH_APPEARING_DURATION)
            )
        }
    }

    // Анимация скрытия всего контента
    LaunchedEffect(state.isSplashVisible) {
        if (!state.isSplashVisible) {
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

    val elements = remember { SplashElementsProvider.getSplashElements() }

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
                    delay(index * SPLASH_ANIMATION_DELAY_FOR_ELEMENT)
                    offsetX.animateTo(
                        element.targetOffsetX,
                        tween(SPLASH_ANIMATION_DURATION, easing = FastOutSlowInEasing)
                    )
                }
                launch {
                    delay(index * SPLASH_ANIMATION_DELAY_FOR_ELEMENT)
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
                .size(Dimens.SplashScreenBackgroundSize220)
                .graphicsLayer { alpha = logoAlpha.value },

            )

        // Логотип
        Image(
            painter = painterResource(id = R.drawable.logo_orange),
            contentDescription = stringResource(R.string.logo_cafe),
            modifier = Modifier
                .align(Alignment.Center)
                .size(Dimens.SplashScreenLogoSize180)
                .graphicsLayer { alpha = logoAlpha.value }
        )
    }
}
