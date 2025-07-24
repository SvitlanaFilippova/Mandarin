package com.mandarinkafe.mandarin.features.address.textsearch.presentation.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.mandarinkafe.mandarin.R
import com.mandarinkafe.mandarin.core.presentation.theme.Colors
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.features.address.textsearch.presentation.ui.components.AddressSearchResults
import com.mandarinkafe.mandarin.features.address.textsearch.presentation.viewmodel.AddressTextContract.AddressTextEvent
import com.mandarinkafe.mandarin.features.address.textsearch.presentation.viewmodel.AddressTextViewModel
import com.mandarinkafe.mandarin.util.presentation.ui.components.SearchBarInputField
import com.yandex.mapkit.geometry.Geometry

@Composable
fun AddressTextScreen(
    query: String?,
    geometry: Geometry?,
    viewModel: AddressTextViewModel = hiltViewModel(),
    navController: NavHostController
) {
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

    Column(Modifier.fillMaxSize()) {

        SearchBarInputField(
            modifier = Modifier
                .padding(Dimens.MarginSmall8),
            query = state.query,
            placeholderRes = R.string.address_for_delivery,
            autoFocus = true,
            onQueryChange = { onEvent(AddressTextEvent.SetQuery(it)) },
            onClear = { onEvent(AddressTextEvent.SetQuery("")) },
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
}