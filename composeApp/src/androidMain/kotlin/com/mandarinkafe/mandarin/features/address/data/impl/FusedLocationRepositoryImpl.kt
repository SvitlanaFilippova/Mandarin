package com.mandarinkafe.mandarin.features.address.data.impl

import android.annotation.SuppressLint
import com.google.android.gms.location.FusedLocationProviderClient
import com.mandarinkafe.mandarin.core.domain.models.GeoPoint
import com.mandarinkafe.mandarin.features.address.domain.api.FusedLocationRepository
import com.mandarinkafe.mandarin.util.Resource
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class FusedLocationRepositoryImpl(private val fusedProvider: FusedLocationProviderClient) :
    FusedLocationRepository {
    @SuppressLint("MissingPermission") // проверка разрешения происходит в ЮИ слое
    override suspend fun getCurrentLocation(): Resource<GeoPoint> =
        suspendCoroutine { continuation ->
            fusedProvider.lastLocation
                .addOnSuccessListener { location ->
                    if (location != null) {
                        continuation.resume(
                            Resource.Success(
                                GeoPoint(
                                    location.latitude,
                                    location.longitude
                                )
                            )
                        )
                    } else {
                        continuation.resume(Resource.ErrorEmptyData())
                    }
                }
                .addOnFailureListener {
                    continuation.resume(Resource.ErrorEmptyData())
                }
        }
}

