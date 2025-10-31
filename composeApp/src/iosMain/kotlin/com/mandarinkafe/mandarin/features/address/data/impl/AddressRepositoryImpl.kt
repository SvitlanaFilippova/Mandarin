package com.mandarinkafe.mandarin.features.address.data.impl

import YandexMapKit.YMKGeoObjectCollectionItem
import YandexMapKit.YMKGeometry
import YandexMapKit.YMKMapKit
import YandexMapKit.YMKPoint
import YandexMapKit.YMKSearchFactory
import YandexMapKit.YMKSearchManager
import YandexMapKit.YMKSearchManagerType
import YandexMapKit.YMKSearchOptions
import YandexMapKit.YMKSearchResponse
import YandexMapKit.YMKSearchSession
import YandexMapKit.YMKSearchTypeBiz
import YandexMapKit.YMKSearchTypeGeo
import YandexMapKit.sharedInstance
import com.mandarinkafe.mandarin.core.domain.models.GeoPoint
import com.mandarinkafe.mandarin.features.address.data.Mapper.toAddressSearchResult
import com.mandarinkafe.mandarin.features.address.data.Mapper.toYandexPoint
import com.mandarinkafe.mandarin.features.address.domain.api.AddressRepository
import com.mandarinkafe.mandarin.features.address.domain.models.AddressSearchResult
import com.mandarinkafe.mandarin.util.Resource
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import platform.Foundation.NSError
import platform.Foundation.NSNumber
import platform.Foundation.numberWithDouble
import platform.Foundation.setValue

@OptIn(ExperimentalForeignApi::class)
class AddressRepositoryImpl : AddressRepository {
    private val searchManager: YMKSearchManager by lazy {
        YMKMapKit.sharedInstance()
        YMKSearchFactory.instance().createSearchManagerWithSearchManagerType(
            YMKSearchManagerType.YMKSearchManagerTypeCombined
        )
    }

    private var session: YMKSearchSession? = null

    private val _addressListChannel =
        Channel<Resource<List<AddressSearchResult>>>(Channel.BUFFERED)
    override val addressListFlow: Flow<Resource<List<AddressSearchResult>>> =
        _addressListChannel.receiveAsFlow()

    private val _addressStringChannel =
        Channel<Resource<AddressSearchResult>>(Channel.BUFFERED)
    override val addressStringFlow: Flow<Resource<AddressSearchResult>> =
        _addressStringChannel.receiveAsFlow()

    // --- Поиск по строке ---
    override suspend fun searchAddressByString(query: String, point: GeoPoint) {
        _addressListChannel.trySend(Resource.Loading())

        val yPoint = point.toYandexPoint()
        val geometry = createGeometry(yPoint)
        val options = createSearchOptions(userPosition = yPoint)
        // Отменяем предыдущую сессию, если была
        session?.cancel()

        session = searchManager.submitWithText(
            query,
            geometry,
            options
        ) { response: YMKSearchResponse?, error: NSError? ->
            when {
                error != null -> {
                    _addressListChannel.trySend(
                        Resource.ErrorOther(
                            error.localizedDescription
                        )
                    )
                }

                response == null -> {
                    _addressListChannel.trySend(Resource.ErrorOther("No response"))
                }

                else -> {
                    val geoObjects = response.collection.children
                        .mapNotNull { (it as? YMKGeoObjectCollectionItem)?.obj }

                    if (geoObjects.isNotEmpty()) {
                        val mapped = geoObjects.map { it.toAddressSearchResult() }
                        _addressListChannel.trySend(Resource.Success(mapped))
                    } else {
                        _addressListChannel.trySend(Resource.ErrorEmptyData())
                    }
                }
            }
        }
    }

    override suspend fun getAddressFromPoint(point: GeoPoint) {
        _addressStringChannel.trySend(Resource.Loading())

        val yPoint = point.toYandexPoint()
        val options = YMKSearchOptions().apply {
            geometry = true
        }

        session?.cancel()

        session = searchManager.submitWithPoint(
            yPoint,
            NSNumber.numberWithDouble(DEFAULT_ZOOM_FOR_SEARCH),
            options
        ) { response: YMKSearchResponse?, error: NSError? ->
            when {
                error != null -> {
                    _addressStringChannel.trySend(
                        Resource.ErrorOther(
                            error.localizedDescription
                        )
                    )
                }

                response == null -> {
                    _addressStringChannel.trySend(Resource.ErrorOther("No response"))
                }

                else -> {
                    val items = response.collection.children
                        .filterIsInstance<YMKGeoObjectCollectionItem>()

                    val geoObj = items.firstOrNull()?.obj
                    if (geoObj != null) {
                        _addressStringChannel.trySend(Resource.Success(geoObj.toAddressSearchResult()))
                    } else {
                        _addressStringChannel.trySend(Resource.ErrorEmptyData())
                    }
                }
            }
        }
    }

    private fun createGeometry(point: YMKPoint): YMKGeometry {
        YMKGeometry()
        val geometry = YMKGeometry()
        geometry.setValue(point, forKey = "point")
        return geometry
    }

    private fun createSearchOptions(userPosition: YMKPoint?): YMKSearchOptions {
        YMKSearchOptions()
        val options = YMKSearchOptions()
        options.setGeometry(true)
        options.setDisableSpellingCorrection(false)
        options.setSearchTypes(YMKSearchTypeBiz or YMKSearchTypeGeo)
        userPosition?.let { options.setUserPosition(it) }
        return options
    }

    private companion object {
        const val DEFAULT_ZOOM_FOR_SEARCH = 16.0
    }
}
