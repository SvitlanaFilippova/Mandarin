package com.mandarinkafe.mandarin.features.order.presentation.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.mandarinkafe.mandarin.R
import com.mandarinkafe.mandarin.core.domain.models.GeoPoint
import com.mandarinkafe.mandarin.core.presentation.theme.Colors
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.core.presentation.theme.Typography
import com.mandarinkafe.mandarin.features.address.savedadresses.presentation.ui.components.SavedAddressCard
import com.mandarinkafe.mandarin.features.cart.presentation.viewmodel.CartViewModel
import com.mandarinkafe.mandarin.features.order.domain.models.DeliveryType
import com.mandarinkafe.mandarin.features.order.presentation.models.UiAddress
import com.mandarinkafe.mandarin.features.order.presentation.ui.components.DeliveryTypeChooser
import com.mandarinkafe.mandarin.features.order.presentation.ui.components.OrderSummaryData
import com.mandarinkafe.mandarin.features.order.presentation.ui.components.PaymentChooser
import com.mandarinkafe.mandarin.features.order.presentation.ui.components.PersonalInfo
import com.mandarinkafe.mandarin.features.order.presentation.ui.components.SubmitOrderButton
import com.mandarinkafe.mandarin.features.order.presentation.ui.components.UtensilPreferences
import com.mandarinkafe.mandarin.features.order.presentation.viewmodel.OrderContract.OrderEffect
import com.mandarinkafe.mandarin.features.order.presentation.viewmodel.OrderContract.OrderEvent
import com.mandarinkafe.mandarin.features.order.presentation.viewmodel.OrderViewModel
import com.mandarinkafe.mandarin.navigation.navigateToAddress
import com.mandarinkafe.mandarin.navigation.navigateToAddressDetails
import com.mandarinkafe.mandarin.util.presentation.ui.components.MyTextField
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@Composable
fun OrderScreen(
    orderViewModel: OrderViewModel = hiltViewModel(),
    cartViewModel: CartViewModel,
    navController: NavHostController
) {
    val list = remember {
        listOf(
            UiAddress(
                point = GeoPoint(55.996873, 38.374358),
                streetAndBuilding = "ул. Солнечная 4, Черноголовка",
                isPrivateHouse = false,
                apartmentNumber = "82",
                entrance = "2",
                floor = "10",
                intercom = "#4444",
                comment = ""
            ),
            UiAddress(
                point = GeoPoint(55.996837, 38.377260),
                streetAndBuilding = "Берёзовая 2а, Черноголовка", isPrivateHouse = true
            ),
            UiAddress(
                point = GeoPoint(55.859610, 38.449978),
                streetAndBuilding = "Рогожская улица, 26, Ногинск",
                isPrivateHouse = false,
                apartmentNumber = "452",
                entrance = "4",
                floor = "10",
                intercom = "#44564444444",
                comment = ""
            )
        )
    }

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
    val chosenDeliveryType = state.deliveryType
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Colors.AppBlack)
            .padding(Dimens.MarginStandard16),
    ) {
        item {
            PersonalInfo(
                nameQuery = state.name,
                phoneQuery = state.phone,
                isError = state.isError,
                phoneIsValid = state.phoneIsValid,
                onNameEntered = { onEvent(OrderEvent.SetName(it)) },
                onPhoneChanged = { onEvent(OrderEvent.SetPhone(it)) },
            )
        }

        item { Spacer(Modifier.height(Dimens.MarginStandard16)) }

        item {
            DeliveryTypeChooser(
                chosen = chosenDeliveryType,
                pickupOnly = cartState.pickupOnly,
                isError = state.isError,
                onDeliverySelected = { onEvent(OrderEvent.SetDeliveryType(it)) },
            )
        }

        if (chosenDeliveryType == DeliveryType.DELIVERY) {

            items(items = list, key = { it.hashCode() }) { item ->
                SavedAddressCard(
                    address = item,
                    onAddressChosen = { onEvent(OrderEvent.SetAddress(item)) },
                    onEditAddress = { onEvent(OrderEvent.EditAddress(item)) },
                    selected = item == state.address
                )
            }

            item {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(Dimens.MarginSmall8)
                        .clickable(
                            onClick = { onEvent(OrderEvent.AddNewAddress) },
                            role = Role.Button
                        ),
                    text = stringResource(R.string.add_address),
                    textAlign = TextAlign.Center,
                    style = Typography.RegularTextStyle,
                    color = Colors.Orange
                )
            }
        }

        item { Spacer(Modifier.height(Dimens.MarginStandard16)) }
        item {
            PaymentChooser(
                chosen = state.paymentType,
                changeAmount = state.changeFrom,
                isError = state.isError,
                onPaymentTypeSelected = { onEvent(OrderEvent.SetPaymentType(it)) },
                onChangeEntered = { onEvent(OrderEvent.SetChangeFrom(it)) },
                noChange = state.noChange,
                onNoChangeToggled = { onEvent(OrderEvent.NoChangeToggled(it)) },
            )
        }

        item {
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
        }
        item { Spacer(Modifier.height(Dimens.MarginStandard16)) }

        item {
            MyTextField(
                value = state.comment,
                labelRes = R.string.your_comment,
                onValueChange = { onEvent(OrderEvent.SetComment(it)) }
            )
        }

        item {
            OrderSummaryData(
                cartSum = cartSum,
                discountSum = discountSum,
                discountPercent = state.discountPercent,
                deliveryCost = deliveryCost,
                addressInNotInDeliveryArea = state.addressInNotInDeliveryArea,
                freeDeliveryThreshold = state.deliveryZone?.freeDeliveryThreshold,
                deliveryType = state.deliveryType
            )
        }

        item {
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

    LaunchedEffect(Unit) {
        effectFlow.collectLatest { effect ->
            when (effect) {
                is OrderEffect.AddNewAddress -> {
                    navController.navigateToAddress()
                }

                is OrderEffect.EditAddress -> navController.navigateToAddressDetails(
                    address = effect.address,
                    isEditMode = true
                )

                is OrderEffect.SubmitOrder -> {}
            }
        }
    }
}