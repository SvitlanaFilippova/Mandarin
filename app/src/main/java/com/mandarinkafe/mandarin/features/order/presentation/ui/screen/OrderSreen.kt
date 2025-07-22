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
import androidx.compose.runtime.rememberCoroutineScope
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
import kotlinx.coroutines.launch

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
    val deliveryCost = state.deliveryCost ?: 0

    val totalOrderSum =
        remember(cartSum, discountSum, deliveryCost) { cartSum - discountSum + deliveryCost }

    val scrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
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
            addressQuery = state.address.addressMain,
            isError = state.isError,
            apartmentNumberQuery = state.address.apartmentNumber,
            apartmentEntranceQuery = state.address.apartmentEntrance,
            apartmentFloorQuery = state.address.apartmentFloor,
            apartmentIntercomQuery = state.address.apartmentIntercom,
            addressComment = state.address.addressComment,
            isPrivateHouse = state.addressIsPrivateHouse,
            onDeliverySelected = { onEvent(OrderEvent.SetDeliveryType(it)) },
            onAddressEntered = { onEvent(OrderEvent.SetAddress(it)) },
            onApartmentNumberEntered = { onEvent(OrderEvent.SetApartmentNumber(it)) },
            onEntranceEntered = { onEvent(OrderEvent.SetEntrance(it)) },
            onFloorEntered = { onEvent(OrderEvent.SetFloor(it)) },
            onIntercomEntered = { onEvent(OrderEvent.SetIntercom(it)) },
            onGetLocationIconClick = { onEvent(OrderEvent.GetLocation) },
            onAddressCommentsEntered = { onEvent(OrderEvent.SetAddressComment(it)) },
            isPrivateHouseToggled = { onEvent(OrderEvent.IsPrivateHouseToggled(it)) },
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
            addressValidated = state.addressValidated,
            freeDeliveryThreshold = state.deliveryZone?.freeDeliveryThreshold,
            addressValidationInProgress = state.addressValidationInProgress
        )



        SubmitOrderButton(
            shouldBeActive = state.canBeSubmitted,
            modifier = Modifier.padding(bottom = Dimens.MarginStandard16),
            onMissingRequiredInfo = {
                onEvent(OrderEvent.OnMissingRequiredInfo)
                coroutineScope.launch {
                    scrollState.animateScrollTo(0)
                }
            },
            onSubmitOrder = { onEvent(OrderEvent.SubmitOrder) },
            totalPrice = totalOrderSum,
        )
    }
}