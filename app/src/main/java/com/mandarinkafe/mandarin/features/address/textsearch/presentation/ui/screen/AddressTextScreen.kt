package com.mandarinkafe.mandarin.features.address.textsearch.presentation.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavHostController
import com.mandarinkafe.mandarin.R
import com.mandarinkafe.mandarin.core.presentation.theme.Colors
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.features.address.textsearch.presentation.ui.components.AddressSearchResults
import com.mandarinkafe.mandarin.features.address.textsearch.presentation.viewmodel.AddressTextContract.AddressTextEvent
import com.mandarinkafe.mandarin.features.address.textsearch.presentation.viewmodel.AddressTextViewModel
import com.mandarinkafe.mandarin.features.order.presentation.ui.components.GetLocationIcon
import com.mandarinkafe.mandarin.util.Constants.MIN_LINES_FOR_ADDRESS_INPUT
import com.mandarinkafe.mandarin.util.presentation.ui.components.MyTextField
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Geometry

@Composable
fun AddressTextScreen(
    query: String?,
    geometry: Geometry?,
    viewModel: AddressTextViewModel = hiltViewModel(),
    navController: NavHostController
) {
    "DEBUG MapKitFactory"

    // передача в viewModel стартовой информации
    if (query == null || geometry == null) return
    LaunchedEffect(Unit) {
        viewModel.onEvent(
            AddressTextEvent.SetInitData(
                geometry = geometry,
                query = query
            )
        )
    }

    val state by viewModel.state.collectAsState()
    val onEvent = viewModel::onEvent

    val lifecycleOwner = LocalLifecycleOwner.current

    Column(
        Modifier
            .fillMaxSize()
            .padding(Dimens.MarginSmall8)
    ) {

        // Строка с адресом
        MyTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = Dimens.MarginSmall8),
            value = state.query,
            minLines = MIN_LINES_FOR_ADDRESS_INPUT,
            labelRes = R.string.street_and_building,
            onValueChange = { onEvent(AddressTextEvent.SetQuery(it)) },
            leadingIcon = { GetLocationIcon(enabled = false) }
        )


        if (state.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.padding(Dimens.MarginSmall8),
                color = Colors.LightGrey
            )
        }
        if (state.data.isNotEmpty()) {
            Text("что-то нашлось, ОМФГ!")
            AddressSearchResults()
        }

    }
    // Lifecycle observer для вызова onStart/onStop у MapKitFactory
    DisposableEffect(lifecycleOwner) {
        val lifecycle = lifecycleOwner.lifecycle

        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_START -> {
                    MapKitFactory.getInstance().onStart()
                }

                Lifecycle.Event.ON_STOP -> {
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