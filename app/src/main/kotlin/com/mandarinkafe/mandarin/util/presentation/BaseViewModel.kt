package com.mandarinkafe.mandarin.util.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

abstract class BaseViewModel<Event : BaseEvent, Effect : BaseEffect, State : BaseState> :
    ViewModel() {

    protected abstract fun setInitialState(): State
    private val initialState: State by lazy { setInitialState() }

    /**
    для сохранения состояния UI
     */
    private val _state = MutableStateFlow(initialState)
    val state: StateFlow<State> = _state.asStateFlow()

    /**
    для одноразовых событий. Например, показа снекбар
     */
    private val _effect = MutableSharedFlow<Effect>()
    val effect: SharedFlow<Effect> = _effect.asSharedFlow()

    /**
    единственная входная точка в ViewModel извне - передача Event
     */
    abstract fun onEvent(event: Event)

    /**
    для отправки эффектов
     */
    protected fun sendEffect(effect: Effect) {
        viewModelScope.launch { _effect.emit(effect) }
    }

    /**
    Для изменения State.
    this внутри setState {} — это текущий State, поэтому можно сразу обращаться к его полям, использовать copy(...).
     */
    protected fun setState(reducer: State.() -> State) {
        _state.value = _state.value.reducer()
    }

    /**
    Устанавливает флаг загрузки.
     */
    protected abstract fun setLoading(isLoading: Boolean = true)
}