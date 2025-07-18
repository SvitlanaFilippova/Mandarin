package com.mandarinkafe.mandarin.features.order.presentation.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.mandarinkafe.mandarin.R
import com.mandarinkafe.mandarin.core.presentation.theme.Colors
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.features.order.domain.models.DeliveryType
import com.mandarinkafe.mandarin.features.order.domain.models.PaymentType
import com.mandarinkafe.mandarin.features.order.domain.models.Utensil
import com.mandarinkafe.mandarin.features.order.presentation.ui.components.DeliveryInfo
import com.mandarinkafe.mandarin.features.order.presentation.ui.components.PaymentChooser
import com.mandarinkafe.mandarin.features.order.presentation.ui.components.PersonalInfo
import com.mandarinkafe.mandarin.features.order.presentation.ui.components.SubmitOrderButton
import com.mandarinkafe.mandarin.features.order.presentation.ui.components.UtensilPreferences
import com.mandarinkafe.mandarin.util.presentation.ui.components.MyTextField

@Preview
@Composable
fun OrderScreen() {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(Colors.AppBlack)
            .padding(Dimens.MarginStandard16),
        verticalArrangement = Arrangement.spacedBy(Dimens.MarginStandard16)
    ) {

        PersonalInfo(
            nameQuery = "Светлана",
            phoneQuery = "9199964288",
            onNameEntered = {},
            onPhoneChanged = {}
        )

        DeliveryInfo(
            chosen = DeliveryType.APARTMENT,
            onDeliverySelected = { },
            addressQuery = "Черноголовка, Солнечная 4",
            onAddressEntered = { },
            apartmentDetailsQuery = "2 подъезд, 10 этаж",
            onApartmentDetailsEntered = { }
        )


        PaymentChooser(
            chosen = PaymentType.CASH,
            changeAmount = "",
            onPaymentTypeSelected = {},
            onChangeEntered = {}
        )

        UtensilPreferences(
            noUtensils = false,
            chosenUtensils = Utensil.entries
        )

        MyTextField(
            value = "",
            labelRes = R.string.your_comment,
            onValueChange = {}
        )

        SubmitOrderButton(
            onClick = { /* обработка нажатия */ },
        )

        Spacer(modifier = Modifier.size(Dimens.MarginStandard16))
    }
}