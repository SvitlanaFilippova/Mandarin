package com.mandarinkafe.mandarin.features.address.data.impl

import com.mandarinkafe.mandarin.core.data.network.NetworkMonitor
import com.mandarinkafe.mandarin.core.domain.models.GeoPoint
import com.mandarinkafe.mandarin.features.address.data.Mapper.toAddressSearchResult
import com.mandarinkafe.mandarin.features.address.data.Mapper.toYandexPoint
import com.mandarinkafe.mandarin.features.address.domain.api.AddressRepository
import com.mandarinkafe.mandarin.features.address.domain.models.AddressSearchResult
import com.mandarinkafe.mandarin.util.Resource
import com.yandex.mapkit.geometry.Geometry
import com.yandex.mapkit.search.Response
import com.yandex.mapkit.search.SearchManager
import com.yandex.mapkit.search.SearchOptions
import com.yandex.mapkit.search.Session
import com.yandex.runtime.Error
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddressRepositoryImpl(
    private val searchManager: SearchManager,
    private val coroutineScope: CoroutineScope,
    private val networkMonitor: NetworkMonitor,
) : AddressRepository {
    private var session: Session? = null
    private val _addressListFlow = MutableSharedFlow<Resource<List<AddressSearchResult>>>()
    override val addressListFlow: SharedFlow<Resource<List<AddressSearchResult>>> =
        _addressListFlow

    private val _addressStringFlow = MutableSharedFlow<Resource<AddressSearchResult>>()
    override val addressStringFlow: SharedFlow<Resource<AddressSearchResult>> =
        _addressStringFlow

    // Слушатель для поиска по текстовому запросу
    private val listenerForSearchByText = object : Session.SearchListener {
        override fun onSearchResponse(response: Response) {
            val geoObjects = response.collection.children.mapNotNull { it.obj }

            if (geoObjects.isNotEmpty()) {
                coroutineScope.launch {
                    _addressListFlow.emit(Resource.Success(geoObjects.map { it.toAddressSearchResult() }))
                }
            } else {
                coroutineScope.launch {
                    _addressListFlow.emit(Resource.ErrorEmptyData())
                }
            }
        }

        override fun onSearchError(error: Error) {
            coroutineScope.launch {
                _addressListFlow.emit(Resource.ErrorOther(error.toString()))
            }
        }
    }

    override suspend fun searchAddressByString(query: String, point: GeoPoint) {
        if (!checkIfNetworkOk()) return

        val yPoint = point.toYandexPoint()
        val geometry = Geometry.fromPoint(yPoint)
        val searchOptions = SearchOptions()

        coroutineScope.launch {
            _addressListFlow.emit(Resource.Loading())
        }

        withContext(Dispatchers.Main) {
            session?.cancel()
            session = searchManager.submit(
                query,
                geometry,
                searchOptions,
                listenerForSearchByText
            )
        }
    }

    // Слушатель для обратного геокодинга
    private val listener = object : Session.SearchListener {
        override fun onSearchResponse(response: Response) {
            val geoObj = response.collection.children.firstOrNull()?.obj
            if (geoObj != null) {
                val result = geoObj.toAddressSearchResult()
                coroutineScope.launch {
                    _addressStringFlow.emit(Resource.Success(result))
                }
            } else {
                coroutineScope.launch {
                    _addressStringFlow.emit(Resource.ErrorEmptyData())
                }
            }
        }

        override fun onSearchError(error: Error) {
            coroutineScope.launch {
                _addressStringFlow.emit(Resource.ErrorOther(error.toString()))
            }
        }
    }

    override suspend fun getAddressFromPoint(point: GeoPoint) {
        if (!checkIfNetworkOk()) return

        val yPoint = point.toYandexPoint()
        val searchOptions = SearchOptions()

        coroutineScope.launch {
            _addressStringFlow.emit(Resource.Loading())
        }

        withContext(Dispatchers.Main) {
            val previousSession = session
            session = null
            previousSession?.cancel()

            session = searchManager.submit(
                yPoint,
                DEFAULT_ZOOM_FOR_SEARCH,
                searchOptions,
                listener
            )
        }
    }

    private fun checkIfNetworkOk(): Boolean {
        return if (!networkMonitor.isNetworkAvailable()) {
            coroutineScope.launch {
                _addressListFlow.emit(Resource.ErrorNoInternet())
            }
            true
        } else {
            false
        }
    }

    private companion object {
        const val DEFAULT_ZOOM_FOR_SEARCH = 16
    }
}