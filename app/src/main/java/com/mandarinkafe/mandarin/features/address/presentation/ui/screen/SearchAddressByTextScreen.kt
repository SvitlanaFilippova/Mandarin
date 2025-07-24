package com.mandarinkafe.mandarin.features.address.presentation.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.mandarinkafe.mandarin.R
import com.mandarinkafe.mandarin.core.presentation.theme.Colors
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.features.address.presentation.ui.components.AddressSearchResults
import com.mandarinkafe.mandarin.features.address.presentation.viewmodel.AddressViewModel
import com.mandarinkafe.mandarin.util.presentation.ui.components.SearchBarInputField

@Composable
fun SearchAddressByTextScreen(
    viewModel: AddressViewModel = hiltViewModel(),
    navController: NavHostController
) {
    val state by viewModel.state.collectAsState()
    viewModel.effect
    state.userLocation
    val addressValue = state.address ?: ""

    Column(Modifier.fillMaxSize()) {
        SearchBarInputField(
            modifier = Modifier
                .padding(Dimens.MarginSmall8),
            query = addressValue,
            enabled = true,
            placeholderRes = R.string.address_for_delivery,
            autoFocus = true,
            onQueryChange = { },
            onClear = { },
            onDismiss = { },
            leadingIcon = {
                Icon(
                    Icons.Default.Search,
                    contentDescription = stringResource(R.string.address_for_delivery),
                    tint = Colors.White
                )
            },
        )

        AddressSearchResults()
    }
}