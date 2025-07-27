package com.mandarinkafe.mandarin.features.address.address.data.impl

import android.util.Log
import com.mandarinkafe.mandarin.core.domain.models.GeoPoint
import com.mandarinkafe.mandarin.features.address.address.domain.api.AddressRepository
import com.mandarinkafe.mandarin.features.address.address.domain.models.AddressSearchResult
import com.mandarinkafe.mandarin.features.address.address.domain.models.toAddressSearchResult
import com.mandarinkafe.mandarin.features.address.address.domain.models.toYandexPoint
import com.mandarinkafe.mandarin.util.Resource
import com.yandex.mapkit.GeoObject
import com.yandex.mapkit.geometry.Geometry
import com.yandex.mapkit.search.Response
import com.yandex.mapkit.search.SearchManager
import com.yandex.mapkit.search.SearchOptions
import com.yandex.mapkit.search.Session
import com.yandex.mapkit.search.ToponymObjectMetadata
import com.yandex.runtime.Error
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext

class AddressRepositoryImpl(
    private val searchManager: SearchManager
) : AddressRepository {
    private var session: Session? = null
    private val _addressStringFlow =
        MutableStateFlow<Resource<AddressSearchResult>>(Resource.Idle())
    override val addressStringFlow: StateFlow<Resource<AddressSearchResult>> =
        _addressStringFlow.asStateFlow()

    private val _addressListFlow =
        MutableStateFlow<Resource<List<AddressSearchResult>>>(Resource.Idle())
    override val addressListFlow: StateFlow<Resource<List<AddressSearchResult>>> =
        _addressListFlow.asStateFlow()

    private val logTag = "DEBUG MapKitFactory geoObject"

    // Слушатель для обратного геокодинга
    private val listener = object : Session.SearchListener {
        override fun onSearchResponse(response: Response) {
            val geoObj = response.collection.children.firstOrNull()?.obj
            if (geoObj != null) {
                _addressStringFlow.value = Resource.Success(geoObj.toAddressSearchResult())
            } else {
                _addressStringFlow.value = Resource.ErrorEmptyData()
            }
        }

        override fun onSearchError(error: Error) {
            Log.d(logTag, "onSearchError: $error")
            _addressStringFlow.value = Resource.ErrorOther(error.toString())
        }
    }

    // Слушатель для поиска по текстовому запросу
    private val listenerForSearchByText = object : Session.SearchListener {
        override fun onSearchResponse(response: Response) {
            val geoObjects = response.collection.children.mapNotNull { it.obj }

            if (geoObjects.isNotEmpty()) {
                geoObjects.forEach { geoObj ->
                    Log.d(logTag, "geoObject dump: ${geoObj.dumpInfo()}")
                }

                _addressListFlow.value =
                    Resource.Success(geoObjects.map { it.toAddressSearchResult() })
            } else {
                _addressListFlow.value = Resource.ErrorEmptyData()
            }
        }

        override fun onSearchError(error: Error) {
            Log.d(logTag, "onSearchError: $error")
            _addressListFlow.value = Resource.ErrorOther(error.toString())
        }
    }

    override suspend fun getAddressFromPoint(point: GeoPoint) {
        val yPoint = point.toYandexPoint()
        val searchOptions = SearchOptions()
        _addressStringFlow.value = Resource.Loading()

        withContext(Dispatchers.Main) {
            session?.cancel()
            session = searchManager.submit(
                yPoint,
                16,
                searchOptions,
                listener
            )
        }
    }

    override suspend fun searchAddressByString(query: String, point: GeoPoint) {
        val yPoint = point.toYandexPoint()
        val geometry = Geometry.fromPoint(yPoint)
        val searchOptions = SearchOptions()
        _addressListFlow.value = Resource.Loading()

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

    private fun GeoObject.dumpInfo(): String {
        // имя POI
        val poiName = name?.takeIf { it.isNotBlank() } ?: "—"

        // первая геометрия: берём её точку, если есть
        val firstPoint = geometry.firstOrNull()?.point
        val pointInfo = if (firstPoint != null) {
            "lat=${firstPoint.latitude}, lon=${firstPoint.longitude}"
        } else {
            "no-geometry"
        }

        // полный адрес из метаданных
        val toponym = metadataContainer
            .getItem(ToponymObjectMetadata::class.java)
            ?.address
        val fullAddress = toponym?.formattedAddress ?: "—"

        // компоненты адреса
        val componentsInfo = toponym?.components
            ?.joinToString { component ->
                component.kinds.joinToString("|") + "=" + component.name
            } ?: "[]"

        return buildString {
            append("name=$poiName; ")
            append("point=[$pointInfo]; ")
            append("fullAddress='$fullAddress'; ")
            append("components=[$componentsInfo]")
        }
    }
}