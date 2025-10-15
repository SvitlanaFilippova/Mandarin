package com.mandarinkafe.mandarin.features.address.data.impl

import com.mandarinkafe.mandarin.core.domain.models.GeoPoint
import com.mandarinkafe.mandarin.features.address.data.Mapper.toAddressSearchResult
import com.mandarinkafe.mandarin.features.address.data.Mapper.toYandexPoint
import com.mandarinkafe.mandarin.features.address.domain.api.AddressRepository
import com.mandarinkafe.mandarin.features.address.domain.models.AddressSearchResult
import com.mandarinkafe.mandarin.util.Resource
import com.yandex.mapkit.geometry.Geometry
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.search.Response
import com.yandex.mapkit.search.SearchManager
import com.yandex.mapkit.search.SearchOptions
import com.yandex.mapkit.search.Session
import com.yandex.runtime.Error
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.withContext

class AddressRepositoryImpl(
    private val searchManager: SearchManager
) : AddressRepository {
    private var session: Session? = null
    private val _addressListChannel = Channel<Resource<List<AddressSearchResult>>>(Channel.BUFFERED)
    override val addressListFlow: Flow<Resource<List<AddressSearchResult>>> =
        _addressListChannel.receiveAsFlow()

    private val _addressStringChannel = Channel<Resource<AddressSearchResult>>(Channel.BUFFERED)
    override val addressStringFlow: Flow<Resource<AddressSearchResult>> =
        _addressStringChannel.receiveAsFlow()


    // Слушатель для поиска по текстовому запросу
    private val listenerForSearchByText = object : Session.SearchListener {
        override fun onSearchResponse(response: Response) {
            val geoObjects = response.collection.children.mapNotNull { it.obj }

            if (geoObjects.isNotEmpty()) {
                _addressListChannel.trySend(
                    Resource.Success(geoObjects.map { it.toAddressSearchResult() })
                )
            } else {
                _addressListChannel.trySend(Resource.ErrorEmptyData())
            }
        }

        override fun onSearchError(error: Error) {
            _addressListChannel.trySend(Resource.ErrorOther(error.toString()))
        }
    }

    override suspend fun searchAddressByString(query: String, point: GeoPoint) {
        val yPoint = point.toYandexPoint() as Point
        val geometry = Geometry.fromPoint(yPoint)
        val searchOptions = SearchOptions()
        _addressListChannel.trySend(Resource.Loading())

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
                _addressStringChannel.trySend(Resource.Success(geoObj.toAddressSearchResult()))
            } else {
                _addressStringChannel.trySend(Resource.ErrorEmptyData())
            }
        }

        override fun onSearchError(error: Error) {
            _addressStringChannel.trySend(Resource.ErrorOther(error.toString()))
        }
    }

    override suspend fun getAddressFromPoint(point: GeoPoint) {
        val yPoint = point.toYandexPoint() as Point
        val searchOptions = SearchOptions()
        _addressStringChannel.trySend(Resource.Loading())

        withContext(Dispatchers.Main) {
            session?.cancel()
            session = searchManager.submit(
                yPoint,
                DEFAULT_ZOOM_FOR_SEARCH,
                searchOptions,
                listener
            )
        }
    }

    private companion object {
        const val DEFAULT_ZOOM_FOR_SEARCH = 16
    }
}