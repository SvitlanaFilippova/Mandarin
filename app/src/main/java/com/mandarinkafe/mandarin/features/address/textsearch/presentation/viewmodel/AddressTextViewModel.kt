package com.mandarinkafe.mandarin.features.address.textsearch.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.mandarinkafe.mandarin.features.address.map.domain.models.toGeoPoint
import com.mandarinkafe.mandarin.features.address.textsearch.SearchResponseItem
import com.mandarinkafe.mandarin.features.address.textsearch.presentation.viewmodel.AddressTextContract.AddressTextEffect
import com.mandarinkafe.mandarin.features.address.textsearch.presentation.viewmodel.AddressTextContract.AddressTextEvent
import com.mandarinkafe.mandarin.features.address.textsearch.presentation.viewmodel.AddressTextContract.AddressTextState
import com.mandarinkafe.mandarin.util.debounce
import com.mandarinkafe.mandarin.util.presentation.BaseViewModel
import com.yandex.mapkit.geometry.Geometry
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.search.Response
import com.yandex.mapkit.search.SearchManager
import com.yandex.mapkit.search.SearchOptions
import com.yandex.mapkit.search.Session
import com.yandex.mapkit.search.Session.SearchListener
import com.yandex.runtime.Error
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AddressTextViewModel @Inject constructor(private val searchManager: SearchManager) :
    BaseViewModel<AddressTextEvent, AddressTextEffect, AddressTextState>() {
    private val logTag = "DEBUG MapKitFactory"
    private var searchSession: Session? = null

    override fun setInitialState() = AddressTextState()

    private val searchWithDebounce = debounce<String>(
        SEARCH_DELAY,
        viewModelScope,
        useLastParam = true
    ) {
        Log.d(logTag, "Debounced search triggered with query: $it")
        startSearch(it)
    }

    override fun onEvent(event: AddressTextEvent) {
        when (event) {
            is AddressTextEvent.ChooseAddress -> {}
            is AddressTextEvent.GoToMapSearch -> {}
            is AddressTextEvent.Search -> {
                startSearch(event.query)
            }

            is AddressTextEvent.SetQuery -> {
                setQuery(event.query)
            }

            is AddressTextEvent.SetInitData -> {
                setInitData(event.geometry, event.query)
            }
        }
    }

    private fun setInitData(geometry: Geometry, query: String) {
        setState { copy(query = query, visibleGeometry = geometry) }

    }

    private fun setQuery(searchText: String) {
        setState { copy(query = searchText) }
        searchWithDebounce.invoke(searchText)
    }

    private fun startSearch(searchText: String? = null) {
        val text = searchText ?: state.value.query
        Log.d(logTag, "StartSearch called with text: '$text'")
        if (text.isEmpty()) {
            Log.d(logTag, "Search aborted: empty query")
            return
        }

        val point = Point(55.751244, 37.618423) // Москва TODO - временно для теста
        val geometry = Geometry.fromPoint(point)

        val region = state.value.visibleGeometry
        if (region == null) {
            Log.d(logTag, "Search aborted: visibleGeometry is null")
            return
        }
        submitSearch(text, geometry) // TODO - временно для теста подставила geometry вместо region
    }

    private val searchSessionListener = object : SearchListener {
        override fun onSearchResponse(response: Response) {
            Log.d(logTag, "onSearchResponse: ${response.collection.children.size} results")
            val items = response.collection.children.mapNotNull {
                val point = it.obj?.geometry?.firstOrNull()?.point ?: return@mapNotNull null
                SearchResponseItem(point.toGeoPoint(), it.obj)
            }
            setState { copy(data = items, isLoading = false) }
        }

        override fun onSearchError(p0: Error) {
            Log.e(logTag, "onSearchError: $p0")
            setState { copy(isError = true, isLoading = false) }
        }
    }

    private fun submitSearch(query: String, geometry: Geometry) {
        Log.d(logTag, "submitSearch: query='$query', geometry=$geometry")
        searchSession?.cancel()
        try {
            searchSession = searchManager.submit(
                query,
                geometry,
                SearchOptions().apply { resultPageSize = 32 },
                searchSessionListener
            )
            Log.d(logTag, "submitSearch: submitted successfully")

        } catch (e: Exception) {
            Log.e(logTag, "submitSearch: exception: $e")
        }
        setLoading()
    }

    private fun reset() {
        Log.d(logTag, "reset called")
        searchWithDebounce.cancel
        searchSession?.cancel()
        searchSession = null
        setState { copy(query = "") }
    }

    override fun setLoading(isLoading: Boolean) {
        Log.d(logTag, "setLoading: $isLoading")
        setState { copy(isLoading = true) }
    }

    private companion object {
        private const val SEARCH_DELAY = 2000L
    }
}