package com.mandarinkafe.mandarin.features.order.presentation.viewmodel

import com.mandarinkafe.mandarin.MR
import com.mandarinkafe.mandarin.core.domain.models.IncomingOrder
import com.mandarinkafe.mandarin.features.menu.domain.api.OrderAcceptStatusRepository
import com.mandarinkafe.mandarin.features.menu.domain.models.closingTimeOrPlaceholder
import com.mandarinkafe.mandarin.features.order.domain.api.SaveOrderToHistoryUseCase
import com.mandarinkafe.mandarin.features.order.presentation.mapper.toDomain
import com.mandarinkafe.mandarin.features.order.presentation.models.UiPaymentType
import com.mandarinkafe.mandarin.features.order.presentation.viewmodel.OrderContract.OrderEffect
import com.mandarinkafe.mandarin.features.order.presentation.viewmodel.OrderContract.OrderEffect.ShowOrderClosingDialog
import com.mandarinkafe.mandarin.features.order.presentation.viewmodel.OrderContract.OrderEffect.ShowSuccess
import com.mandarinkafe.mandarin.features.order.presentation.viewmodel.OrderContract.OrderState
import com.mandarinkafe.mandarin.features.order.presentation.viewmodel.helpers.OrderCreator
import com.mandarinkafe.mandarin.features.savedadresses.domain.CartContentUseCases
import com.mandarinkafe.mandarin.features.savedadresses.domain.OrderInfoUseCases
import com.mandarinkafe.mandarin.util.Resource
import com.mandarinkafe.mandarin.util.formatPhoneNumberForSdk
import dev.icerock.moko.resources.StringResource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

internal data class OrderSubmitFlowDependencies(
    val scope: CoroutineScope,
    val orderAcceptStatusRepository: OrderAcceptStatusRepository,
    val infoUseCases: OrderInfoUseCases,
    val cartUseCases: CartContentUseCases,
    val orderCreator: OrderCreator,
    val saveOrderToHistory: SaveOrderToHistoryUseCase,
    val getState: () -> OrderState,
    val setState: (OrderState.() -> OrderState) -> Unit,
    val setLoading: (Boolean) -> Unit,
    val sendEffect: (OrderEffect) -> Unit,
    val sendErrorEffect: (StringResource, String?) -> Unit,
    val clearState: () -> Unit,
    val onRefreshUserInfoAfterOrder: () -> Unit,
    val saveUserName: () -> Unit,
    val showMissingRequiredInfo: () -> Unit,
)

internal class OrderViewModelSubmitFlow(
    private val deps: OrderSubmitFlowDependencies,
) {

    fun checkIfOrderCanBeSubmitted() {
        deps.scope.launch {
            deps.setLoading(true)
            val snapshot = deps.orderAcceptStatusRepository.fetchOrderAcceptStatusFresh()
            if (!snapshot.isAcceptingOrders) {
                deps.setLoading(false)
                deps.sendEffect(
                    ShowOrderClosingDialog(
                        isClosedForWholeDay = snapshot.isClosedForWholeDay,
                        closingTime = if (snapshot.isClosedForWholeDay) {
                            null
                        } else {
                            snapshot.closingTimeOrPlaceholder()
                        },
                    ),
                )
                return@launch
            }
            proceedTerminalThenSubmitOrder()
        }
    }

    fun proceedTerminalThenSubmitOrder() {
        deps.scope.launch {
            deps.setLoading(true)
            when (val terminalResponse = deps.infoUseCases.checkIfTerminalIsAlive()) {
                is Resource.Success -> {
                    if (terminalResponse.data == true) {
                        submitOrder()
                    } else {
                        deps.setLoading(false)
                        deps.sendErrorEffect(
                            MR.strings.error_terminal_unavailable,
                            null,
                        )
                    }
                }

                is Resource.ErrorNoInternet -> {
                    deps.setLoading(false)
                    deps.sendErrorEffect(MR.strings.error_no_internet, null)
                }

                else -> {
                    deps.setLoading(false)
                    deps.sendErrorEffect(MR.strings.error_unknown, null)
                }
            }
        }
    }

    private fun submitOrder() {
        val minAmountForOnlinePayment = 1.0
        val currentState = deps.getState()

        if (!currentState.isNameValid) {
            deps.showMissingRequiredInfo()
            return
        }

        if (currentState.paymentInfo.chosenPaymentType == UiPaymentType.ONLINE &&
            currentState.totalOrderSum < minAmountForOnlinePayment
        ) {
            deps.sendErrorEffect(MR.strings.error_online_payment_minimum_amount, null)
            deps.setState {
                copy(paymentInfo = paymentInfo.copy(chosenPaymentType = null))
            }
            return
        }

        if (currentState.shouldSaveUserName) deps.saveUserName()
        deps.scope.launch {
            deps.setLoading(true)
            val order = currentState.toDomain(
                paymentType = currentState.paymentInfo.chosenPaymentTypeDomain,
            )

            if (order.items.isEmpty()) {
                deps.sendErrorEffect(MR.strings.error_cart_empty_on_order, null)
                deps.setLoading(false)
                return@launch
            }

            deps.orderCreator.submit(
                scope = deps.scope,
                order = order,
                onSuccess = ::onSuccessOrderCreation,
                onError = deps.sendErrorEffect,
                onLoading = { deps.setLoading(true) },
            )
        }
    }

    private fun onSuccessOrderCreation(order: IncomingOrder) {
        val savedChosenPaymentType = deps.getState().paymentInfo.chosenPaymentType
        val savedUserPhone = deps.getState().userInfo.phone

        deps.clearState()

        deps.scope.launch {
            deps.cartUseCases.clearCart()
            val paymentMethodCode = savedChosenPaymentType?.code
            deps.saveOrderToHistory(order, paymentMethodCode)
        }

        val paymentMethodCode = savedChosenPaymentType?.code
        if (savedChosenPaymentType == UiPaymentType.ONLINE) {
            val userPhone = savedUserPhone.formatPhoneNumberForSdk()
            deps.sendEffect(
                OrderEffect.StartOnlinePayment(
                    order.id,
                    order.sum ?: 0.0,
                    userPhone,
                    paymentMethodCode,
                ),
            )
        } else {
            deps.sendEffect(ShowSuccess(order.id, paymentMethodCode))
        }
        deps.onRefreshUserInfoAfterOrder()
    }
}
