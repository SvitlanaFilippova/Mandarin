package com.mandarinkafe.mandarin.splash.presentation.view_model

import com.mandarinkafe.mandarin.util.BaseEffect
import com.mandarinkafe.mandarin.util.BaseEvent
import com.mandarinkafe.mandarin.util.BaseState

sealed interface SplashContract {
    sealed interface SplashEvent : BaseEvent

    sealed interface SplashEffect : BaseEffect

    data class SplashState(
        val isVisible: Boolean = true,
    ) : BaseState
}