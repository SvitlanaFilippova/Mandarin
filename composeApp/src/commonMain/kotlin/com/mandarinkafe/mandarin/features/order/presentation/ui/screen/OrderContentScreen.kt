package com.mandarinkafe.mandarin.features.order.presentation.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.mandarinkafe.mandarin.MR
import com.mandarinkafe.mandarin.core.presentation.theme.Colors
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.core.presentation.theme.Typography
import com.mandarinkafe.mandarin.features.order.presentation.ui.components.AlertAboutPickupOnly
import com.mandarinkafe.mandarin.features.order.presentation.ui.components.DeliveryTypeChooser
import com.mandarinkafe.mandarin.features.order.presentation.ui.components.DeliveryTypeTitle
import com.mandarinkafe.mandarin.features.order.presentation.ui.components.OrderSummaryData
import com.mandarinkafe.mandarin.features.order.presentation.ui.components.PaymentChooser
import com.mandarinkafe.mandarin.features.order.presentation.ui.components.PersonalInfo
import com.mandarinkafe.mandarin.features.order.presentation.ui.components.SavedAddressesSection
import com.mandarinkafe.mandarin.features.order.presentation.ui.components.SelfPickupInfo
import com.mandarinkafe.mandarin.features.order.presentation.ui.components.SubmitOrderButton
import com.mandarinkafe.mandarin.features.order.presentation.ui.components.UtensilPreferences
import com.mandarinkafe.mandarin.features.order.presentation.viewmodel.OrderContract.OrderEvent
import com.mandarinkafe.mandarin.features.order.presentation.viewmodel.OrderContract.OrderState
import com.mandarinkafe.mandarin.util.presentation.ui.components.ConsentTextWithLinks
import com.mandarinkafe.mandarin.util.presentation.ui.components.textfields.MyTextField
import com.mandarinkafe.mandarin.util.presentation.ui.components.ScreenTitleWithBackButton
import dev.icerock.moko.resources.compose.stringResource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun OrderContent(
    state: OrderState,
    onEvent: (OrderEvent) -> Unit,
    scrollState: LazyListState,
    coroutineScope: CoroutineScope,
    onDeleteRequest: (String) -> Unit,
    showAllAddresses: Boolean,
    onToggleShowAll: () -> Unit,
    onBackClick: () -> Unit,
) {

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Colors.AppBlack)
            .padding(Dimens.MarginSmall8),
        state = scrollState,
    ) {
        item {
            ScreenTitleWithBackButton(
                name = stringResource(MR.strings.submit_order_screen_title),
                onBackClick = onBackClick
            )
        }
        item {
            with(state) {
                PersonalInfo(
                    nameQuery = userInfo.name,
                    phoneQuery = userInfo.phone,
                    isError = isError,
                    phoneIsValid = userInfo.phoneIsValid,
                    showSaveUserInfoCheckbox = showSaveUserInfoCheckbox,
                    saveUserInfoCheckboxText = stringResource(saveUserInfoCheckboxText),
                    saveUserInfo = saveUserInfo,
                    onNameEntered = { onEvent(OrderEvent.SetName(it)) },
                    onPhoneChanged = { onEvent(OrderEvent.SetPhone(it)) },
                    onSaveUserInfoToggled = { onEvent(OrderEvent.ToggleSaveUserInfo(it)) },
                )
            }
        }

        item { Spacer(Modifier.height(Dimens.MarginStandard16)) }

        item {
            DeliveryTypeTitle(
                chosen = state.deliveryInfo.deliveryType,
                isError = state.isError,
            )
        }

        item {
            with(state) {
                AlertAboutPickupOnly(
                    containsAlcohol = containsAlcohol,
                    pickupOnly = pickupOnly,
                    pickupOnlyPositionsNames = pickupOnlyPositionsNames,
                    onRemovePickupOnly = { onEvent(OrderEvent.RemovePickupOnly) },
                )
            }
        }


        item {
            with(state) {
                DeliveryTypeChooser(
                    chosen = deliveryInfo.deliveryType,
                    deliveryEnabled = !pickupOnly && !containsAlcohol,
                    isError = isError,
                    onDeliverySelected = { onEvent(OrderEvent.SetDeliveryType(it)) },
                )
            }
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
                    text = stringResource(MR.strings.choose_address),
                    style = Typography.ErrorTextStyle
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
                    onDeleteRequest = onDeleteRequest,
                    showAllAddresses = showAllAddresses,
                    onToggleShowAll = onToggleShowAll
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
                labelRes = MR.strings.your_comment,
                onValueChange = { onEvent(OrderEvent.SetComment(it)) }
            )
        }

        item { Spacer(Modifier.height(Dimens.MarginStandard16)) }

        item {
            with(state.cartSummary) {
                OrderSummaryData(
                    cartSum = totalCartSum,
                    discountSum = discountSum,
                    discountSize = discountPercent,
                    deliveryCost = state.deliveryCost,
                    containNotDiscountable = containNotDiscountable,
                    addressInNotInDeliveryArea = state.deliveryInfo.addressOutOfDeliveryZone,
                    deliveryInfoIsLoading = state.deliveryInfo.isLoading,
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
            ConsentTextWithLinks(buttonName = stringResource(MR.strings.submit_order))
        }

        item { Spacer(Modifier.height(Dimens.MarginSuperHugeForCheckoutButton)) }
    }
}