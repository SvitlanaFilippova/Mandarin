package com.mandarinkafe.mandarin.features.order.presentation.viewmodel

import androidx.lifecycle.viewModelScope
import com.mandarinkafe.mandarin.MR
import com.mandarinkafe.mandarin.core.domain.models.Address
import com.mandarinkafe.mandarin.features.account.domain.api.UserInfoRepository
import com.mandarinkafe.mandarin.features.auth.domain.api.AuthRepository
import com.mandarinkafe.mandarin.features.menu.domain.api.OrderAcceptStatusRepository
import com.mandarinkafe.mandarin.features.order.domain.api.ApplyPhoneDiscountUseCase
import com.mandarinkafe.mandarin.features.order.domain.api.PickupOnlyRemoveUseCase
import com.mandarinkafe.mandarin.features.order.domain.api.SaveOrderToHistoryUseCase
import com.mandarinkafe.mandarin.features.order.domain.models.DeliveryType
import com.mandarinkafe.mandarin.features.order.domain.models.Utensil
import com.mandarinkafe.mandarin.features.order.presentation.models.UiPaymentType
import com.mandarinkafe.mandarin.features.order.presentation.viewmodel.OrderContract.OrderEffect
import com.mandarinkafe.mandarin.features.order.presentation.viewmodel.OrderContract.OrderEffect.AddNewAddress
import com.mandarinkafe.mandarin.features.order.presentation.viewmodel.OrderContract.OrderEffect.EditAddress
import com.mandarinkafe.mandarin.features.order.presentation.viewmodel.OrderContract.OrderEffect.ShowMessage
import com.mandarinkafe.mandarin.features.order.presentation.viewmodel.OrderContract.OrderEvent
import com.mandarinkafe.mandarin.features.order.presentation.viewmodel.OrderContract.OrderState
import com.mandarinkafe.mandarin.features.order.presentation.viewmodel.helpers.CartObserver
import com.mandarinkafe.mandarin.features.order.presentation.viewmodel.helpers.OrderCreator
import com.mandarinkafe.mandarin.features.order.presentation.viewmodel.state.DeliveryInfo
import com.mandarinkafe.mandarin.features.order.presentation.viewmodel.state.PaymentInfo
import com.mandarinkafe.mandarin.features.savedadresses.domain.AddressUseCases
import com.mandarinkafe.mandarin.features.savedadresses.domain.CartContentUseCases
import com.mandarinkafe.mandarin.features.savedadresses.domain.OrderInfoUseCases
import com.mandarinkafe.mandarin.util.Constants
import com.mandarinkafe.mandarin.util.Resource
import com.mandarinkafe.mandarin.util.presentation.BaseViewModel
import dev.icerock.moko.resources.StringResource
import kotlinx.coroutines.launch

class OrderViewModel(
    private val cartUseCases: CartContentUseCases,
    private val pickupOnlyRemover: PickupOnlyRemoveUseCase,
    private val orderCreator: OrderCreator,
    private val addressUseCases: AddressUseCases,
    private val infoUseCases: OrderInfoUseCases,
    private val applyPhoneDiscount: ApplyPhoneDiscountUseCase,
    private val saveOrderToHistory: SaveOrderToHistoryUseCase,
    private val userInfoRepository: UserInfoRepository,
    private val authRepository: AuthRepository,
    private val orderAcceptStatusRepository: OrderAcceptStatusRepository,
) : BaseViewModel<OrderEvent, OrderEffect, OrderState>() {

    private val cartObserver = CartObserver(
        observeCartItems = cartUseCases.observeCartItems,
        resolvePickupPoint = cartUseCases.resolvePickupPoint,
        recalculateCartSummary = ::recalculateCartSummary
    )

    private val orderUserInfo = OrderViewModelUserInfo(
        scope = viewModelScope,
        userInfoRepository = userInfoRepository,
        authRepository = authRepository,
        getState = { state.value },
        setState = { setState(it) },
        onPhoneChangedDiscount = ::checkDiscount,
    )

    private val submitFlow = OrderViewModelSubmitFlow(
        OrderSubmitFlowDependencies(
            scope = viewModelScope,
            orderAcceptStatusRepository = orderAcceptStatusRepository,
            infoUseCases = infoUseCases,
            cartUseCases = cartUseCases,
            orderCreator = orderCreator,
            saveOrderToHistory = saveOrderToHistory,
            getState = { state.value },
            setState = { setState(it) },
            setLoading = ::setLoading,
            sendEffect = ::sendEffect,
            sendErrorEffect = ::sendErrorEffect,
            clearState = ::clearState,
            onRefreshUserInfoAfterOrder = { orderUserInfo.getSavedUserInfo() },
            saveUserName = { orderUserInfo.saveUserName() },
            showMissingRequiredInfo = ::showMissingRequiredInfo,
        ),
    )

    init {
        getSavedAddresses()
        observeCartItems()
    }

    override fun setInitialState() = OrderState()

    override fun onEvent(event: OrderEvent) {
        when (event) {
            is OrderEvent.GetInitData -> getInitData()
            is OrderEvent.RefreshAddresses -> getSavedAddresses()
            is OrderEvent.SetName -> setName(event.query)
            is OrderEvent.SetDeliveryType -> setDeliveryType(event.deliveryType)
            is OrderEvent.SetPaymentType -> setPaymentType(event.paymentType)
            is OrderEvent.SetChangeFrom -> setChangeFrom(event.query)
            is OrderEvent.SetNoNeedUtensils -> setNoNeedUtensils(event.noNeedUtensils)
            is OrderEvent.SetChosenUtensils -> setChosenUtensils(event.utensil, event.isChosen)
            is OrderEvent.SetComment -> setComment(event.query)
            is OrderEvent.AddNewAddress -> createNewAddress()
            is OrderEvent.EditAddress -> goToAddressEdit(event.address)
            is OrderEvent.NoChangeToggled -> setNoChange(event.noChange)
            is OrderEvent.SetAddress -> setAddress(event.address)
            is OrderEvent.RemoveAddress -> removeSavedAddress(event.id)
            is OrderEvent.SelectAddressById -> selectAddressById(event.id)
            is OrderEvent.OnMissingRequiredInfo -> showMissingRequiredInfo()
            is OrderEvent.SubmitOrder -> submitFlow.checkIfOrderCanBeSubmitted()
            is OrderEvent.OrderClosingDialogConfirm -> submitFlow.proceedTerminalThenSubmitOrder()
            is OrderEvent.OrderClosingDialogDismiss -> Unit
            is OrderEvent.StopObservingStatus -> orderCreator.stopObserving()
            is OrderEvent.ToggleSaveUserInfo -> toggleSaveUserInfo(event.checked)
            is OrderEvent.RemovePickupOnly -> removePickupOnly()
        }
    }

    private fun getInitData() {
        getPaymentTypes()
        getSavedAddresses()
        orderUserInfo.getSavedUserInfo()
    }

    private fun removePickupOnly() {
        viewModelScope.launch {
            val itemIds = state.value.pickupOnlyPositions.map { it.id }
            pickupOnlyRemover(itemIds)
            sendEffect(ShowMessage(MR.strings.pickup_only_positions_removed))
        }
    }

    private fun getPaymentTypes() {
        viewModelScope.launch {
            when (val response = infoUseCases.getPaymentTypesUseCase()) {
                is Resource.ErrorNoInternet ->
                    sendErrorEffect(MR.strings.error_no_internet)

                is Resource.Success -> {
                    if (response.data != null) {
                        setState { copy(paymentInfo = paymentInfo.copy(availablePaymentTypes = response.data)) }
                    } else {
                        sendErrorEffect(MR.strings.error_payment_types_unavailable)
                    }
                }

                else -> {
                    sendErrorEffect(MR.strings.error_payment_types_unavailable)
                }
            }
        }
    }

    private fun toggleSaveUserInfo(checked: Boolean) {
        setState { copy(shouldSaveUserName = checked) }
    }

    private fun selectAddressById(id: String) {
        viewModelScope.launch {
            // Пытаемся найти адрес в текущем списке
            var address = state.value.deliveryInfo.savedAddresses.firstOrNull { it.id == id }

            // Если адрес не найден, обновляем список адресов и повторяем попытку
            if (address == null) {
                // Обновляем адреса и ждем завершения
                val addressList = addressUseCases.getSavedAddressesUseCase()
                setState {
                    val newDeliveryInfo = deliveryInfo.copy(
                        savedAddresses = addressList
                    )
                    copy(deliveryInfo = newDeliveryInfo)
                }
                // Пытаемся найти адрес в только что полученном списке
                address = addressList.firstOrNull { it.id == id }
            }

            // Если адрес найден - выбираем его, иначе просто игнорируем (безопасная обёртка)
            if (address != null) {
                setAddress(address)
            }
        }
    }

    private fun removeSavedAddress(id: String) {
        viewModelScope.launch {
            addressUseCases.removeAddress(id)
            getSavedAddresses()
        }
    }

    private fun observeCartItems() {
        viewModelScope.launch {
            cartObserver.observe { items, containNotDiscountable, isPickupOnly, pickupPoint, containsAlcohol ->
                setState {
                    val newDeliveryInfo =
                        if (isPickupOnly) {
                            deliveryInfo.copy(
                                deliveryType = DeliveryType.SELF_PICKUP,
                                chosenAddress = null
                            )
                        } else {
                            deliveryInfo
                        }
                    val newCartSummary = cartSummary.copy(
                        items = items,
                        containNotDiscountable = containNotDiscountable
                    )

                    val pickupOnlyPositions = items
                        .filter {
                            it.customizedMeal.meal.isPickupOnly ||
                                    it.customizedMeal.meal.labels.any { label -> label.name == Constants.LABEL_18 }
                        }
                    val pickupOnlyPositionsNames = pickupOnlyPositions
                        .map { it.name }
                        .distinct()

                    copy(
                        cartSummary = newCartSummary,
                        deliveryInfo = newDeliveryInfo,
                        pickupPoint = pickupPoint,
                        containsAlcohol = containsAlcohol,
                        pickupOnly = isPickupOnly,
                        pickupOnlyPositions = pickupOnlyPositions,
                        pickupOnlyPositionsNames = pickupOnlyPositionsNames,
                    )
                }
            }
        }
    }

    private fun getSavedAddresses() {
        viewModelScope.launch {
            val addressList = addressUseCases.getSavedAddressesUseCase()
            setState {
                val newDeliveryInfo = deliveryInfo.copy(
                    savedAddresses = addressList
                )
                copy(deliveryInfo = newDeliveryInfo)
            }
        }
    }

    private fun setAddress(address: Address) {
        setState {
            copy(
                deliveryInfo = deliveryInfo.copy(
                    chosenAddress = address,
                    isLoading = true
                )
            )
        }
        viewModelScope.launch {
            val deliveryZone = addressUseCases.getDeliveryZone(address.point)
            setState {
                val newDeliveryInfo = deliveryInfo.copy(
                    deliveryZone = deliveryZone,
                    isLoading = false
                )
                copy(deliveryInfo = newDeliveryInfo)
            }
            // Проверяем и сбрасываем онлайн-оплату после обновления состояния
            resetOnlinePaymentIfNeeded()
        }
    }

    /**
     * Сбрасывает выбор онлайн-оплаты, если сумма заказа меньше 1 рубля.
     * Используется как вспомогательный метод для предотвращения дублирования кода.
     */
    private fun resetOnlinePaymentIfNeeded() {
        val minAmountForOnlinePayment = 1.0
        val currentState = state.value
        if (currentState.paymentInfo.chosenPaymentType == UiPaymentType.ONLINE &&
            currentState.totalOrderSum < minAmountForOnlinePayment
        ) {
            setState {
                copy(paymentInfo = paymentInfo.copy(chosenPaymentType = null))
            }
        }
    }

    private fun clearState() {
        setState {
            val savedAddresses = deliveryInfo.savedAddresses
            val paymentTypes = paymentInfo.availablePaymentTypes
            OrderState(
                deliveryInfo = DeliveryInfo(savedAddresses = savedAddresses),
                paymentInfo = PaymentInfo(availablePaymentTypes = paymentTypes)
            )
        }
    }

    private fun goToAddressEdit(address: Address) {
        sendEffect(EditAddress(address))
    }

    private fun createNewAddress() {
        sendEffect(AddNewAddress)
    }

    private fun setNoChange(noChange: Boolean) {
        setState { copy(paymentInfo = paymentInfo.copy(noChange = noChange)) }
    }

    private fun setChangeFrom(query: String) {
        setState { copy(paymentInfo = paymentInfo.copy(changeFrom = query)) }
    }

    private fun setChosenUtensils(utensil: Utensil, isChosen: Boolean) {
        setState {
            copy(
                utensils = utensils.copy(
                    chosenUtensils = with(utensils.chosenUtensils) {
                        if (isChosen) this + utensil else this - utensil
                    }
                )
            )
        }
    }

    private fun setComment(query: String) {
        setState { copy(comment = query) }
    }

    private fun setDeliveryType(deliveryType: DeliveryType) {
        setState { copy(deliveryInfo = deliveryInfo.copy(deliveryType = deliveryType)) }
    }

    private fun setName(query: String) {
        val newInfo = state.value.userInfo.copy(name = query)
        setState {
            copy(
                userInfo = newInfo
            )
        }
    }

    private fun setNoNeedUtensils(noNeedUtensils: Boolean) {
        setState { copy(utensils = utensils.copy(noNeedUtensils = noNeedUtensils)) }
    }

    private fun setPaymentType(paymentType: UiPaymentType) {
        val minAmountForOnlinePayment = 1.0
        val currentTotalSum = state.value.totalOrderSum

        // Проверяем, не пытаются ли выбрать онлайн-оплату для заказа меньше 1 рубля
        if (paymentType == UiPaymentType.ONLINE && currentTotalSum < minAmountForOnlinePayment) {
            // Сбрасываем выбор оплаты и показываем сообщение
            setState {
                copy(paymentInfo = paymentInfo.copy(chosenPaymentType = null))
            }
            sendErrorEffect(MR.strings.error_online_payment_minimum_amount)
            return
        }

        setState {
            copy(paymentInfo = paymentInfo.copy(chosenPaymentType = paymentType))
        }
    }

    private fun checkDiscount(phone: String) {
        viewModelScope.launch {
            val discount = applyPhoneDiscount(phone, state.value.cartSummary.discountPercent)
            if (discount.shouldUpdate) {
                setState {
                    copy(
                        cartSummary = cartSummary.copy(
                            discountPercent = discount.discountSize
                        )
                    )
                }
                recalculateCartSummary(discount.discountSize)
            }
        }
    }

    private fun showMissingRequiredInfo() {
        setState { copy(isError = true) }
        sendErrorEffect(MR.strings.error_missing_required_fields)
    }

    private fun recalculateCartSummary(discountSize: Int? = null) {
        setState {
            val discountSize = discountSize ?: cartSummary.discountPercent
            // Исключаем скрытые блюда из расчета суммы
            val visibleItems = cartSummary.items.filter { !it.customizedMeal.meal.isHidden }
            val cartSumWithDiscount =
                cartUseCases.calculateCartTotalWithDiscount(visibleItems, discountSize)
            val newCartSummary = cartSummary.copy(
                cartSumWithDiscount = cartSumWithDiscount,
            )

            // Вычисляем новую общую сумму заказа (корзина + доставка)
            val newDeliveryCost = when {
                deliveryInfo.isPickup -> 0
                deliveryInfo.deliveryZone == null -> 0
                cartSumWithDiscount < deliveryInfo.deliveryZone.freeDeliveryThreshold ->
                    deliveryInfo.deliveryZone.deliveryPrice

                else -> 0
            }
            val newTotalOrderSum = cartSumWithDiscount + newDeliveryCost.toDouble()

            // Автоматически сбрасываем онлайн-оплату, если сумма стала меньше 1 рубля
            val minAmountForOnlinePayment = 1.0
            val newPaymentInfo = if (paymentInfo.chosenPaymentType == UiPaymentType.ONLINE &&
                newTotalOrderSum < minAmountForOnlinePayment
            ) {
                paymentInfo.copy(chosenPaymentType = null)
            } else {
                paymentInfo
            }

            copy(
                cartSummary = newCartSummary,
                paymentInfo = newPaymentInfo
            )
        }
    }

    override fun setLoading(isLoading: Boolean) {
        setState { copy(isLoading = isLoading) }
    }

    private fun sendErrorEffect(msg: StringResource, details: String? = null) {
        setLoading(false)
        sendEffect(ShowMessage(msg, details))
    }
}
