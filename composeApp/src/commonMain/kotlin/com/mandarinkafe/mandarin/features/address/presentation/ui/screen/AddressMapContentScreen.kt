package com.mandarinkafe.mandarin.features.address.presentation.ui.screen

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.mandarinkafe.mandarin.core.domain.models.Address


@Composable
expect fun AddressMapContentScreen(
    navController: NavController,
    initAddress: Address?,
    returnToRoute: String
)