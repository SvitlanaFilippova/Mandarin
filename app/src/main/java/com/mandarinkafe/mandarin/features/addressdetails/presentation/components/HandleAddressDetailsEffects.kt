package com.mandarinkafe.mandarin.features.addressdetails.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavController
import com.mandarinkafe.mandarin.features.addressdetails.presentation.viewmodel.AddressDetailsContract.AddressDetailsEffect
import com.mandarinkafe.mandarin.navigation.navigateToAddress
import kotlinx.coroutines.flow.Flow

@Composable
fun HandleAddressDetailsEffects(
    effectFlow: Flow<AddressDetailsEffect>,
    navController: NavController,
) {
    LaunchedEffect(effectFlow) {
        effectFlow.collect { effect ->
            when (effect) {
                is AddressDetailsEffect.EditLocation -> navController.navigateToAddress(effect.address)
            }
        }
    }
}
