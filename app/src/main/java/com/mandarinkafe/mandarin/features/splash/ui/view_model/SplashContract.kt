package com.mandarinkafe.mandarin.features.splash.ui.view_model

import com.mandarinkafe.mandarin.core.BaseEffect
import com.mandarinkafe.mandarin.core.BaseEvent
import com.mandarinkafe.mandarin.core.BaseState

sealed interface SplashContract {
    sealed interface SplashEvent : BaseEvent

    sealed interface SplashEffect : BaseEffect

    data class SplashState(
        val isVisible: Boolean = true,
    ) : BaseState
}