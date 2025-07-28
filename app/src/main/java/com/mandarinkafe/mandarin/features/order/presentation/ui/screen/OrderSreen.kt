package com.mandarinkafe.mandarin.features.order.presentation.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.mandarinkafe.mandarin.R
import com.mandarinkafe.mandarin.core.presentation.theme.Colors
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.features.cart.presentation.viewmodel.CartViewModel
import com.mandarinkafe.mandarin.features.order.domain.models.DeliveryType
import com.mandarinkafe.mandarin.features.order.presentation.ui.components.DeliveryTypeChooser
import com.mandarinkafe.mandarin.features.order.presentation.ui.components.OrderSummaryData
import com.mandarinkafe.mandarin.features.order.presentation.ui.components.PaymentChooser
import com.mandarinkafe.mandarin.features.order.presentation.ui.components.PersonalInfo
import com.mandarinkafe.mandarin.features.order.presentation.ui.components.SavedAddressesSection
import com.mandarinkafe.mandarin.features.order.presentation.ui.components.SubmitOrderButton
import com.mandarinkafe.mandarin.features.order.presentation.ui.components.UtensilPreferences
import com.mandarinkafe.mandarin.features.order.presentation.viewmodel.OrderContract.OrderEffect
import com.mandarinkafe.mandarin.features.order.presentation.viewmodel.OrderContract.OrderEvent
import com.mandarinkafe.mandarin.features.order.presentation.viewmodel.OrderViewModel
import com.mandarinkafe.mandarin.navigation.extensions.navigateToAddress
import com.mandarinkafe.mandarin.navigation.extensions.navigateToAddressDetails
import com.mandarinkafe.mandarin.util.Constants.DEFAULT_SAVED_ADDRESSES_NUMBER
import com.mandarinkafe.mandarin.util.Constants.SHOULD_REFRESH_ADDRESSES_KEY
import com.mandarinkafe.mandarin.util.Constants.SHOULD_SELECT_LAST_ADDED_KEY
import com.mandarinkafe.mandarin.util.presentation.ui.components.ConfirmationDialog
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
    var showConfirmDeleteDialog by remember { mutableStateOf(false) }
    var addressToDelete by remember { mutableStateOf<String?>(null) }

    val scrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()
    val chosenDeliveryType = state.deliveryType

    var showAllAddresses by remember { mutableStateOf(false) }
    val addresses = if (showAllAddresses) {
        state.savedAddresses
    } else {
        state.savedAddresses.take(
            DEFAULT_SAVED_ADDRESSES_NUMBER
        )
    }

    // для корректного возврата с экрана добавления адреса
    val currentBackStackEntry = remember(navController) {
        navController.currentBackStackEntry
    }
    val savedStateHandle = currentBackStackEntry?.savedStateHandle
    LaunchedEffect(savedStateHandle) {
        savedStateHandle?.getLiveData<Boolean>(SHOULD_REFRESH_ADDRESSES_KEY)
            ?.observeForever { shouldRefresh ->
                if (shouldRefresh == true) {
                    orderViewModel.onEvent(OrderEvent.RefreshAddresses)
                    savedStateHandle[SHOULD_REFRESH_ADDRESSES_KEY] = false
                }
            }

        savedStateHandle?.getLiveData<Boolean>(SHOULD_SELECT_LAST_ADDED_KEY)
            ?.observeForever { shouldSelect ->
                if (shouldSelect == true) {
                    orderViewModel.onEvent(OrderEvent.SelectLastAddedAddress)
                    savedStateHandle[SHOULD_SELECT_LAST_ADDED_KEY] = false
                }
            }
    }

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
        item { Spacer(Modifier.height(Dimens.MarginSmall8)) }

        if (chosenDeliveryType == DeliveryType.DELIVERY) {
            item {
                SavedAddressesSection(
                    addresses = addresses,
                    selectedAddress = state.chosenAddress,
                    onEvent = onEvent,
                    onDeleteRequest = {
                        addressToDelete = it
                        showConfirmDeleteDialog = true
                    },
                    showAllAddresses = showAllAddresses,
                    onToggleShowAll = { showAllAddresses = !showAllAddresses })
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

    // Диалог для подтверждения желания удалить адрес
    if (showConfirmDeleteDialog && addressToDelete != null) {
        ConfirmationDialog(
            titleRes = R.string.delete_address_question,
            textRes = R.string.delete_address_text,
            onConfirm = {
                addressToDelete?.let { id ->
                    onEvent(OrderEvent.RemoveAddress(id))
                }
                showConfirmDeleteDialog = false
                addressToDelete = null
            },
            onDismiss = {
                showConfirmDeleteDialog = false
                addressToDelete = null
            }
        )
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