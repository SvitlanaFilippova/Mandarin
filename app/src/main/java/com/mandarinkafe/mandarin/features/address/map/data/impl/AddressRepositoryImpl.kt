package com.mandarinkafe.mandarin.features.address.map.data.impl

import android.util.Log
import com.mandarinkafe.mandarin.features.address.map.domain.api.AddressRepository
import com.mandarinkafe.mandarin.features.address.map.domain.models.GeoPoint
import com.mandarinkafe.mandarin.features.address.map.domain.models.toYandexPoint
import com.mandarinkafe.mandarin.util.Resource
import com.yandex.mapkit.geometry.Geometry
import com.yandex.mapkit.search.Response
import com.yandex.mapkit.search.SearchManager
import com.yandex.mapkit.search.SearchOptions
import com.yandex.mapkit.search.Session
import com.yandex.runtime.Error
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class AddressRepositoryImpl(
    private val searchManager: SearchManager
) : AddressRepository {
    private var session: Session? = null
    private val _addressFlow = MutableStateFlow<Resource<String>>(Resource.Idle())
    override val addressFlow: StateFlow<Resource<String>> = _addressFlow.asStateFlow()

    override suspend fun getAddressFromPoint(point: GeoPoint) {
        val yPoint = point.toYandexPoint()
        val searchOptions = SearchOptions()

        val listener = object : Session.SearchListener {
            override fun onSearchResponse(response: Response) {
                val name = response.collection.children
                    .firstOrNull()
                    ?.obj
                    ?.name

                if (name != null) {
                    _addressFlow.value = Resource.Success(name)
                } else
                    _addressFlow.value = Resource.ErrorEmptyData()
            }

            override fun onSearchError(error: Error) {
                Log.e("DEBUG LOCATION", "onSearchError: $error")
                _addressFlow.value = Resource.ErrorOther(error.toString())
            }
        }
        if (session == null) {
            session = withContext(Dispatchers.Main) {
                searchManager.submit(
                    yPoint,
                    16,
                    searchOptions,
                    listener
                )
            }
        } else {
            session.let {
                it?.resubmit(listener)
            }
        }
    }

    override suspend fun searchAddressByString(query: String, point: GeoPoint) =
        suspendCoroutine { cont ->
            Log.d(
                "DEBUG LOCATION",
                "AddressRepositoryImpl searchAddressByString called with query: $query"
            )
            val yPoint = point.toYandexPoint()
            val searchOptions = SearchOptions()
            val searchListener = object : Session.SearchListener {
                override fun onSearchResponse(response: Response) {
                    Log.d("DEBUG LOCATION", "onSearchResponse: received $response")

                    val name = response.collection.children
                        .firstOrNull()
                        ?.obj
                        ?.name

                    Log.d("DEBUG LOCATION", "Parsed address: $name")
                    cont.resume(name)
                }

                override fun onSearchError(p0: Error) {
                    Log.e("DEBUG LOCATION", "onSearchError: $p0")
                    cont.resume(null)
                }
            }

            try {
                val session = searchManager.submit(
                    query,
                    Geometry.fromPoint(yPoint),
                    searchOptions,
                    searchListener
                )
                Log.d("DEBUG LOCATION", "searchManager.submit() returned session: $session")

            } catch (t: Throwable) {
                Log.e("DEBUG LOCATION", "Exception calling searchManager.submit()", t)
                cont.resume(null)
            }
        }
}

