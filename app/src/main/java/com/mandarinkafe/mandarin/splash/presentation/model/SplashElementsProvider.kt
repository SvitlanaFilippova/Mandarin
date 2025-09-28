package com.mandarinkafe.mandarin.splash.presentation.model

import androidx.compose.ui.Alignment
import com.mandarinkafe.mandarin.R

object SplashElementsProvider {
    private val splashElementsArray = arrayOf<SplashElement>(
        SplashElement(
            R.drawable.splash_wave2,
            Alignment.TopEnd,
            offsetX = 200f,
            offsetY = 500f,
            targetOffsetX = 0f,
            targetOffsetY = 400f
        ),
        SplashElement(
            R.drawable.splash_pizza,
            Alignment.TopEnd,
            offsetX = 400f,
            offsetY = -300f,
            targetOffsetX = 100f,
            targetOffsetY = -100f
        ),

        SplashElement(
            R.drawable.splash_pizza1,
            Alignment.TopEnd,
            offsetX = 800f,
            offsetY = 900f,
            targetOffsetX = 160f,
            targetOffsetY = 200f
        ),

        SplashElement(
            R.drawable.splash_towel,
            Alignment.CenterEnd,
            offsetX = 400f,
            offsetY = 800f,
            targetOffsetX = 300f,
            targetOffsetY = 400f
        ),

        SplashElement(
            R.drawable.splash_wave2,
            Alignment.BottomStart,
            offsetX = 200f,
            offsetY = 300f,
            targetOffsetX = -100f,
            targetOffsetY = 100f
        ),

        SplashElement(
            R.drawable.splash_tomato2,
            Alignment.BottomCenter,
            offsetX = -150f,
            offsetY = 500f,
            targetOffsetX = -50f,
            targetOffsetY = -300f
        ),

        SplashElement(
            R.drawable.splash_pizza3,
            Alignment.BottomStart,
            offsetX = 100f,
            offsetY = 500f,
            targetOffsetX = 100f,
            targetOffsetY = -100f
        ),

        SplashElement(
            R.drawable.splash_wave3,
            Alignment.TopStart,
            offsetX = -400f,
            offsetY = -600f,
            targetOffsetX = 00f,
            targetOffsetY = 300f
        ),

        SplashElement(
            R.drawable.splash_olives,
            Alignment.CenterStart,
            offsetX = -500f,
            offsetY = -600f,
            targetOffsetX = -200f,
            targetOffsetY = -400f
        ),

        SplashElement(
            R.drawable.splash_wave5,
            Alignment.CenterStart,
            offsetX = -300f,
            offsetY = -800f,
            targetOffsetX = -30f,
            targetOffsetY = 500f
        ),

        SplashElement(
            R.drawable.splash_mushroom,
            Alignment.CenterStart,
            offsetX = -800f,
            offsetY = -300f,
            targetOffsetX = 0f,
            targetOffsetY = 200f
        ),

        SplashElement(
            R.drawable.splash_wave6,
            Alignment.CenterStart,
            offsetX = -200f,
            offsetY = -400f,
            targetOffsetX = -50f,
            targetOffsetY = 000f
        ),

        SplashElement(
            R.drawable.splash_wave3,
            Alignment.BottomEnd,
            offsetX = 200f,
            offsetY = 500f,
            targetOffsetX = 150f,
            targetOffsetY = 200f
        ),

        SplashElement(
            R.drawable.splash_board,
            Alignment.BottomEnd,
            offsetX = -200f,
            offsetY = 500f,
            targetOffsetX = 100f,
            targetOffsetY = 0f
        ),
        SplashElement(
            R.drawable.splash_fork,
            Alignment.BottomEnd,
            offsetX = 0f,
            offsetY = 500f,
            targetOffsetX = 0f,
            targetOffsetY = 100f
        ),

        SplashElement(
            R.drawable.splash_tomato,
            Alignment.CenterEnd,
            offsetX = 200f,
            offsetY = 500f,
            targetOffsetX = 0f,
            targetOffsetY = -200f
        ),

        SplashElement(
            R.drawable.splash_wave4,
            Alignment.CenterEnd,
            offsetX = 500f,
            offsetY = 200f,
            targetOffsetX = 100f,
            targetOffsetY = 0f
        ),

        SplashElement(
            R.drawable.splash_pepper,
            Alignment.CenterEnd,
            offsetX = 200f,
            offsetY = 500f,
            targetOffsetX = 0f,
            targetOffsetY = 300f
        ),
        SplashElement(
            R.drawable.splash_cheese,
            Alignment.TopStart,
            offsetX = -400f,
            offsetY = -300f,
            targetOffsetX = -50f,
            targetOffsetY = -50f
        ),
    )

    fun getSplashElements(): Array<SplashElement> = splashElementsArray
}