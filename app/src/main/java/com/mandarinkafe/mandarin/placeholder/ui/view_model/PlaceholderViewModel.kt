package com.mandarinkafe.mandarin.placeholder.ui.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mandarinkafe.mandarin.menu.domain.usecase.MenuInteractor
import com.mandarinkafe.mandarin.placeholder.ui.view_model.PlaceholderContract.Effect
import com.mandarinkafe.mandarin.placeholder.ui.view_model.PlaceholderContract.Event
import com.mandarinkafe.mandarin.placeholder.ui.view_model.PlaceholderContract.State
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlaceholderViewModel
@Inject constructor(
    private val menuInteractor: MenuInteractor,
) : ViewModel() {
    private val _state = MutableStateFlow(State()) // для хранения состояния ЮИ
    val state: StateFlow<State> = _state.asStateFlow()

    private val _effect =
        MutableSharedFlow<Effect>() // для одноразовых событий. Например, показа снекбар
    val effect: SharedFlow<Effect> = _effect.asSharedFlow()

    fun onEvent(event: Event) {
        when (event) {
            Event.Retry -> forceRefreshMenu()
            Event.OnPhoneClick -> TODO()
        }

    }

    private fun forceRefreshMenu() {
        _state.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            menuInteractor.forceRefresh()

            //  TODO: далее как-то надо сделать проверку на успех и если успех,
            //   то перебрасывать обратно на меню.
        }
    }

    private fun sendEffect(effect: Effect) {
        viewModelScope.launch { _effect.emit(effect) }
    }
}