package com.mandarinkafe.mandarin.core

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mandarinkafe.mandarin.core.DataState.Data
import com.mandarinkafe.mandarin.core.DataState.Loading
import com.mandarinkafe.mandarin.core.DataState.NetworkStatus
import com.mandarinkafe.mandarin.core.DataState.Response
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

abstract class BaseViewModel<Event : ViewEvent,
        UiState : ViewState,
        SingleAction : ViewSingleAction> : ViewModel() {

    abstract fun setInitialState(): UiState
    abstract fun onTriggerEvent(event: Event)

    private val initialState: UiState by lazy { setInitialState() }
    private val _state: MutableState<UiState> = mutableStateOf(initialState)
    val state = _state

    private val _event: MutableSharedFlow<Event> = MutableSharedFlow()
    private val _action: Channel<SingleAction> = Channel()
    val action = _action.receiveAsFlow()

    private val _errors: Channel<UIComponent> = Channel()
    val errors = _errors.receiveAsFlow()

    init {
        subscribeToEvents()

    }

    private fun subscribeToEvents() {

        viewModelScope.launch {
            _event.collect {
                onTriggerEvent(it)
            }
        }
    }

    fun setEvent(event: Event) {
        viewModelScope.launch { _event.emit(event) }
    }

    protected fun setState(reducer: UiState.() -> UiState) {
        val newState = state.value.reducer()
        _state.value = newState
    }

    protected fun setAction(builder: () -> SingleAction) {
        val effectValue = builder()
        viewModelScope.launch { _action.send(effectValue) }
    }

    protected fun setError(builder: () -> UIComponent) {
        val effectValue = builder()
        viewModelScope.launch {
            _errors.send(effectValue)
        }
    }

    fun <T> executeUseCase(
        flow: Flow<DataState<T>>,
        onSuccess: (T?) -> Unit,
        onLoading: (ProgressBarState) -> Unit,
        onNetworkStatus: (NetworkState) -> Unit = {}
    ) {
        viewModelScope.launch {
            flow.collectLatest { dataState ->
                when (dataState) {
                    is NetworkStatus -> {
                        onNetworkStatus(dataState.networkState)
                    }

                    is Response -> {
                        setError { dataState.uiComponent }
                    }

                    is Data -> {
                        onSuccess(dataState.data)
                    }

                    is Loading -> {
                        onLoading(dataState.progressBarState)
                    }
                }
            }
        }
    }

}