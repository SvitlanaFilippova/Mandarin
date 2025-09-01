package com.mandarinkafe.mandarin.features.more.presentation.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mandarinkafe.mandarin.R.drawable
import com.mandarinkafe.mandarin.R.string
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.features.address.address.presentation.ui.components.MapWithButtons
import com.mandarinkafe.mandarin.features.more.presentation.ui.components.DeliveryZonesSection
import com.mandarinkafe.mandarin.features.more.presentation.viewmodel.DeliveryContract.DeliveryEvent
import com.mandarinkafe.mandarin.features.more.presentation.viewmodel.DeliveryViewModel
import com.mandarinkafe.mandarin.util.Constants.MAP_ANIMATION_DURATION
import com.mandarinkafe.mandarin.util.Constants.MAP_DEFAULT_AZIMUTH
import com.mandarinkafe.mandarin.util.Constants.MAP_DEFAULT_TILT
import com.mandarinkafe.mandarin.util.Constants.MAP_DEFAULT_ZOOM_FOR_DELIVERY_SCREEN
import com.mandarinkafe.mandarin.util.presentation.ui.components.BindMapViewToLifecycle
import com.mandarinkafe.mandarin.util.presentation.ui.components.InfoCard
import com.mandarinkafe.mandarin.util.presentation.ui.components.LoadingScreen
import com.mandarinkafe.mandarin.util.presentation.ui.components.ScreenTitleWithBackButton
import com.yandex.mapkit.Animation
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.mapview.MapView

@Composable
fun DeliveryScreen(
    viewModel: DeliveryViewModel = hiltViewModel(),
    onBackClick: () -> Boolean
) {
    val state by viewModel.state.collectAsState()
    var mapView by remember { mutableStateOf<MapView?>(null) }
    val initLocation = state.initPinPoint
    val onEvent = viewModel::onEvent
    var mapShouldBeVisible by remember { mutableStateOf(true) }

    LaunchedEffect(initLocation, mapView) {
        moveCamera(initLocation, mapView)
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        if (state.isLoading) {
            LoadingScreen()
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = Dimens.MarginSmall8)
            ) {
                item {
                    // Заголовок экрана и стрелка "Назад"
                    ScreenTitleWithBackButton(
                        name = stringResource(id = string.more_delivery_info),
                        onBackClick = {
                            onBackClick()
                            mapShouldBeVisible = false
                        },
                    )
                }

                item { Spacer(modifier = Modifier.height(Dimens.MarginSmall8)) }

                item {
                    // Время доставки
                    InfoCard(
                        iconPainter = painterResource(drawable.ic_clock),
                        title = stringResource(string.delivery_duration_title),
                        lines = listOf(
                            stringResource(string.delivery_duration_value) to null
                        )
                    )
                }
                item { Spacer(modifier = Modifier.height(Dimens.MarginSmall8)) }

                if (mapShouldBeVisible) {
                    item {
                        // Карта
                        with(state) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(500.dp)
                            ) {
                                MapWithButtons(
                                    mapView = mapView,
                                    deliveryAreas = deliveryAreas,
                                    displayAddress = displayAddress,
                                    deliveryArea = deliveryArea,
                                    isLoading = fetchAddressInProgress,
                                    isError = error != null,
                                    onMapReady = { mapView = it },
                                    onCameraMoved = { onEvent(DeliveryEvent.CameraMoved(it)) },
                                    onBackToInitLocationClick = {
                                        moveCamera(
                                            point = initLocation,
                                            mapView = mapView
                                        )
                                    },
                                    locationChosen = locationChosen
                                )
                            }
                        }
                    }
                }
                item { Spacer(modifier = Modifier.height(Dimens.MarginSmall8)) }
                item {
                    // Все зоны доставки
                    DeliveryZonesSection(
                        deliveryAreas = state.deliveryAreas
                    )
                }

                item { Spacer(modifier = Modifier.height(Dimens.MarginSmall8)) }

            }

        }
        BindMapViewToLifecycle(mapView)
    }
}

private fun moveCamera(point: Point?, mapView: MapView?) {
    if (point != null) {
        mapView?.mapWindow?.map?.move(
            CameraPosition(
                point,
                MAP_DEFAULT_ZOOM_FOR_DELIVERY_SCREEN,
                MAP_DEFAULT_AZIMUTH,
                MAP_DEFAULT_TILT
            ),
            Animation(Animation.Type.SMOOTH, MAP_ANIMATION_DURATION),
            null
        )
    }
}
