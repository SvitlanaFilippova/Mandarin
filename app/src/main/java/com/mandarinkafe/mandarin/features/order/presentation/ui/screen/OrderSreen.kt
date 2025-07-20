package com.mandarinkafe.mandarin.features.order.presentation.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.mandarinkafe.mandarin.R
import com.mandarinkafe.mandarin.core.presentation.theme.Colors
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.features.cart.presentation.viewmodel.CartViewModel
import com.mandarinkafe.mandarin.features.order.presentation.ui.components.DeliveryInfo
import com.mandarinkafe.mandarin.features.order.presentation.ui.components.OrderSummaryData
import com.mandarinkafe.mandarin.features.order.presentation.ui.components.PaymentChooser
import com.mandarinkafe.mandarin.features.order.presentation.ui.components.PersonalInfo
import com.mandarinkafe.mandarin.features.order.presentation.ui.components.SubmitOrderButton
import com.mandarinkafe.mandarin.features.order.presentation.ui.components.UtensilPreferences
import com.mandarinkafe.mandarin.features.order.presentation.viewmodel.OrderContract.OrderEvent
import com.mandarinkafe.mandarin.features.order.presentation.viewmodel.OrderViewModel
import com.mandarinkafe.mandarin.util.presentation.ui.components.MyTextField

@Composable
fun OrderScreen(
    orderViewModel: OrderViewModel = hiltViewModel(),
    cartViewModel: CartViewModel,
) {
    val state by orderViewModel.state.collectAsState()
    val cartState by cartViewModel.state.collectAsState()
    val onEvent = orderViewModel::onEvent

    val cartSum = cartState.totalCartPrice
    val discountSum = state.discountSum
    val deliveryCost = state.deliveryCost
    val totalOrderSum =
        remember(cartSum, discountSum, deliveryCost) { cartSum - discountSum + deliveryCost }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(Colors.AppBlack)
            .padding(Dimens.MarginStandard16),
        verticalArrangement = Arrangement.spacedBy(Dimens.MarginStandard16)
    ) {
        PersonalInfo(
            nameQuery = state.name,
            phoneQuery = state.phone,
            isError = state.isError,
            phoneIsValid = state.phoneIsValid,
            onNameEntered = { onEvent(OrderEvent.SetName(it)) },
            onPhoneChanged = { onEvent(OrderEvent.SetPhone(it)) },
        )

        DeliveryInfo(
            chosen = state.deliveryType,
            onDeliverySelected = { onEvent(OrderEvent.SetDeliveryType(it)) },
            addressQuery = state.address,
            isError = state.isError,
            onAddressEntered = { onEvent(OrderEvent.SetAddress(it)) },
            apartmentNumberQuery = state.apartmentNumber,
            onApartmentNumberEntered = { onEvent(OrderEvent.SetApartmentNumber(it)) },
            apartmentEntranceQuery = state.apartmentEntrance,
            onEntranceEntered = { onEvent(OrderEvent.SetEntrance(it)) },
            apartmentFloorQuery = state.apartmentFloor,
            onFloorEntered = { onEvent(OrderEvent.SetFloor(it)) },
            apartmentIntercomQuery = state.apartmentIntercom,
            onIntercomEntered = { onEvent(OrderEvent.SetIntercom(it)) },
            onGetLocationIconClick = { onEvent(OrderEvent.GetLocation) },
            addressComment = state.addressComment,
            onAddressCommentsEntered = { onEvent(OrderEvent.SetAddressComment(it)) },
        )

        PaymentChooser(
            chosen = state.paymentType,
            changeAmount = state.changeFrom,
            isError = state.isError,
            onPaymentTypeSelected = { onEvent(OrderEvent.SetPaymentType(it)) },
            onChangeEntered = { onEvent(OrderEvent.SetChangeFrom(it)) },
            noChange = state.noChange,
            onNoChangeToggled = { onEvent(OrderEvent.NoChangeToggled(it)) },
        )

        UtensilPreferences(
            noUtensils = state.noNeedUtensils,
            chosenUtensils = state.chosenUtensils,
            onChangeNoUtensils = { onEvent(OrderEvent.SetNoNeedUtensils(it)) },
            onChooseUtensil = { utensil, isChecked ->
                onEvent(
                    OrderEvent.SetChosenUtensils(
                        utensil,
                        isChecked
                    )
                )
            }
        )

        MyTextField(
            value = state.comment,
            labelRes = R.string.your_comment,
            onValueChange = { onEvent(OrderEvent.SetComment(it)) }
        )


        OrderSummaryData(
            cartSum = cartSum,
            discountSum = discountSum,
            discountPercent = state.discountPercent,
            deliveryCost = deliveryCost,
        )




        SubmitOrderButton(
            shouldBeActive = state.canBeSubmitted,
            modifier = Modifier.padding(bottom = Dimens.MarginStandard16),
            onMissingRequiredInfo = { onEvent(OrderEvent.OnMissingRequiredInfo) },
            onSubmitOrder = { onEvent(OrderEvent.SubmitOrder) },
            totalPrice = totalOrderSum,
        )
    }
}