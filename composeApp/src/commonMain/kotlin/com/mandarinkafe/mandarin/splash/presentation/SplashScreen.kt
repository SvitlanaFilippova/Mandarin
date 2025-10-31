package com.mandarinkafe.mandarin.splash.presentation

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import com.mandarinkafe.mandarin.MR
import com.mandarinkafe.mandarin.core.presentation.theme.Colors
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.splash.presentation.model.SplashElementsProvider
import com.mandarinkafe.mandarin.util.Constants.SPLASH_ANIMATION_DELAY_FOR_ELEMENT
import com.mandarinkafe.mandarin.util.Constants.SPLASH_ANIMATION_DURATION
import com.mandarinkafe.mandarin.util.Constants.SPLASH_APPEARING_DURATION
import com.mandarinkafe.mandarin.util.Constants.SPLASH_GLOBAL_ALPHA_INIT
import com.mandarinkafe.mandarin.util.Constants.SPLASH_LOGO_ALPHA_INIT
import dev.icerock.moko.resources.compose.painterResource
import dev.icerock.moko.resources.compose.stringResource
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SplashScreen() {
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
                painter = painterResource(element.resId),
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

        // Логотип на подложке
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .size(Dimens.SplashScreenBackgroundSize220)
                .background(
                    color = Colors.AppBlack,
                    shape = CircleShape
                )
                .graphicsLayer { alpha = logoAlpha.value }
        ) {
            Image(
                painter = painterResource(MR.images.logo_orange_simplified),
                contentDescription = stringResource(MR.strings.logo_cafe),
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(Dimens.SplashScreenLogoSize180)
                    .graphicsLayer { alpha = logoAlpha.value }
            )
        }
    }
}
