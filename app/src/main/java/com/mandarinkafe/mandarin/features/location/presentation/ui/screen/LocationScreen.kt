package com.mandarinkafe.mandarin.features.location.presentation.ui.screen

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.mandarinkafe.mandarin.R
import com.mandarinkafe.mandarin.core.presentation.theme.Colors
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.features.location.presentation.ui.components.BigButtonWithText
import com.mandarinkafe.mandarin.features.location.presentation.ui.components.ChosenLocationPin
import com.mandarinkafe.mandarin.features.location.presentation.ui.components.RequestLocationPermission
import com.mandarinkafe.mandarin.features.location.presentation.viewmodel.LocationContract.LocationEffect
import com.mandarinkafe.mandarin.features.location.presentation.viewmodel.LocationContract.LocationEvent
import com.mandarinkafe.mandarin.features.location.presentation.viewmodel.LocationContract.LocationEvent.CameraMoved
import com.mandarinkafe.mandarin.features.location.presentation.viewmodel.LocationViewModel
import com.mandarinkafe.mandarin.util.Constants.MAP_ANIMATION_DURATION
import com.mandarinkafe.mandarin.util.Constants.MAP_DEFAULT_AZIMUTH
import com.mandarinkafe.mandarin.util.Constants.MAP_DEFAULT_TILT
import com.mandarinkafe.mandarin.util.Constants.MAP_DEFAULT_ZOOM
import com.mandarinkafe.mandarin.util.presentation.ui.components.MapAreas
import com.mandarinkafe.mandarin.util.presentation.ui.components.SearchBarInputField
import com.mandarinkafe.mandarin.util.presentation.ui.components.buttons.RoundedButton
import com.yandex.mapkit.Animation
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraListener
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.CameraUpdateReason
import com.yandex.mapkit.map.Map
import com.yandex.mapkit.mapview.MapView
import kotlinx.coroutines.flow.collectLatest

@Composable
fun LocationScreen(
    viewModel: LocationViewModel = hiltViewModel(),
    navController: NavHostController
) {
    val state by viewModel.state.collectAsState()
    val effectFlow = viewModel.effect
    val userLocation = state.userLocation
    val onEvent = viewModel::onEvent
    val mapView = remember { mutableStateOf<MapView?>(null) }


    RequestLocationPermission(
        onGranted = { onEvent(LocationEvent.RequestLocation) }
    )

    // Добавляем на карту зоны доставки
    mapView.value?.let {
        MapAreas(it)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        val addressValue = state.address ?: ""
        // Строка с кнопкой назад и заголовком экрана
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Стрелка "назад"
//            IconButton(
//                onClick = { onEvent(LocationEvent.GoBack) })
//            {
//                Icon(
//                    modifier = Modifier
//                        .padding(Dimens.MarginSmall8),
//                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
//                    tint = Colors.White,
//                    contentDescription = stringResource(R.string.back)
//                )
//            }

            // Строка с адресом
            SearchBarInputField(
                modifier = Modifier
                    .padding(Dimens.MarginSmall8)
                    .clickable(onClick = {
                        onEvent(LocationEvent.GoToTextSearch(addressValue))
                    }),
                query = addressValue,
                enabled = false,
                placeholderRes = R.string.address_for_delivery,
                autoFocus = false,
                leadingIcon = {
                    if (state.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.padding(Dimens.MarginSmall8),
                            color = Colors.LightGrey
                        )
                    }
                }

            )
        }
        // Контейнер для карты и её элементов управления
        Box(
            modifier = Modifier
                .fillMaxSize()
        )
        {
            // Карта
            AndroidView(
                factory = { MapView(it) },
                modifier = Modifier.fillMaxSize()
            ) { mv ->
                mapView.value = mv

                // Слушатель перемещения камеры
                mv.mapWindow.map.addCameraListener(object : CameraListener {
                    override fun onCameraPositionChanged(
                        map: Map,
                        cameraPosition: CameraPosition,
                        reason: CameraUpdateReason,
                        finished: Boolean
                    ) {
                        if (finished) {
                            Log.d(
                                "DEBUG LOCATION",
                                "onCameraPositionChanged finished, calling VM, target:- ${cameraPosition.target}"
                            )
                            onEvent(CameraMoved(cameraPosition.target))
                        }
                    }
                })
            }

            // Кнопка "Вернуться к позиции пользователя"
            RoundedButton(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(bottom = Dimens.MarginHuge64, end = Dimens.MarginSmall8),
                onClick = { moveCamera(userLocation, mapView.value) },
                painter = painterResource(R.drawable.ic_my_location),
                contentDescriptionResId = R.string.to_my_location
            )

            // Центральный маркер
            ChosenLocationPin(
                modifier = Modifier
                    .align(Alignment.Center)
            )

            // Кнопка "Доставить сюда"
            BigButtonWithText(
                modifier = Modifier
                    .padding(horizontal = Dimens.MarginBig32, vertical = Dimens.MarginSmall8)
                    .align(Alignment.BottomCenter),
                shouldBeActive = state.locationChosen,
                textResID = R.string.deliver_to_this_location,
                onClick = { onEvent(LocationEvent.GoToAddressDetails) }
            )

        }
    }
    LaunchedEffect(userLocation) {
        if (userLocation != null) {
            moveCamera(userLocation, mapView.value)
        }
    }

    LaunchedEffect(Unit) {
        effectFlow.collectLatest { effect ->
            when (effect) {
                is LocationEffect.GoBack -> navController.popBackStack()
                else -> {}
            }
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
