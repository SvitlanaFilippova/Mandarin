package com.mandarinkafe.mandarin.search.ui.view_model

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(

) : ViewModel() {

    private val _state = MutableStateFlow(SearchContract.State()) // для хранения состояния ЮИ
    val state: StateFlow<SearchContract.State> = _state.asStateFlow()
}


