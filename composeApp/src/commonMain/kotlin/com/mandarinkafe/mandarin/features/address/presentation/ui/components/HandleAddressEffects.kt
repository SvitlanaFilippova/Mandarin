package com.mandarinkafe.mandarin.features.address.presentation.ui.components

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavController
import com.mandarinkafe.mandarin.features.address.presentation.viewmodel.AddressContract.AddressEffect
import com.mandarinkafe.mandarin.navigation.extensions.navigateToAddressDetails
import kotlinx.coroutines.flow.Flow

@Composable
fun HandleAddressEffects(
    effectFlow: Flow<AddressEffect>,
    navController: NavController,
    returnToRoute: String,
    snackbarHostState: SnackbarHostState,
) {
    LaunchedEffect(effectFlow) {
        effectFlow.collect { effect ->
            when (effect) {
                is AddressEffect.GoBack -> navController.popBackStack()
                is AddressEffect.GoToAddressDetailsEffect -> {
                    navController.navigateToAddressDetails(
                        address = effect.address,
                        isEditMode = false,
                        returnToRoute = returnToRoute,
                    )
                }
                is AddressEffect.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(
                        message = effect.message,
                        duration = SnackbarDuration.Short,
                        withDismissAction = true,
                    )
                }
            }
        }
    }
}