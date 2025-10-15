package com.mandarinkafe.mandarin.features.more.presentation.viewmodel

import com.mandarinkafe.mandarin.util.presentation.BaseEffect
import com.mandarinkafe.mandarin.util.presentation.BaseEvent
import com.mandarinkafe.mandarin.util.presentation.BaseState

sealed interface AboutContract {
    sealed interface AboutEvent : BaseEvent
    sealed interface AboutEffect : BaseEffect
    data class AboutState(
        val versionName: String? = null,
        val lastUpdated: String? = null,
        val revision: Int? = null
    ) : BaseState
}