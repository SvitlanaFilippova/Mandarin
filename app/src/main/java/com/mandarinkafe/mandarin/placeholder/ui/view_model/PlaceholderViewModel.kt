package com.mandarinkafe.mandarin.placeholder.ui.view_model

import androidx.lifecycle.viewModelScope
import com.mandarinkafe.mandarin.core.BaseViewModel
import com.mandarinkafe.mandarin.features.menu.domain.usecase.MenuInteractor
import com.mandarinkafe.mandarin.placeholder.ui.view_model.PlaceholderContract.PlaceholderEffect
import com.mandarinkafe.mandarin.placeholder.ui.view_model.PlaceholderContract.PlaceholderEvent
import com.mandarinkafe.mandarin.placeholder.ui.view_model.PlaceholderContract.PlaceholderState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlaceholderViewModel
@Inject constructor(
    private val menuInteractor: MenuInteractor,
) : BaseViewModel<PlaceholderEvent, PlaceholderEffect, PlaceholderState>() {
    override fun setInitialState() = PlaceholderState()

    override fun onEvent(event: PlaceholderEvent) {
        when (event) {
            PlaceholderEvent.Retry -> forceRefreshMenu()
            PlaceholderEvent.OnPhoneClick -> TODO()
        }
    }

    private fun forceRefreshMenu() {
        setState { copy(isLoading = true) }
        viewModelScope.launch {
            menuInteractor.forceRefresh()

            //  TODO: далее как-то надо сделать проверку на успех и если успех,
            //   то перебрасывать обратно на меню.
        }
    }

}