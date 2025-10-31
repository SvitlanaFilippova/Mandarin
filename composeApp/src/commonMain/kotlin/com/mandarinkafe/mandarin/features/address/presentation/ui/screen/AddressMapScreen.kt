package com.mandarinkafe.mandarin.features.address.presentation.ui.screen

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
import androidx.navigation.NavController
import com.mandarinkafe.mandarin.MR
import com.mandarinkafe.mandarin.core.domain.models.Address
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.features.address.presentation.ui.components.HandleAddressEffects
import com.mandarinkafe.mandarin.features.address.presentation.ui.components.SearchByTextResults
import com.mandarinkafe.mandarin.features.address.presentation.viewmodel.AddressContract
import com.mandarinkafe.mandarin.features.map.MapCameraController
import com.mandarinkafe.mandarin.features.order.presentation.ui.components.LocationIcon
import com.mandarinkafe.mandarin.shared.presentation.viewmodel.rememberAddressViewModel
import com.mandarinkafe.mandarin.util.Constants
import com.mandarinkafe.mandarin.util.Constants.MIN_LINES_FOR_ADDRESS_INPUT
import com.mandarinkafe.mandarin.util.presentation.ui.components.ScreenTitleWithBackButton
import com.mandarinkafe.mandarin.util.presentation.ui.components.buttons.ButtonWithText
import com.mandarinkafe.mandarin.util.presentation.ui.components.map.RequestLocationPermission
import com.mandarinkafe.mandarin.util.presentation.ui.components.textfields.MyTextField
import dev.icerock.moko.resources.compose.stringResource


@Composable
fun AddressMapScreen(
    navController: NavController,
    initAddress: Address?,
    returnToRoute: String,
) {
    val viewModel = rememberAddressViewModel()
    val state by viewModel.state.collectAsState()
    val onEvent = viewModel::onEvent

    // Создаем контроллер камеры (платформо-специфичная реализация)
    val cameraController = remember { createMapCameraController() }

    var mapShouldBeVisible by remember { mutableStateOf(true) }

    // если был передан адрес для редактирования - передаём его дальше в VM
    initAddress?.let {
        LaunchedEffect(Unit) {
            viewModel.onEvent(AddressContract.AddressEvent.SetInitAddress(initAddress))
        }
    }
    var searchResultsBeVisible by remember { mutableStateOf(false) }

    val onValueChange: (String) -> Unit = {
        if (it.isNotBlank()) {
            onEvent(AddressContract.AddressEvent.ChangeSearchQuery(it))
            searchResultsBeVisible = true
        } else {
            onEvent(AddressContract.AddressEvent.ChangeSearchQuery(it))
            searchResultsBeVisible = false
        }
    }
    val initLocation = state.initPinPoint
    val userLocation = state.userLocation

    // Проверяем разрешение на определение местоположения. Если его нет - запрашиваем. Если есть - определеяем.
    RequestLocationPermission(
        onGranted = { onEvent(AddressContract.AddressEvent.RequestAddress) }
    )

    val keyboardController =
        LocalSoftwareKeyboardController.current


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(Dimens.MarginSmall8)
    ) {

        ScreenTitleWithBackButton(
            name = stringResource(MR.strings.address_screen_title),
            onBackClick = { navController.popBackStack() }
        )


        // Строка с адресом
        MyTextField(
            modifier = Modifier.fillMaxWidth(),
            minLines = MIN_LINES_FOR_ADDRESS_INPUT,
            value = state.displayAddress ?: "",
            labelRes = MR.strings.street_and_building,
            onValueChange = { onValueChange(it) },
            leadingIcon = { LocationIcon(enabled = false) }
        )

        // Родительский контейнер для отображения результатов поиска поверх карты
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            if (mapShouldBeVisible) {
                with(state) {
                    AddressMapContentScreen(
                        navController = navController,
                        initAddress = initAddress,
                        returnToRoute = returnToRoute,
                        initLocation = initLocation,
                        userLocation = userLocation,
                        deliveryAreas = allDeliveryAreas,
                        displayAddress = displayAddress,
                        deliveryArea = currentDeliveryArea,
                        isLoading = fetchAddressInProgress,
                        locationChosen = locationChosen,
                        addressValue = state.displayAddress ?: "",
                        isError = error != null,
                        onCameraMoved = { point ->
                            onEvent(
                                AddressContract.AddressEvent.CameraMoved(
                                    point
                                )
                            )
                        },
                        cameraController = cameraController
                    )
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
                        },
                        onDismiss = {
                            searchResultsBeVisible = false
                            keyboardController?.hide()
                        },
                        cameraController = cameraController
                    )
                }


                ButtonWithText(
                    modifier = Modifier
                        .padding(Dimens.MarginBig24)
                        .align(Alignment.BottomCenter),
                    shouldBeActive =  state.locationChosen,
                    text = stringResource(MR.strings.deliver_to_this_location),
                    onClick = {
                        onEvent(AddressContract.AddressEvent.GoToAddressDetails)
                        mapShouldBeVisible = false
                    }
                )

            }
        }
    }

    HandleAddressEffects(
        effectFlow = viewModel.effect,
        navController = navController,
        returnToRoute = returnToRoute
    )

}

/**
 * Создает платформо-специфичную реализацию MapCameraController
 */
expect fun createMapCameraController(): MapCameraController

