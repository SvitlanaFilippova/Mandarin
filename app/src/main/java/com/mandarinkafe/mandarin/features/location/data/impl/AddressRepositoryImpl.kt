package com.mandarinkafe.mandarin.features.location.data.impl

import android.util.Log
import com.mandarinkafe.mandarin.features.location.domain.api.AddressRepository
import com.mandarinkafe.mandarin.features.location.domain.models.GeoPoint
import com.mandarinkafe.mandarin.features.location.domain.models.toYandexPoint
import com.yandex.mapkit.geometry.Geometry
import com.yandex.mapkit.search.Response
import com.yandex.mapkit.search.SearchManager
import com.yandex.mapkit.search.SearchOptions
import com.yandex.mapkit.search.Session
import com.yandex.runtime.Error
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class AddressRepositoryImpl(
    private val searchManager: SearchManager
) : AddressRepository {

    override suspend fun getAddressFromPoint(point: GeoPoint): String? =
        suspendCoroutine { cont ->
            Log.d(
                "DEBUG LOCATION",
                "AddressRepositoryImpl.getAddressFromPoint called with $point"
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
                    yPoint,
                    16,
                    searchOptions,
                    searchListener
                )
                Log.d("DEBUG LOCATION", "searchManager.submit() returned session: $session")

            } catch (t: Throwable) {
                Log.e("DEBUG LOCATION", "Exception calling searchManager.submit()", t)
                cont.resume(null)
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

