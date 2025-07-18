package com.mandarinkafe.mandarin.features.order.presentation.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.mandarinkafe.mandarin.R
import com.mandarinkafe.mandarin.features.order.domain.models.DeliveryType
import com.mandarinkafe.mandarin.util.presentation.ui.components.MyTextField

@Composable
fun DeliveryInfo(
    chosen: DeliveryType,
    onDeliverySelected: (DeliveryType) -> Unit,
    addressQuery: String,
    onAddressEntered: (String) -> Unit,
    apartmentDetailsQuery: String,
    onApartmentDetailsEntered: (String) -> Unit,

    ) {
    val requestApartmentDetails by remember(chosen) { mutableStateOf(chosen == DeliveryType.APARTMENT) }
    DeliveryTypeChooser(chosen = DeliveryType.PRIVATE_HOUSE, onDeliverySelected)

    MyTextField(
        value = addressQuery,
        labelRes = R.string.your_address,
        onValueChange = { onAddressEntered(it) }
    )

    if (requestApartmentDetails) {
        MyTextField(
            value = apartmentDetailsQuery,
            labelRes = R.string.your_apartment_details,
            onValueChange = { onApartmentDetailsEntered(it) }
        )
    }

}