package com.mandarinkafe.mandarin.features.order.presentation.viewmodel

import androidx.lifecycle.viewModelScope
import com.mandarinkafe.mandarin.R
import com.mandarinkafe.mandarin.core.domain.api.ClearCartUseCase
import com.mandarinkafe.mandarin.core.domain.api.ObserveCartItemsUseCase
import com.mandarinkafe.mandarin.core.domain.models.Address
import com.mandarinkafe.mandarin.core.domain.models.IncomingOrder
import com.mandarinkafe.mandarin.core.domain.models.UserInfo
import com.mandarinkafe.mandarin.features.address.address.domain.api.GetDeliveryZoneUseCase
import com.mandarinkafe.mandarin.features.infrastructure.domain.api.CheckIfTerminalIsAliveUseCase
import com.mandarinkafe.mandarin.features.infrastructure.domain.api.GetPaymentTypesUseCase
import com.mandarinkafe.mandarin.features.order.data.mapper.toDomain
import com.mandarinkafe.mandarin.features.order.domain.api.ApplyPhoneDiscountUseCase
import com.mandarinkafe.mandarin.features.order.domain.api.CalculateCartTotalWithDiscountUseCase
import com.mandarinkafe.mandarin.features.order.domain.api.PickupOnlyRemoveUseCase
import com.mandarinkafe.mandarin.features.order.domain.api.ResolvePickupPointUseCase
import com.mandarinkafe.mandarin.features.order.domain.api.SaveOrderToHistoryUseCase
import com.mandarinkafe.mandarin.features.order.domain.api.UserInfoRepository
import com.mandarinkafe.mandarin.features.order.domain.models.DeliveryType
import com.mandarinkafe.mandarin.features.order.domain.models.Utensil
import com.mandarinkafe.mandarin.features.order.presentation.models.UiPaymentType
import com.mandarinkafe.mandarin.features.order.presentation.viewmodel.OrderContract.OrderEffect
import com.mandarinkafe.mandarin.features.order.presentation.viewmodel.OrderContract.OrderEffect.AddNewAddress
import com.mandarinkafe.mandarin.features.order.presentation.viewmodel.OrderContract.OrderEffect.EditAddress
import com.mandarinkafe.mandarin.features.order.presentation.viewmodel.OrderContract.OrderEffect.ShowMassage
import com.mandarinkafe.mandarin.features.order.presentation.viewmodel.OrderContract.OrderEffect.ShowSuccess
import com.mandarinkafe.mandarin.features.order.presentation.viewmodel.OrderContract.OrderEvent
import com.mandarinkafe.mandarin.features.order.presentation.viewmodel.OrderContract.OrderState
import com.mandarinkafe.mandarin.features.order.presentation.viewmodel.helpers.CartObserver
import com.mandarinkafe.mandarin.features.order.presentation.viewmodel.helpers.OrderCreator
import com.mandarinkafe.mandarin.features.order.presentation.viewmodel.state.DeliveryInfo
import com.mandarinkafe.mandarin.features.order.presentation.viewmodel.state.PaymentInfo
import com.mandarinkafe.mandarin.features.order.presentation.viewmodel.state.UserInfoUi
import com.mandarinkafe.mandarin.features.savedadresses.domain.api.GetSavedAddressesUseCase
import com.mandarinkafe.mandarin.features.savedadresses.domain.api.RemoveAddressUseCase
import com.mandarinkafe.mandarin.util.Constants
import com.mandarinkafe.mandarin.util.Constants.VALID_PHONE_LENGTH
import com.mandarinkafe.mandarin.util.Resource
import com.mandarinkafe.mandarin.util.UiText
import com.mandarinkafe.mandarin.util.presentation.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrderViewModel @Inject constructor(
    observeCartItemsUseCase: ObserveCartItemsUseCase,
    resolvePickupPoint: ResolvePickupPointUseCase,
    private val pickupOnlyRemover: PickupOnlyRemoveUseCase,
    private val orderCreator: OrderCreator,
    private val getDeliveryZone: GetDeliveryZoneUseCase,
    private val getSavedAddressesUseCase: GetSavedAddressesUseCase,
    private val removeAddress: RemoveAddressUseCase,
    private val getPaymentTypesUseCase: GetPaymentTypesUseCase,
    private val clearCart: ClearCartUseCase,
    private val calculateCartTotalWithDiscount: CalculateCartTotalWithDiscountUseCase,
    private val applyPhoneDiscount: ApplyPhoneDiscountUseCase,
    private val saveOrderToHistory: SaveOrderToHistoryUseCase,
    private val checkIfTerminalIsAlive: CheckIfTerminalIsAliveUseCase,
    private val userInfoRepository: UserInfoRepository
) : BaseViewModel<OrderEvent, OrderEffect, OrderState>() {

    private val cartObserver = CartObserver(
        observeCartItems = observeCartItemsUseCase,
        resolvePickupPoint = resolvePickupPoint,
        recalculateCartSummary = ::recalculateCartSummary
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
            is OrderEvent.SetPhone -> setPhone(event.query)
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
            is OrderEvent.SubmitOrder -> checkIfOrderCanBeSubmitted()
            is OrderEvent.StopObservingStatus -> orderCreator.stopObserving()
            is OrderEvent.ToggleSaveUserInfo -> toggleSaveUserInfo(event.checked)
            is OrderEvent.RemovePickupOnly -> removePickupOnly()
        }
    }

    private fun getInitData() {
        getPaymentTypes()
        getSavedAddresses()
        getSavedUserInfo()
    }

    private fun removePickupOnly() {
        viewModelScope.launch {
            val itemIds = state.value.pickupOnlyPositions.map { it.id }
            pickupOnlyRemover(itemIds)
            sendEffect(ShowMassage(UiText.StringResource(R.string.pickup_only_positions_removed)))
        }
    }

    private fun getPaymentTypes() {
        viewModelScope.launch {
            val response = getPaymentTypesUseCase()
            when (response) {
                is Resource.ErrorNoInternet ->
                    sendErrorEffect(UiText.StringResource(R.string.error_no_internet))

                is Resource.Success -> {
                    if (response.data != null) {
                        setState { copy(paymentInfo = paymentInfo.copy(availablePaymentTypes = response.data)) }
                    } else {
                        sendErrorEffect(UiText.StringResource(R.string.error_payment_types_unavailable))
                    }
                }

                else -> {
                    sendErrorEffect(UiText.StringResource(R.string.error_payment_types_unavailable))
                }
            }
        }
    }

    private fun toggleSaveUserInfo(checked: Boolean) {
        setState { copy(saveUserInfo = checked) }
    }

    private fun getSavedUserInfo() {
        viewModelScope.launch {
            val savedUserInfo = userInfoRepository.getUserInfo()
            setState {
                if (savedUserInfo != null) {
                    // Есть сохранённые данные → не показываем чекбокс
                    copy(
                        userInfo = userInfo.copy(
                            name = savedUserInfo.name,
                            phone = savedUserInfo.phone
                        ),
                        showSaveUserInfoCheckbox = false
                    )
                } else {
                    // Нет сохранённых данных → показываем чекбокс
                    copy(showSaveUserInfoCheckbox = true)
                }
            }
            savedUserInfo?.let { checkDiscount(it.phone) }
        }
    }

    private fun saveUserInfo() {
        viewModelScope.launch {
            val userInfo = state.value.userInfo
            userInfoRepository.saveUserInfo(
                UserInfo(
                    name = userInfo.name,
                    phone = userInfo.phone
                )
            )
        }
    }

    private fun updateUserInfo(newInfo: UserInfoUi) {
        viewModelScope.launch {
            val saved = userInfoRepository.getUserInfo()
            val showCheckbox = when {
                saved == null -> true // первый раз сохраняем
                saved.name != newInfo.name || saved.phone != newInfo.phone -> true // данные изменились
                else -> false
            }
            val text = if (saved != null) {
                UiText.StringResource(R.string.update_saved_name_and_phone)
            } else {
                UiText.StringResource(R.string.save_name_and_phone)
            }

            setState {
                copy(
                    userInfo = newInfo,
                    showSaveUserInfoCheckbox = showCheckbox,
                    saveUserInfoCheckboxText = text
                )
            }
        }
    }

    private fun selectAddressById(id: String) {
        val address = state.value.deliveryInfo.savedAddresses.first { it.id == id }
        setAddress(address)
    }

    private fun removeSavedAddress(id: String) {
        viewModelScope.launch { removeAddress(id) }
        getSavedAddresses()
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
            val addressList = getSavedAddressesUseCase().reversed()
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
            val deliveryZone = getDeliveryZone(address.point)
            setState {
                val newDeliveryInfo = deliveryInfo.copy(
                    deliveryZone = deliveryZone,
                    isLoading = false
                )
                copy(deliveryInfo = newDeliveryInfo)
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
        updateUserInfo(newInfo)
    }

    private fun setNoNeedUtensils(noNeedUtensils: Boolean) {
        setState { copy(utensils = utensils.copy(noNeedUtensils = noNeedUtensils)) }
    }

    private fun setPaymentType(paymentType: UiPaymentType) {
        setState {
            copy(paymentInfo = paymentInfo.copy(chosenPaymentType = paymentType))
        }
    }

    private fun setPhone(rawPhone: String) {
        viewModelScope.launch {
            val digitsOnly = rawPhone.filter { it.isDigit() }
            val normalized = when {
                digitsOnly.startsWith("7") -> digitsOnly.drop(1)
                digitsOnly.startsWith("8") -> digitsOnly.drop(1)
                else -> digitsOnly
            }
            val phone = normalized.take(VALID_PHONE_LENGTH)

            val newInfo = state.value.userInfo.copy(phone = phone)
            updateUserInfo(newInfo)
            checkDiscount(phone)
        }
    }

    private fun checkDiscount(phone: String) {
        viewModelScope.launch {
            val discount = applyPhoneDiscount(phone, state.value.cartSummary.discountPercent)
            if (discount.shouldUpdate) {
                setState {
                    copy(
                        cartSummary = cartSummary.copy(
                            discountPercent = discount.discountSize,
                            discountId = discount.discountId
                        )
                    )
                }
                recalculateCartSummary(discount.discountSize)
            }
        }
    }

    private fun showMissingRequiredInfo() {
        setState { copy(isError = true) }
        sendErrorEffect(UiText.StringResource(R.string.error_missing_required_fields))
    }

    private fun recalculateCartSummary(discountSize: Int? = null) {
        setState {
            val discountSize = discountSize ?: cartSummary.discountPercent
            val cartSumWithDiscount =
                calculateCartTotalWithDiscount(cartSummary.items, discountSize)
            copy(
                cartSummary = cartSummary.copy(
                    cartSumWithDiscount = cartSumWithDiscount,
                )
            )
        }
    }

    override fun setLoading(isLoading: Boolean) {
        setState { copy(isLoading = isLoading) }
    }

    private fun checkIfOrderCanBeSubmitted() {
        viewModelScope.launch {
            setLoading()
            val terminalResponse = checkIfTerminalIsAlive()
            when (terminalResponse) {
                is Resource.Success -> {
                    if (terminalResponse.data == true) {
                        submitOrder()
                    } else {
                        sendErrorEffect(
                            UiText.StringResource(R.string.error_terminal_unavailable)
                        )
                    }
                }

                is Resource.ErrorNoInternet -> {
                    sendErrorEffect(UiText.StringResource(R.string.error_no_internet))
                }

                else -> sendErrorEffect(UiText.StringResource(R.string.error_unknown))
            }
        }
    }

    private fun submitOrder() {
        if (state.value.saveUserInfo) saveUserInfo()

        viewModelScope.launch {
            setLoading()
            val order = state.value.toDomain(
                paymentType = state.value.paymentInfo.chosenPaymentTypeDomain
            )
            orderCreator.submit(
                scope = viewModelScope,
                order = order,
                onSuccess = ::onSuccessOrderCreation,
                onError = ::sendErrorEffect,
                onLoading = ::setLoading
            )
        }
    }

    private fun onSuccessOrderCreation(order: IncomingOrder) {
        clearState()
        sendEffect(ShowSuccess(order.id))
        viewModelScope.launch {
            clearCart()
            saveOrderToHistory(order)
        }
    }

    private fun sendErrorEffect(msg: UiText) {
        setLoading(false)
        sendEffect(ShowMassage(msg))
    }
}