package com.mandarinkafe.mandarin.features.splash.ui.view_model

import androidx.lifecycle.viewModelScope
import com.mandarinkafe.mandarin.core.BaseViewModel
import com.mandarinkafe.mandarin.features.menu.domain.usecase.MenuInteractor
import com.mandarinkafe.mandarin.features.splash.ui.view_model.SplashContract.SplashEffect
import com.mandarinkafe.mandarin.features.splash.ui.view_model.SplashContract.SplashEvent
import com.mandarinkafe.mandarin.features.splash.ui.view_model.SplashContract.SplashState
import com.mandarinkafe.mandarin.util.Constants.SPLASH_SCREEN_DURATION
import com.mandarinkafe.mandarin.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val menuInteractor: MenuInteractor
) : BaseViewModel<SplashEvent, SplashEffect, SplashState>() {
    init {
        loadInitialData()
    }

    override fun setInitialState() = SplashState()
    override fun onEvent(event: SplashEvent) {}

    private fun loadInitialData() {
        viewModelScope.launch {
            // Запускаем параллельно таймер, который через SPLASH_SCREEN_DURATION закроет экран,
            launch {
                delay(SPLASH_SCREEN_DURATION)
                setState {
                    copy(isVisible = false)
                }
            }

            // Параллельно начинаем загрузку меню
            menuInteractor.getMenu().collectLatest { resource ->
                if (resource is Resource.Success) {
                    setState { copy(isVisible = false) }
                    // Если Success прилетит раньше таймера SPLASH_SCREEN_DURATION - закрываем экран
                }
            }
        }
    }
}
