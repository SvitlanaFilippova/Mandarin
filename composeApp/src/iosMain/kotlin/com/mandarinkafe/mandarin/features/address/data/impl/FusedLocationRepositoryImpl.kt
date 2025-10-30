package com.mandarinkafe.mandarin.features.address.data.impl

import com.mandarinkafe.mandarin.core.domain.models.GeoPoint
import com.mandarinkafe.mandarin.features.address.domain.api.FusedLocationRepository
import com.mandarinkafe.mandarin.util.Resource
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import kotlinx.coroutines.suspendCancellableCoroutine
import platform.CoreLocation.CLLocation
import platform.CoreLocation.CLLocationManager
import platform.CoreLocation.CLLocationManagerDelegateProtocol
import platform.CoreLocation.kCLAuthorizationStatusAuthorizedAlways
import platform.CoreLocation.kCLAuthorizationStatusAuthorizedWhenInUse
import platform.darwin.NSObject
import platform.Foundation.NSError
import kotlin.coroutines.resume

class FusedLocationRepositoryImpl : FusedLocationRepository {
	@OptIn(ExperimentalForeignApi::class)
	override suspend fun getCurrentLocation(): Resource<GeoPoint> {
		return suspendCancellableCoroutine { continuation ->
			val manager = CLLocationManager()

			// На iOS разрешения запрашиваются в UI-слое, здесь только использование
			val status = CLLocationManager.authorizationStatus()
			if (status != kCLAuthorizationStatusAuthorizedWhenInUse && status != kCLAuthorizationStatusAuthorizedAlways) {
				continuation.resume(Resource.ErrorOther("Location permission not granted"))
				return@suspendCancellableCoroutine
			}

			var didResume = false

			val delegate = object : NSObject(), CLLocationManagerDelegateProtocol {
				override fun locationManager(manager: CLLocationManager, didUpdateLocations: List<*>) {
					if (didResume) return
					val last = didUpdateLocations.lastOrNull() as? CLLocation
					if (last != null) {
						didResume = true
						manager.stopUpdatingLocation()
						manager.delegate = null
						val (lat, lon) = last.coordinate.let { coord ->
							var latTmp = 0.0
							var lonTmp = 0.0
							coord.useContents {
								latTmp = latitude
								lonTmp = longitude
							}
							latTmp to lonTmp
						}
						continuation.resume(
							Resource.Success(
								GeoPoint(
									latitude = lat,
									longitude = lon
								)
							)
						)
					}
				}

				override fun locationManager(manager: CLLocationManager, didFailWithError: NSError) {
					if (didResume) return
					didResume = true
					manager.stopUpdatingLocation()
					manager.delegate = null
					continuation.resume(Resource.ErrorOther("Failed to get location"))
				}
			}

			manager.delegate = delegate

			// Пытаемся получить одноточечное обновление местоположения
			// requestLocation() доступен и отдаст один апдейт через didUpdateLocations
			// В качестве запасного варианта включаем startUpdatingLocation
			try {
				manager.requestLocation()
			} catch (_: Throwable) {
				manager.startUpdatingLocation()
			}

			continuation.invokeOnCancellation {
				manager.stopUpdatingLocation()
				manager.delegate = null
			}
		}
	}
}
