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
    isError: Boolean,
    addressQuery: String,
    onAddressEntered: (String) -> Unit,
    addressComment: String,
    onAddressCommentsEntered: (String) -> Unit,
    apartmentNumberQuery: String,
    onApartmentNumberEntered: (String) -> Unit,
    apartmentEntranceQuery: String,
    onEntranceEntered: (String) -> Unit,
    apartmentFloorQuery: String,
    onFloorEntered: (String) -> Unit,
    apartmentIntercomQuery: String,
    onIntercomEntered: (String) -> Unit,
    onGetLocationIconClick: () -> Unit,
) {
    val requestApartmentDetails by remember(chosen) { mutableStateOf(chosen == DeliveryType.APARTMENT) }
    val requestAddressDetails by remember(chosen) { mutableStateOf(chosen == DeliveryType.PRIVATE_HOUSE) }

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
        ApartmentDetails(
            isError = isError,
            apartmentNumberQuery = apartmentNumberQuery,
            onApartmentNumberEntered = onApartmentNumberEntered,
            apartmentEntranceQuery = apartmentEntranceQuery,
            onEntranceEntered = onEntranceEntered,
            apartmentFloorQuery = apartmentFloorQuery,
            onFloorEntered = onFloorEntered,
            apartmentIntercomQuery = apartmentIntercomQuery,
            onIntercomQEntered = onIntercomEntered
        )
    }

    // Опциональное поле для примечания к адресу, если выбрана доставка в частный дом
    if (requestAddressDetails) {
        MyTextField(
            value = addressComment,
            labelRes = R.string.address_comment,
            onValueChange = { onAddressCommentsEntered(it) }
        )
    }

}