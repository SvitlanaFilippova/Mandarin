package com.mandarinkafe.mandarin.features.order.presentation.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import com.mandarinkafe.mandarin.MR
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.util.presentation.ui.components.textfields.MyTextField

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
    visible: Boolean,
) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn() + expandVertically(),
        exit = fadeOut() + shrinkVertically()
    ) {
        Column {
            Spacer(Modifier.height(Dimens.MarginSmall8))
            Row {
                // № Квартиры
                MyTextField(
                    modifier = Modifier.weight(1f),
                    isError = isError && apartmentNumberQuery.isEmpty(),
                    value = apartmentNumberQuery,
                    labelRes = MR.strings.address_apartment_number,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    onValueChange = { onApartmentNumberEntered(it) }
                )
                Spacer(modifier = Modifier.size(Dimens.MarginSmall8))

                // Подъезд
                MyTextField(
                    modifier = Modifier.weight(1f),
                    isError = isError && apartmentEntranceQuery.isEmpty(),
                    value = apartmentEntranceQuery,
                    labelRes = MR.strings.address_apartment_entrance,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    onValueChange = { onEntranceEntered(it) }
                )
            }
            Spacer(Modifier.height(Dimens.MarginSmall8))
            Row {
                // Этаж
                MyTextField(
                    modifier = Modifier.weight(1f),
                    isError = isError && apartmentFloorQuery.isEmpty(),
                    value = apartmentFloorQuery,
                    labelRes = MR.strings.address_apartment_floor,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    onValueChange = { onFloorEntered(it) }
                )
                Spacer(modifier = Modifier.size(Dimens.MarginSmall8))

                // Домофон
                MyTextField(
                    modifier = Modifier.weight(1f),
                    value = apartmentIntercomQuery,
                    labelRes = MR.strings.address_apartment_intercom,
                    onValueChange = { onIntercomEntered(it) }
                )
            }
        }
    }
}