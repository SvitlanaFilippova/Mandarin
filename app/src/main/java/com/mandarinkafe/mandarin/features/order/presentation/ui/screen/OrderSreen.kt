package com.mandarinkafe.mandarin.features.order.presentation.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import com.mandarinkafe.mandarin.R
import com.mandarinkafe.mandarin.core.presentation.theme.Colors
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.core.presentation.theme.Typography
import com.mandarinkafe.mandarin.features.order.presentation.ui.components.DeliveryTypeChooser
import com.mandarinkafe.mandarin.features.order.presentation.ui.components.OrderSummaryData
import com.mandarinkafe.mandarin.features.order.presentation.ui.components.PaymentChooser
import com.mandarinkafe.mandarin.features.order.presentation.ui.components.PersonalInfo
import com.mandarinkafe.mandarin.features.order.presentation.ui.components.SavedAddressesSection
import com.mandarinkafe.mandarin.features.order.presentation.ui.components.SelfPickupInfo
import com.mandarinkafe.mandarin.features.order.presentation.ui.components.SubmitOrderButton
import com.mandarinkafe.mandarin.features.order.presentation.ui.components.UtensilPreferences
import com.mandarinkafe.mandarin.features.order.presentation.viewmodel.OrderContract.OrderEffect
import com.mandarinkafe.mandarin.features.order.presentation.viewmodel.OrderContract.OrderEvent
import com.mandarinkafe.mandarin.features.order.presentation.viewmodel.OrderContract.OrderEvent.StopObservingStatus
import com.mandarinkafe.mandarin.features.order.presentation.viewmodel.OrderViewModel
import com.mandarinkafe.mandarin.navigation.extensions.navigateToAddress
import com.mandarinkafe.mandarin.navigation.extensions.navigateToAddressDetails
import com.mandarinkafe.mandarin.navigation.extensions.navigateToOrderConfirmation
import com.mandarinkafe.mandarin.util.Constants.SHOULD_REFRESH_ADDRESSES_KEY
import com.mandarinkafe.mandarin.util.Constants.SHOULD_SELECT_ADDRESS_ID
import com.mandarinkafe.mandarin.util.presentation.LocalSnackbarHostState
import com.mandarinkafe.mandarin.util.presentation.ui.components.ConfirmationDialog
import com.mandarinkafe.mandarin.util.presentation.ui.components.MyTextField
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@Composable
fun OrderScreen(
    orderViewModel: OrderViewModel,
    navController: NavHostController
) {
    val state by orderViewModel.state.collectAsState()
    val effectFlow = orderViewModel.effect
    val onEvent = orderViewModel::onEvent
    var showConfirmDeleteDialog by remember { mutableStateOf(false) }
    var addressToDelete by remember { mutableStateOf<String?>(null) }
    val scrollState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    var showAllAddresses by remember { mutableStateOf(false) }

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

        savedStateHandle?.getLiveData<String>(SHOULD_SELECT_ADDRESS_ID)
            ?.observeForever { id ->
                if (id != null) {
                    orderViewModel.onEvent(OrderEvent.SelectAddressById(id))
                    savedStateHandle[SHOULD_SELECT_ADDRESS_ID] = null
                }
            }
    }

    LaunchedEffect(Unit) {
        orderViewModel.onEvent(OrderEvent.GetPaymentTypes)
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Colors.AppBlack)
            .padding(Dimens.MarginSmall8),
        state = scrollState,
    ) {
        item {
            with(state.userInfo) {
                PersonalInfo(
                    nameQuery = name,
                    phoneQuery = phone,
                    isError = state.isError,
                    phoneIsValid = phoneIsValid,
                    onNameEntered = { onEvent(OrderEvent.SetName(it)) },
                    onPhoneChanged = { onEvent(OrderEvent.SetPhone(it)) },
                )
            }
        }

        item { Spacer(Modifier.height(Dimens.MarginStandard16)) }

        item {
            DeliveryTypeChooser(
                chosen = state.deliveryInfo.deliveryType,
                pickupOnly = state.pickupOnly,
                isError = state.isError,
                onDeliverySelected = { onEvent(OrderEvent.SetDeliveryType(it)) },
            )
        }
        item { Spacer(Modifier.height(Dimens.MarginSmall8)) }

        item {
            SelfPickupInfo(
                visible = state.deliveryInfo.isPickup,
                pickupPoint = state.pickupPoint
            )
        }

        // Сообщение об ошибке, если выбрана доставка, но не выбран адрес
        if (state.isError && state.deliveryInfo.isDelivery && !state.deliveryInfo.addressIsValid) {
            item {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = Dimens.MarginSuperSmall4),
                    text = stringResource(R.string.choose_address),
                    style = Typography.RegularTextStyle.copy(color = Colors.ErrorRed),
                )
            }
        }

        item {
            with(state.deliveryInfo) {
                SavedAddressesSection(
                    visible = isDelivery,
                    allSavedAddresses = savedAddresses,
                    selectedAddress = chosenAddress,
                    onEvent = onEvent,
                    onDeleteRequest = {
                        addressToDelete = it
                        showConfirmDeleteDialog = true
                    },
                    showAllAddresses = showAllAddresses,
                    onToggleShowAll = { showAllAddresses = !showAllAddresses }
                )
            }
        }


        item { Spacer(Modifier.height(Dimens.MarginStandard16)) }

        item {
            with(state.paymentInfo) {
                PaymentChooser(
                    paymentTypes = availablePaymentTypes,
                    chosen = chosenPaymentType,
                    changeAmount = changeFrom,
                    isError = state.isError,
                    onPaymentTypeSelected = { onEvent(OrderEvent.SetPaymentType(it)) },
                    onChangeEntered = { onEvent(OrderEvent.SetChangeFrom(it)) },
                    noChange = noChange,
                    onNoChangeToggled = { onEvent(OrderEvent.NoChangeToggled(it)) },
                )
            }
        }
        item { Spacer(Modifier.height(Dimens.MarginStandard16)) }

        item {
            with(state.utensils) {
                UtensilPreferences(
                    noUtensils = noNeedUtensils,
                    chosenUtensils = chosenUtensils,
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
            with(state.cartSummary) {
                OrderSummaryData(
                    cartSum = totalCartSum,
                    discountSum = discountSum,
                    discountSize = discountCategory,
                    deliveryCost = state.deliveryCost,
                    containNotDiscountable = containNotDiscountable,
                    addressInNotInDeliveryArea = state.deliveryInfo.addressOutOfDeliveryZone,
                    freeDeliveryThreshold = state.deliveryInfo.deliveryZone?.freeDeliveryThreshold,
                    isPickup = state.deliveryInfo.isPickup
                )
            }
        }

        item {
            SubmitOrderButton(
                shouldBeActive = state.canBeSubmitted,
                isLoading = state.isLoading,
                modifier = Modifier.padding(bottom = Dimens.MarginStandard16),
                onMissingRequiredInfo = {
                    onEvent(OrderEvent.OnMissingRequiredInfo)
                    coroutineScope.launch {
                        scrollState.animateScrollToItem(0)
                    }
                },
                onSubmitOrder = { onEvent(OrderEvent.SubmitOrder) },
                totalOrderSum = state.totalOrderSum,
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
    val snackbarHostState = LocalSnackbarHostState.current

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

                is OrderEffect.ShowError -> {
                    snackbarHostState.showSnackbar("Ошибка: ${effect.message}")
                }

                is OrderEffect.ShowSuccess -> {
                    navController.navigateToOrderConfirmation(orderId = effect.orderId)
                }
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            onEvent(StopObservingStatus)
        }
    }
}