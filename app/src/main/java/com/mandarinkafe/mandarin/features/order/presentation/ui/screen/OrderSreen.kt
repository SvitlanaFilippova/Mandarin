package com.mandarinkafe.mandarin.features.order.presentation.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.mandarinkafe.mandarin.R
import com.mandarinkafe.mandarin.core.presentation.theme.Colors
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.features.cart.presentation.viewmodel.CartViewModel
import com.mandarinkafe.mandarin.features.order.presentation.ui.components.OrderSummaryData
import com.mandarinkafe.mandarin.features.order.presentation.ui.components.PaymentChooser
import com.mandarinkafe.mandarin.features.order.presentation.ui.components.PersonalInfo
import com.mandarinkafe.mandarin.features.order.presentation.ui.components.SubmitOrderButton
import com.mandarinkafe.mandarin.features.order.presentation.ui.components.UtensilPreferences
import com.mandarinkafe.mandarin.features.order.presentation.viewmodel.OrderContract.OrderEffect
import com.mandarinkafe.mandarin.features.order.presentation.viewmodel.OrderContract.OrderEvent
import com.mandarinkafe.mandarin.features.order.presentation.viewmodel.OrderViewModel
import com.mandarinkafe.mandarin.navigation.navigateToLocation
import com.mandarinkafe.mandarin.util.presentation.ui.components.MyTextField
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@Composable
fun OrderScreen(
    orderViewModel: OrderViewModel = hiltViewModel(),
    cartViewModel: CartViewModel,
    navController: NavHostController
) {
    val state by orderViewModel.state.collectAsState()
    val effectFlow = orderViewModel.effect
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



        Button(
            onClick = { onEvent(OrderEvent.CreateNewAddress) },
            content = { Text("+ добавить адрес") })

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

    LaunchedEffect(Unit) {
        effectFlow.collectLatest { effect ->
            when (effect) {
                is OrderEffect.GoToLocationScreen -> {
                    navController.navigateToLocation(effect.address)
                }

                is OrderEffect.SubmitOrder -> {}

                else -> {}
            }
        }
    }
}