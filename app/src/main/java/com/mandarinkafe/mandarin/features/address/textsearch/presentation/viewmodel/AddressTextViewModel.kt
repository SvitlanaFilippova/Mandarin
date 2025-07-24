package com.mandarinkafe.mandarin.features.address.textsearch.presentation.viewmodel

import androidx.lifecycle.viewModelScope
import com.mandarinkafe.mandarin.features.address.textsearch.presentation.viewmodel.AddressTextContract.AddressTexEffect
import com.mandarinkafe.mandarin.features.address.textsearch.presentation.viewmodel.AddressTextContract.AddressTexEvent
import com.mandarinkafe.mandarin.features.address.textsearch.presentation.viewmodel.AddressTextContract.AddressTexState
import com.mandarinkafe.mandarin.util.debounce
import com.mandarinkafe.mandarin.util.presentation.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AddressTextViewModel @Inject constructor() :
    BaseViewModel<AddressTexEvent, AddressTexEffect, AddressTexState>() {

    override fun setInitialState() = AddressTexState()

    private val searchWithDebounce = debounce<String>(
        SEARCH_DELAY,
        viewModelScope,
        useLastParam = true
    ) { expression ->
        //TODO()
    }

    override fun onEvent(event: AddressTexEvent) {
        when (event) {
            else -> {} //TODO()
        }
    }

    fun cancelSearchDebounce() {
        searchWithDebounce.cancel()
    }

    override fun setLoading(isLoading: Boolean) {
        //
    }

    private companion object {
        private const val SEARCH_DELAY = 2000L
    }
}