package com.mandarinkafe.mandarin.features.order.presentation.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import com.mandarinkafe.mandarin.R
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.util.presentation.ui.components.MyTextField

@Composable
fun ApartmentDetails(
    isError: Boolean,
    apartmentNumberQuery: String,
    onApartmentNumberEntered: (String) -> Unit,
    apartmentEntranceQuery: String,
    onEntranceEntered: (String) -> Unit,
    apartmentFloorQuery: String,
    onFloorEntered: (String) -> Unit,
    apartmentIntercomQuery: String,
    onIntercomEntered: (String) -> Unit,
) {
    Row {
        // № Квартиры
        MyTextField(
            modifier = Modifier.weight(1f),
            isError = isError && apartmentNumberQuery.isEmpty(),
            value = apartmentNumberQuery,
            labelRes = R.string.address_apartment_number,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            onValueChange = { onApartmentNumberEntered(it) }
        )
        Spacer(modifier = Modifier.size(Dimens.MarginSmall8))

        // Подъезд
        MyTextField(
            modifier = Modifier.weight(1f),
            isError = isError && apartmentEntranceQuery.isEmpty(),
            value = apartmentEntranceQuery,
            labelRes = R.string.address_apartment_entrance,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            onValueChange = { onEntranceEntered(it) }
        )
    }

    Row {
        // Этаж
        MyTextField(
            modifier = Modifier.weight(1f),
            isError = isError && apartmentFloorQuery.isEmpty(),
            value = apartmentFloorQuery,
            labelRes = R.string.address_apartment_floor,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            onValueChange = { onFloorEntered(it) }
        )
        Spacer(modifier = Modifier.size(Dimens.MarginSmall8))

        // Домофон
        MyTextField(
            modifier = Modifier.weight(1f),
            value = apartmentIntercomQuery,
            labelRes = R.string.address_apartment_intercom,
            onValueChange = { onIntercomEntered(it) }
        )
    }
}