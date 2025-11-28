package com.mandarinkafe.mandarin.features.delivery.presentation.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.mandarinkafe.mandarin.MR
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.features.delivery.presentation.ui.components.DeliveryZonesSection
import com.mandarinkafe.mandarin.features.delivery.presentation.viewmodel.DeliveryContract.DeliveryEvent
import com.mandarinkafe.mandarin.shared.presentation.viewmodel.rememberDeliveryViewModel
import com.mandarinkafe.mandarin.util.presentation.ui.components.InfoCard
import com.mandarinkafe.mandarin.util.presentation.ui.components.ScreenTitleWithBackButton
import com.mandarinkafe.mandarin.util.presentation.ui.components.map.RequestLocationPermission
import dev.icerock.moko.resources.compose.painterResource
import dev.icerock.moko.resources.compose.stringResource

@Composable
fun DeliveryScreen(
    onBackClick: () -> Unit,
) {
    val viewModel = rememberDeliveryViewModel()
    val state by viewModel.state.collectAsState()
    val initLocation = state.initPinPoint
    val onEvent = viewModel::onEvent
    var mapShouldBeVisible by remember { mutableStateOf(true) }

    // Проверяем разрешение на определение местоположения. Если его нет - запрашиваем. Если есть - определеяем.
    RequestLocationPermission(
        onGranted = { onEvent(DeliveryEvent.RequestAddress) }
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        // Заголовок экрана и стрелка "Назад"
        ScreenTitleWithBackButton(
            name = stringResource(MR.strings.more_delivery_info),
            onBackClick = {
                onBackClick()
                mapShouldBeVisible = false
            },
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = Dimens.MarginSmall8)
        ) {
            Spacer(modifier = Modifier.height(Dimens.MarginSmall8))

            // Все зоны доставки
            with(state) {
                DeliveryZonesSection(
                    deliveryAreas = deliveryAreas,
                    initLocation = initLocation,
                    userLocation = userLocation,
                    displayAddress = displayAddress,
                    deliveryArea = deliveryArea,
                    isLoading = fetchAddressInProgress,
                    isError = error != null,
                    locationChosen = locationChosen,
                    mapShouldBeVisible = mapShouldBeVisible,
                    onCameraMoved = { onEvent(DeliveryEvent.CameraMoved(it)) }
                )
            }

            Spacer(modifier = Modifier.height(Dimens.MarginSmall8))

            // Время доставки
            InfoCard(
                iconPainter = painterResource(MR.images.ic_clock),
                title = stringResource(MR.strings.delivery_duration_title),
                lines = listOf(
                    stringResource(MR.strings.delivery_duration_value) to null
                )
            )


            Spacer(modifier = Modifier.height(Dimens.MarginBig32))
        }
    }
}
