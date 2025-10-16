package com.mandarinkafe.mandarin.features.address.addressdetails.presentation.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.mandarinkafe.mandarin.core.domain.models.Address
import moe.tlaster.precompose.navigation.Navigator

@Composable
fun AddressDetailsScreen(
    initAddress: Address?,
    returnToRoute: String,
    isEditMode: Boolean,
    navigator: Navigator,
    callerEntry: Any
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text("Address Details Screen - KMP Migration Placeholder")
    }
}
