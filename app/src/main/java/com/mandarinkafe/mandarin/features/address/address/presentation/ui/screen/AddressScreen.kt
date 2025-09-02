package com.mandarinkafe.mandarin.features.address.address.presentation.ui.screen

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.mandarinkafe.mandarin.R
import com.mandarinkafe.mandarin.core.domain.models.Address
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.features.address.address.domain.models.toYandexPoint
import com.mandarinkafe.mandarin.features.address.address.presentation.ui.components.HandleAddressEffects
import com.mandarinkafe.mandarin.features.address.address.presentation.ui.components.MapWithButtons
import com.mandarinkafe.mandarin.features.address.address.presentation.ui.components.RequestLocationPermission
import com.mandarinkafe.mandarin.features.address.address.presentation.ui.components.SearchByTextResults
import com.mandarinkafe.mandarin.features.address.address.presentation.viewmodel.AddressContract.AddressEvent
import com.mandarinkafe.mandarin.features.address.address.presentation.viewmodel.AddressContract.AddressEvent.CameraMoved
import com.mandarinkafe.mandarin.features.address.address.presentation.viewmodel.AddressViewModel
import com.mandarinkafe.mandarin.features.order.presentation.ui.components.LocationIcon
import com.mandarinkafe.mandarin.util.Constants
import com.mandarinkafe.mandarin.util.Constants.MAP_ANIMATION_DURATION
import com.mandarinkafe.mandarin.util.Constants.MAP_DEFAULT_AZIMUTH
import com.mandarinkafe.mandarin.util.Constants.MAP_DEFAULT_TILT
import com.mandarinkafe.mandarin.util.Constants.MAP_DEFAULT_ZOOM_FOR_ADDRESS_SCREEN
import com.mandarinkafe.mandarin.util.Constants.MIN_LINES_FOR_ADDRESS_INPUT
import com.mandarinkafe.mandarin.util.presentation.ui.components.BindMapViewToLifecycle
import com.mandarinkafe.mandarin.util.presentation.ui.components.MyTextField
import com.yandex.mapkit.Animation
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.mapview.MapView

@Composable
fun AddressMapScreen(
    viewModel: AddressViewModel = hiltViewModel(),
    navController: NavHostController,
    initAddress: Address?,
    returnToRoute: String
) {
    val state by viewModel.state.collectAsState()
    val onEvent = viewModel::onEvent

    // если был передан адрес для редактирования - передаём его дальше в VM
    initAddress?.let {
        LaunchedEffect(Unit) {
            viewModel.onEvent(AddressEvent.SetInitAddress(initAddress))
        }
    }

    var mapView by remember { mutableStateOf<MapView?>(null) }
    val initLocation = state.initPinPoint
    val userLocation = state.userLocation
    LaunchedEffect(initLocation, userLocation) {
        if (initLocation != null) {
            moveCamera(initLocation, mapView)
        } else {
            moveCamera(userLocation, mapView)
        }
    }
    RequestLocationPermission(
        onGranted = { onEvent(AddressEvent.RequestAddress) }
    )
    var searchResultsBeVisible by remember { mutableStateOf(false) }
    var mapShouldBeVisible by remember { mutableStateOf(true) }
    val keyboardController =
        LocalSoftwareKeyboardController.current // Добавляем на карту зоны доставки
    val addressValue = state.displayAddress ?: ""
    val onValueChange: (String) -> Unit = {
        if (it.isNotBlank()) {
            onEvent(AddressEvent.ChangeSearchQuery(it))
            searchResultsBeVisible = true
        } else {
            onEvent(AddressEvent.ChangeSearchQuery(it))
            searchResultsBeVisible = false
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(Dimens.MarginSmall8)
    ) {
        // Строка с адресом
        MyTextField(
            modifier = Modifier
                .fillMaxWidth()
                .animateContentSize(
                    animationSpec = tween(durationMillis = Constants.ANIMATION_DURATION_FAST)
                ),
            minLines = MIN_LINES_FOR_ADDRESS_INPUT,
            value = addressValue,
            labelRes = R.string.street_and_building,
            onValueChange = { onValueChange(it) },
            leadingIcon = { LocationIcon(enabled = false) }
        )
        // Родительский контейнер для отображения результатов поиска поваерх карты
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            // Контейнер для карты и её элементов управления
            if (mapShouldBeVisible) {
                with(state) {
                    val onBackToInitClick = initLocation?.let { { moveCamera(it, mapView) } }
                    val onBackToUserClick = userLocation?.let { { moveCamera(it, mapView) } }

                    MapWithButtons(
                        mapView = mapView,
                        deliveryAreas = deliveryAreas,
                        displayAddress = displayAddress,
                        deliveryArea = deliveryArea,
                        isLoading = fetchAddressInProgress,
                        locationChosen = locationChosen,
                        isError = error != null,
                        onMapReady = { mapView = it },
                        onCameraMoved = { onEvent(CameraMoved(it)) },
                        onDeliverHereClick = {
                            onEvent(AddressEvent.GoToAddressDetails)
                            mapShouldBeVisible = false
                        },
                        onBackToInitLocationClick = onBackToInitClick,
                        onBackToUserLocationClick = onBackToUserClick
                    )
                }
            }

            if (searchResultsBeVisible) {
                SearchByTextResults(
                    modifier = Modifier
                        .align(Alignment.TopCenter),
                    isLoading = state.searchInProgress,
                    data = state.searchResults,
                    searchError = state.searchError,
                    onItemClick = {
                        searchResultsBeVisible = false
                        keyboardController?.hide()
                        moveCamera(it.point?.toYandexPoint(), mapView)

                    },
                    onDismiss = {
                        searchResultsBeVisible = false
                        keyboardController?.hide()
                    },
                )
            }

        }
    }
    HandleAddressEffects(
        effectFlow = viewModel.effect,
        navController = navController,
        returnToRoute = returnToRoute
    )

    BindMapViewToLifecycle(mapView)
}

private fun moveCamera(point: Point?, mapView: MapView?) {
    if (point != null) {
        mapView?.mapWindow?.map?.move(
            CameraPosition(
                point,
                MAP_DEFAULT_ZOOM_FOR_ADDRESS_SCREEN,
                MAP_DEFAULT_AZIMUTH,
                MAP_DEFAULT_TILT
            ),
            Animation(Animation.Type.SMOOTH, MAP_ANIMATION_DURATION),
            null
        )
    }
}
