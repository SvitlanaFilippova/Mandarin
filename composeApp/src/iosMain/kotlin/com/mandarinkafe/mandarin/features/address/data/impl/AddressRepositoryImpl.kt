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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import platform.Foundation.NSError
import platform.Foundation.NSNumber
import platform.Foundation.numberWithDouble
import platform.Foundation.setValue

@OptIn(ExperimentalForeignApi::class)
class AddressRepositoryImpl(
    private val coroutineScope: CoroutineScope,
) : AddressRepository {
    private val searchManager: YMKSearchManager by lazy {
        YMKMapKit.sharedInstance()
        YMKSearchFactory.instance().createSearchManagerWithSearchManagerType(
            YMKSearchManagerType.YMKSearchManagerTypeCombined
        )
    }

    private var session: YMKSearchSession? = null

    private val _addressListFlow =
        MutableSharedFlow<Resource<List<AddressSearchResult>>>(extraBufferCapacity = 1)
    override val addressListFlow: Flow<Resource<List<AddressSearchResult>>> =
        _addressListFlow.asSharedFlow()

    private val _addressStringFlow =
        MutableSharedFlow<Resource<AddressSearchResult>>(extraBufferCapacity = 1)
    override val addressStringFlow: Flow<Resource<AddressSearchResult>> =
        _addressStringFlow.asSharedFlow()

    // --- Поиск по строке ---
    override suspend fun searchAddressByString(query: String, point: GeoPoint) {
        coroutineScope.launch {
            _addressListFlow.emit(Resource.Loading())
        }

        val yPoint = point.toYandexPoint()
        val geometry = createGeometry(yPoint)
        val options = createSearchOptions(userPosition = yPoint)

        session?.cancel()

        session = searchManager.submitWithText(
            query,
            geometry,
            options
        ) { response: YMKSearchResponse?, error: NSError? ->
            when {
                error != null -> {
                    coroutineScope.launch {
                        _addressListFlow.emit(Resource.ErrorOther(error.localizedDescription))
                    }
                }

                response == null -> {
                    coroutineScope.launch {
                        _addressListFlow.emit(Resource.ErrorOther("No response"))
                    }
                }

                else -> {
                    val geoObjects = response.collection.children
                        .mapNotNull { (it as? YMKGeoObjectCollectionItem)?.obj }

                    if (geoObjects.isNotEmpty()) {
                        val mapped = geoObjects.map { it.toAddressSearchResult() }
                        coroutineScope.launch {
                            _addressListFlow.emit(Resource.Success(mapped))
                        }
                    } else {
                        coroutineScope.launch {
                            _addressListFlow.emit(Resource.ErrorEmptyData())
                        }
                    }
                }
            }
        }
    }

    override suspend fun getAddressFromPoint(point: GeoPoint) {
        coroutineScope.launch {
            _addressStringFlow.emit(Resource.Loading())
        }

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
                    coroutineScope.launch {
                        _addressStringFlow.emit(Resource.ErrorOther(error.localizedDescription))
                    }
                }

                response == null -> {
                    coroutineScope.launch {
                        _addressStringFlow.emit(Resource.ErrorOther("No response"))
                    }
                }

                else -> {
                    val items = response.collection.children
                        .filterIsInstance<YMKGeoObjectCollectionItem>()

                    val geoObj = items.firstOrNull()?.obj
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
