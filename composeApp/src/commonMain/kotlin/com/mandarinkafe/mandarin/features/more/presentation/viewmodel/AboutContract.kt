package com.mandarinkafe.mandarin.features.more.presentation.viewmodel

import com.mandarinkafe.mandarin.util.presentation.BaseContract

sealed interface AboutContract {
    sealed interface AboutEvent : BaseContract.BaseEvent
    sealed interface AboutEffect : BaseContract.BaseEffect
    data class AboutState(
        val versionName: String? = null,
        val lastUpdated: String? = null,
        val revision: Int? = null,
    ) : BaseContract.BaseState
}

