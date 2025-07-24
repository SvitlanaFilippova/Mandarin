package com.mandarinkafe.mandarin.features.address.map.presentation.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavController
import com.mandarinkafe.mandarin.features.address.map.presentation.viewmodel.AddressContract.AddressEffect
import com.mandarinkafe.mandarin.navigation.navigateToAddressDetails
import com.mandarinkafe.mandarin.navigation.navigateToAddressText
import kotlinx.coroutines.flow.Flow

@Composable
fun HandleAddressEffects(
    effectFlow: Flow<AddressEffect>,
    navController: NavController,
) {
    LaunchedEffect(effectFlow) {
        effectFlow.collect { effect ->
            when (effect) {
                is AddressEffect.GoBack -> navController.popBackStack()
                is AddressEffect.GoToAddressDetailsEffect -> {
                    navController.navigateToAddressDetails(effect.address)
                }

                is AddressEffect.GoToTextSearchEffect -> {
                    navController.navigateToAddressText(effect.query, effect.geometry)
                }
            }
        }
    }
}