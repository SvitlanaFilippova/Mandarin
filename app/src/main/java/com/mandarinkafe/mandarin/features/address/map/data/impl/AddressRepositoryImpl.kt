package com.mandarinkafe.mandarin.features.address.map.data.impl

import android.util.Log
import com.mandarinkafe.mandarin.core.domain.models.GeoPoint
import com.mandarinkafe.mandarin.features.address.map.domain.api.AddressRepository
import com.mandarinkafe.mandarin.features.address.map.domain.models.toYandexPoint
import com.mandarinkafe.mandarin.util.Resource
import com.yandex.mapkit.GeoObject
import com.yandex.mapkit.search.Address
import com.yandex.mapkit.search.Response
import com.yandex.mapkit.search.SearchManager
import com.yandex.mapkit.search.SearchOptions
import com.yandex.mapkit.search.SearchType
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
    private val _addressFlow = MutableStateFlow<Resource<String>>(Resource.Idle())
    override val addressFlow: StateFlow<Resource<String>> = _addressFlow.asStateFlow()
    private val logTag = "DEBUG MapKitFactory"

    private val listener = object : Session.SearchListener {
        override fun onSearchResponse(response: Response) {
            val geoObj = response.collection.children.firstOrNull()?.obj
            val displayName = formatDisplayName(geoObj)

            if (displayName != null) {
                _addressFlow.value = Resource.Success(displayName)
            } else {
                Log.d(logTag, "Empty address from response")
                _addressFlow.value = Resource.ErrorEmptyData()
            }
        }

        override fun onSearchError(error: Error) {
            Log.d(logTag, "onSearchError: $error")
            _addressFlow.value = Resource.ErrorOther(error.toString())
        }
    }

    override suspend fun getAddressFromPoint(point: GeoPoint) {
        val yPoint = point.toYandexPoint()
        val searchOptions = SearchOptions().apply {
            searchTypes = SearchType.GEO.value
        }
        _addressFlow.value = Resource.Loading()

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

    /**
     * Формирует строку вида "<POI‑имя>, <населённый пункт>" или просто "<населённый пункт>",
     * или `null`, если ничего не найдено.
     */
    private fun formatDisplayName(geoObj: GeoObject?): String? {
        if (geoObj == null) return null

        val poiName = geoObj.name?.takeIf { it.isNotBlank() }

        val address = geoObj.metadataContainer
            .getItem(ToponymObjectMetadata::class.java)
            ?.address

        val locality = address?.components
            ?.firstOrNull { it.kinds.contains(Address.Component.Kind.LOCALITY) }
            ?.name

        val components = mutableListOf<String>()

        if (poiName != null && poiName != locality) {
            components.add(poiName)
        }

        if (!locality.isNullOrBlank()) {
            components.add(locality)
        }

        return components.joinToString(", ").ifBlank { null }
    }
}

