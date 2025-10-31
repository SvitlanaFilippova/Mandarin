package com.mandarinkafe.mandarin.features.savedadresses.presentation.ui.components

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavController
import com.mandarinkafe.mandarin.features.savedadresses.presentation.viewmodel.SavedAddressesContract.SavedAddressesEffect
import com.mandarinkafe.mandarin.navigation.NavConstants
import com.mandarinkafe.mandarin.navigation.extensions.navigateToAddress
import com.mandarinkafe.mandarin.navigation.extensions.navigateToAddressDetails
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest

@Composable
fun HandleSavedAddressesEffects(
    effectFlow: Flow<SavedAddressesEffect>,
    navController: NavController,
    snackbarHostState: SnackbarHostState,
) {
    LaunchedEffect(Unit) {
        effectFlow.collectLatest { effect ->
            when (effect) {
                is SavedAddressesEffect.ShowError ->
                    snackbarHostState.showSnackbar("Ошибка: ${effect.message}")

                is SavedAddressesEffect.AddNewAddress -> {
                    navController.navigateToAddress(
                        returnToRoute = NavConstants.SAVED_ADDRESSES_ROUTE
                    )
                }

                is SavedAddressesEffect.EditAddress ->
                    navController.navigateToAddressDetails(
                        address = effect.address,
                        isEditMode = true,
                        returnToRoute = NavConstants.SAVED_ADDRESSES_ROUTE
                    )
            }
        }
    }
}