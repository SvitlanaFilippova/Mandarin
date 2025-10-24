package com.mandarinkafe.mandarin.features.address.presentation.ui.screen

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.navigation.NavController
import com.mandarinkafe.mandarin.MR
import com.mandarinkafe.mandarin.core.domain.models.Address
import com.mandarinkafe.mandarin.features.address.data.Mapper.toGeoPoint
import com.mandarinkafe.mandarin.features.address.data.Mapper.toYandexPoint
import com.mandarinkafe.mandarin.features.address.presentation.ui.components.HandleAddressEffects
import com.mandarinkafe.mandarin.features.address.presentation.ui.components.SearchByTextResults
import com.mandarinkafe.mandarin.features.address.presentation.viewmodel.AddressContract
import com.mandarinkafe.mandarin.features.order.presentation.ui.components.LocationIcon
import com.mandarinkafe.mandarin.shared.presentation.viewmodel.rememberAddressViewModel
import com.mandarinkafe.mandarin.util.Constants
import com.mandarinkafe.mandarin.util.Constants.MIN_LINES_FOR_ADDRESS_INPUT
import com.mandarinkafe.mandarin.util.ConstantsMap.MAP_DEFAULT_ZOOM_FOR_ADDRESS_SCREEN
import com.mandarinkafe.mandarin.util.presentation.ui.components.map.BindMapViewToLifecycle
import com.mandarinkafe.mandarin.util.presentation.ui.components.map.MapWithButtons
import com.mandarinkafe.mandarin.util.presentation.ui.components.map.RequestLocationPermission
import com.mandarinkafe.mandarin.util.presentation.ui.components.map.moveCamera
import com.mandarinkafe.mandarin.util.presentation.ui.components.textfields.MyTextField
import com.yandex.mapkit.mapview.MapView

@Composable
actual fun AddressMapContentScreen(
    navController: NavController,
    initAddress: Address?,
    returnToRoute: String,
) {
    val viewModel = rememberAddressViewModel()
    val state by viewModel.state.collectAsState()
    val onEvent = viewModel::onEvent

    // если был передан адрес для редактирования - передаём его дальше в VM
    initAddress?.let {
        LaunchedEffect(Unit) {
            viewModel.onEvent(AddressContract.AddressEvent.SetInitAddress(initAddress))
        }
    }

    var mapView by remember { mutableStateOf<MapView?>(null) }

    val initLocation = state.initPinPoint?.toYandexPoint()
    val userLocation = state.userLocation?.toYandexPoint()

    LaunchedEffect(initLocation, userLocation) {
        if (initLocation != null) {
            moveCamera(initLocation, mapView)
        } else {
            moveCamera(userLocation, mapView)
        }
    }

    RequestLocationPermission(
        onGranted = { onEvent(AddressContract.AddressEvent.RequestAddress) }
    )

    var searchResultsBeVisible by remember { mutableStateOf(false) }
    var mapShouldBeVisible by remember { mutableStateOf(true) }
    val keyboardController =
        LocalSoftwareKeyboardController.current // Добавляем на карту зоны доставки
    val addressValue = state.displayAddress ?: ""
    val onValueChange: (String) -> Unit = {
        if (it.isNotBlank()) {
            onEvent(AddressContract.AddressEvent.ChangeSearchQuery(it))
            searchResultsBeVisible = true
        } else {
            onEvent(AddressContract.AddressEvent.ChangeSearchQuery(it))
            searchResultsBeVisible = false
        }
    }


    // Строка с адресом
    MyTextField(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(
                animationSpec = tween(durationMillis = Constants.ANIMATION_DURATION_FAST)
            ),
        minLines = MIN_LINES_FOR_ADDRESS_INPUT,
        value = addressValue,
        labelRes = MR.strings.street_and_building,
        onValueChange = { onValueChange(it) },
        leadingIcon = { LocationIcon(enabled = false) }
    )
    // Родительский контейнер для отображения результатов поиска поверх карты
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
                    deliveryAreas = allDeliveryAreas,
                    displayAddress = displayAddress,
                    deliveryArea = currentDeliveryArea,
                    isLoading = fetchAddressInProgress,
                    locationChosen = locationChosen,
                    isError = error != null,
                    onMapReady = { mapView = it },
                    onCameraMoved = { onEvent(AddressContract.AddressEvent.CameraMoved(it.toGeoPoint())) },
                    onDeliverHereClick = {
                        onEvent(AddressContract.AddressEvent.GoToAddressDetails)
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
                    moveCamera(
                        point = it.point?.toYandexPoint(),
                        mapView = mapView,
                        zoom = MAP_DEFAULT_ZOOM_FOR_ADDRESS_SCREEN
                    )

                },
                onDismiss = {
                    searchResultsBeVisible = false
                    keyboardController?.hide()
                },
            )
        }

    }

    HandleAddressEffects(
        effectFlow = viewModel.effect,
        navController = navController,
        returnToRoute = returnToRoute
    )

    BindMapViewToLifecycle(mapView)
}
