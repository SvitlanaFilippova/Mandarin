package com.mandarinkafe.mandarin.features.address.textsearch.presentation.viewmodel

import com.mandarinkafe.mandarin.features.address.textsearch.SearchResponseItem
import com.mandarinkafe.mandarin.util.BaseEffect
import com.mandarinkafe.mandarin.util.BaseEvent
import com.mandarinkafe.mandarin.util.BaseState
import com.yandex.mapkit.geometry.Geometry

sealed interface AddressTextContract {

    sealed interface AddressTextEvent : BaseEvent {
        data class SetQuery(val query: String) : AddressTextEvent
        data class SetInitData(val geometry: Geometry, val query: String) : AddressTextEvent
        data class Search(val query: String) : AddressTextEvent
        data object ChooseAddress : AddressTextEvent
        data object GoToMapSearch : AddressTextEvent
    }

    sealed interface AddressTextEffect : BaseEffect {
        data object GoToMapSearch : AddressTextEffect
    }

    data class AddressTextState(
        val query: String = "",
        val isLoading: Boolean = false,
        val isError: Boolean = false,
        val visibleGeometry: Geometry? = null,
        val data: List<SearchResponseItem> = listOf()
    ) : BaseState
}