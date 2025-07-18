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
    chosen: DeliveryType?,
    onDeliverySelected: (DeliveryType) -> Unit,
    addressQuery: String,
    isError: Boolean,
    onAddressEntered: (String) -> Unit,
    apartmentDetailsQuery: String,
    onApartmentDetailsEntered: (String) -> Unit,
    onGetLocationIconClick: () -> Unit,
) {
    val requestApartmentDetails by remember(chosen) { mutableStateOf(chosen == DeliveryType.APARTMENT) }

    DeliveryTypeChooser(chosen = chosen, onDeliverySelected = onDeliverySelected, isError = isError)

    // Поле для ввода адреса показываем только если выбор способа доставки уже сделан, и это НЕ самовывоз
    if (chosen != null && chosen != DeliveryType.SELF_PICKUP) {
        MyTextField(
            isError = isError && addressQuery.isEmpty(),
            value = addressQuery,
            labelRes = R.string.your_address,
            leadingIcon = {
                GetLocationIcon(
                    onClick = onGetLocationIconClick
                )
            },
            onValueChange = { onAddressEntered(it) }
        )
    }

    // Поле для ввода деталей адреса показываем только если выбран способ доставки в квартиру
    if (requestApartmentDetails) {
        MyTextField(
            isError = isError && apartmentDetailsQuery.isEmpty(),
            value = apartmentDetailsQuery,
            labelRes = R.string.your_apartment_details,
            onValueChange = { onApartmentDetailsEntered(it) }
        )
    }

}