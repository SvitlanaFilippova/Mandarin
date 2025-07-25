package com.mandarinkafe.mandarin.features.address.map.presentation.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavHostController
import com.mandarinkafe.mandarin.R
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.features.address.map.presentation.ui.components.ChosenLocationPin
import com.mandarinkafe.mandarin.features.address.map.presentation.ui.components.DeliveryAreaInfo
import com.mandarinkafe.mandarin.features.address.map.presentation.ui.components.DeliveryAreasOnMap
import com.mandarinkafe.mandarin.features.address.map.presentation.ui.components.HandleAddressEffects
import com.mandarinkafe.mandarin.features.address.map.presentation.ui.components.RequestLocationPermission
import com.mandarinkafe.mandarin.features.address.map.presentation.viewmodel.AddressContract.AddressEvent
import com.mandarinkafe.mandarin.features.address.map.presentation.viewmodel.AddressContract.AddressEvent.CameraMoved
import com.mandarinkafe.mandarin.features.address.map.presentation.viewmodel.AddressContract.AddressEvent.SetVisibleRegion
import com.mandarinkafe.mandarin.features.address.map.presentation.viewmodel.AddressViewModel
import com.mandarinkafe.mandarin.features.order.presentation.ui.components.GetLocationIcon
import com.mandarinkafe.mandarin.util.Constants.MAP_ANIMATION_DURATION
import com.mandarinkafe.mandarin.util.Constants.MAP_DEFAULT_AZIMUTH
import com.mandarinkafe.mandarin.util.Constants.MAP_DEFAULT_TILT
import com.mandarinkafe.mandarin.util.Constants.MAP_DEFAULT_ZOOM
import com.mandarinkafe.mandarin.util.Constants.MIN_LINES_FOR_ADDRESS_INPUT
import com.mandarinkafe.mandarin.util.presentation.ui.components.MyTextField
import com.mandarinkafe.mandarin.util.presentation.ui.components.buttons.ButtonWithText
import com.mandarinkafe.mandarin.util.presentation.ui.components.buttons.RoundedButton
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraListener
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.mapview.MapView

@Composable
fun AddressMapScreen(
    viewModel: AddressViewModel = hiltViewModel(),
    navController: NavHostController
) {
    val state by viewModel.state.collectAsState()
    val userLocation = state.initPinLocation
    val onEvent = viewModel::onEvent
    var mapView by remember { mutableStateOf<MapView?>(null) }
    val lifecycleOwner = LocalLifecycleOwner.current

    RequestLocationPermission(
        onGranted = { onEvent(AddressEvent.RequestAddress) }
    )
    var mapShouldBeVisible by remember { mutableStateOf(true) }
    // Добавляем на карту зоны доставки
    mapView?.let {
        DeliveryAreasOnMap(
            mapView = it,
            deliveryAreas = state.deliveryAreas
        )
    }

    val cameraListener = remember {
        CameraListener { _, cameraPosition, _, finished ->
            if (finished) {
                onEvent(CameraMoved(cameraPosition.target))
                onEvent(SetVisibleRegion(mapView?.mapWindow?.map?.visibleRegion))
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(Dimens.MarginSmall8)
    ) {
        val addressValue = state.displayAddress ?: ""
        // Строка с адресом
        MyTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = Dimens.MarginSmall8)
                .clickable(onClick = {
                    onEvent(
                        AddressEvent.GoToTextSearch(
                            addressValue
                        )
                    )
                }),
            enabled = false,
            minLines = MIN_LINES_FOR_ADDRESS_INPUT,
            value = addressValue,
            labelRes = R.string.street_and_building,
            leadingIcon = { GetLocationIcon(enabled = false) }
        )

        // Контейнер для карты и её элементов управления
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(Dimens.CornerRadius8))
        )
        {
            if (mapShouldBeVisible) {
                // Карта
                AndroidView(
                    modifier = Modifier.fillMaxSize(),
                    factory = { MapView(it) },
                ) {
                    it.mapWindow.map.addCameraListener(cameraListener)
                    onEvent(SetVisibleRegion(it.mapWindow.map.visibleRegion))
                    mapView = it
                }
                if (state.displayAddress != null) {
                    // Окно с информацией о текущей зоне доставки
                    DeliveryAreaInfo(
                        modifier = Modifier.align(Alignment.TopCenter),
                        deliveryArea = state.deliveryArea
                    )
                }

                // Кнопка "Вернуться к позиции пользователя"
                RoundedButton(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(bottom = Dimens.MarginHuge64, end = Dimens.MarginSmall8),
                    onClick = { moveCamera(userLocation, mapView) },
                    painter = painterResource(R.drawable.ic_my_location),
                    contentDescriptionResId = R.string.to_my_location
                )

                // Центральный маркер
                val offset = remember { -Dimens.MapPinSize / 2 }
                ChosenLocationPin(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .offset(y = offset),
                    isLoading = state.isLoading,
                    addressFound = state.locationChosen,
                    isError = state.error != null
                )

                // Кнопка "Доставить сюда"
                ButtonWithText(
                    modifier = Modifier
                        .padding(Dimens.MarginBig20)
                        .align(Alignment.BottomCenter),
                    shouldBeActive = state.locationChosen,
                    textResID = R.string.deliver_to_this_location,
                    onClick = {
                        onEvent(AddressEvent.GoToAddressDetails)
                        mapShouldBeVisible = false
                    }
                )

            }
        }
    }

    HandleAddressEffects(
        effectFlow = viewModel.effect,
        navController = navController
    )

    LaunchedEffect(userLocation) {
        if (userLocation != null) {
            moveCamera(userLocation, mapView)
        }
    }

    // Lifecycle observer для вызова onStart/onStop у MapKitFactory
    DisposableEffect(lifecycleOwner) {
        val lifecycle = lifecycleOwner.lifecycle

        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_START -> {
                    MapKitFactory.getInstance().onStart()
                    mapView?.onStart()
                }

                Lifecycle.Event.ON_STOP -> {
                    mapView?.onStop()
                    MapKitFactory.getInstance().onStop()
                }

                else -> Unit
            }
        }
        lifecycle.addObserver(observer)
        onDispose {
            lifecycle.removeObserver(observer)
        }
    }
}

private fun moveCamera(point: Point?, mapView: MapView?) {
    if (point != null)
        mapView?.mapWindow?.map?.move(
            CameraPosition(point, MAP_DEFAULT_ZOOM, MAP_DEFAULT_AZIMUTH, MAP_DEFAULT_TILT),
            Animation(Animation.Type.SMOOTH, MAP_ANIMATION_DURATION),
            null
        )
}
