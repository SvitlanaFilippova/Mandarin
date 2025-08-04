package com.mandarinkafe.mandarin.features.order.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.mandarinkafe.mandarin.core.domain.api.ClearCartUseCase
import com.mandarinkafe.mandarin.core.domain.api.ObserveCartItemsUseCase
import com.mandarinkafe.mandarin.core.domain.models.Address
import com.mandarinkafe.mandarin.features.address.address.domain.api.GetDeliveryZoneUseCase
import com.mandarinkafe.mandarin.features.address.savedadresses.domain.api.GetSavedAddressesUseCase
import com.mandarinkafe.mandarin.features.address.savedadresses.domain.api.RemoveAddressUseCase
import com.mandarinkafe.mandarin.features.order.data.mapper.toDomain
import com.mandarinkafe.mandarin.features.order.domain.api.ApplyPhoneDiscountUseCase
import com.mandarinkafe.mandarin.features.order.domain.api.CalculateCartTotalWithDiscountUseCase
import com.mandarinkafe.mandarin.features.order.domain.api.CreateOrderUseCase
import com.mandarinkafe.mandarin.features.order.domain.api.GetPaymentTypesUseCase
import com.mandarinkafe.mandarin.features.order.domain.api.ResolvePickupPointUseCase
import com.mandarinkafe.mandarin.features.order.domain.models.CreationStatus
import com.mandarinkafe.mandarin.features.order.domain.models.DeliveryType
import com.mandarinkafe.mandarin.features.order.domain.models.Utensil
import com.mandarinkafe.mandarin.features.order.presentation.models.UiPaymentType
import com.mandarinkafe.mandarin.features.order.presentation.viewmodel.OrderContract.OrderEffect
import com.mandarinkafe.mandarin.features.order.presentation.viewmodel.OrderContract.OrderEffect.AddNewAddress
import com.mandarinkafe.mandarin.features.order.presentation.viewmodel.OrderContract.OrderEffect.EditAddress
import com.mandarinkafe.mandarin.features.order.presentation.viewmodel.OrderContract.OrderEffect.ShowError
import com.mandarinkafe.mandarin.features.order.presentation.viewmodel.OrderContract.OrderEffect.ShowSuccess
import com.mandarinkafe.mandarin.features.order.presentation.viewmodel.OrderContract.OrderEvent
import com.mandarinkafe.mandarin.features.order.presentation.viewmodel.OrderContract.OrderState
import com.mandarinkafe.mandarin.features.order.presentation.viewmodel.state.DeliveryInfo
import com.mandarinkafe.mandarin.features.order.presentation.viewmodel.state.PaymentInfo
import com.mandarinkafe.mandarin.features.orderconfirmation.domain.api.ObserveOrderStatusUseCase
import com.mandarinkafe.mandarin.util.Resource
import com.mandarinkafe.mandarin.util.presentation.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrderViewModel @Inject constructor(
    private val getDeliveryZone: GetDeliveryZoneUseCase,
    private val getSavedAddressesUseCase: GetSavedAddressesUseCase,
    private val removeAddress: RemoveAddressUseCase,
    private val observeCartItemsUseCase: ObserveCartItemsUseCase,
    private val getPaymentTypesUseCase: GetPaymentTypesUseCase,
    private val createOrderUseCase: CreateOrderUseCase,
    private val clearCart: ClearCartUseCase,
    private val observeOrderStatus: ObserveOrderStatusUseCase,
    private val calculateCartTotalWithDiscount: CalculateCartTotalWithDiscountUseCase,
    private val resolvePickupPoint: ResolvePickupPointUseCase,
    private val applyPhoneDiscount: ApplyPhoneDiscountUseCase
) : BaseViewModel<OrderEvent, OrderEffect, OrderState>() {

    init {
        getSavedAddresses()
        observeCartItems()
    }

    override fun setInitialState() = OrderState()
    private var observeStatusJob: Job? = null

    override fun onEvent(event: OrderEvent) {
        when (event) {
            is OrderEvent.GetPaymentTypes -> getPaymentTypes()
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
            is OrderEvent.SubmitOrder -> submitOrder()
            is OrderEvent.StopObservingStatus -> stopObservingOrderStatus()
        }
    }

    private fun getPaymentTypes() {
        viewModelScope.launch {
            val types = getPaymentTypesUseCase()
            setState { copy(paymentInfo = paymentInfo.copy(availablePaymentTypes = types)) }
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
            observeCartItemsUseCase().collect { items ->
                setState {
                    // проверяем, есть ли в корзине блюда, на которые не распространяется скидка
                    val containNotDiscountable = items.keys.any { !it.meal.discountable }

                    // прововеряем, откуда нужно будет забирать заказ в случае самовывоза
                    val pickupPoint = resolvePickupPoint(items.keys)

                    // если была выбрана доставка, но заказ стал isPickupOnly - обнуляем данные доставки
                    val isPickupOnly = items.keys.any { it.meal.isPickupOnly }

                    // Обновляем инфо в стейте
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
                    copy(
                        cartSummary = newCartSummary,
                        deliveryInfo = newDeliveryInfo,
                        pickupPoint = pickupPoint,
                        pickupOnly = isPickupOnly
                    )
                }
                // вызываем перерасчёт скидки и стоимости доставки
                recalculateCartSummary()

            }
        }
    }

    private fun getSavedAddresses() {
        viewModelScope.launch {
            val addressList = getSavedAddressesUseCase().reversed()
            setState {
                val newDeliveryInfo = deliveryInfo.copy(savedAddresses = addressList)
                copy(deliveryInfo = newDeliveryInfo)
            }
        }
    }

    private fun setAddress(address: Address) {
        viewModelScope.launch {
            val deliveryZone = getDeliveryZone(address.point)
            setState {
                val newDeliveryInfo = deliveryInfo.copy(
                    chosenAddress = address,
                    deliveryZone = deliveryZone,
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
        setState { copy(userInfo = userInfo.copy(name = query)) }
    }

    private fun setNoNeedUtensils(noNeedUtensils: Boolean) {
        setState { copy(utensils = utensils.copy(noNeedUtensils = noNeedUtensils)) }
    }

    private fun setPaymentType(paymentType: UiPaymentType) {
        setState {
            copy(paymentInfo = paymentInfo.copy(chosenPaymentType = paymentType))
        }
    }

    private fun setPhone(query: String) {
        viewModelScope.launch {
            val result = applyPhoneDiscount(query, state.value.cartSummary.discountCategory)

            setState { copy(userInfo = userInfo.copy(phone = result.phone)) }
            if (result.shouldUpdate) {
                setState {
                    copy(cartSummary = cartSummary.copy(discountCategory = result.discountSize))
                }
                recalculateCartSummary(result.discountSize)
            }
        }
    }

    private fun showMissingRequiredInfo() {
        setState {
            copy(isError = true)
        }
        sendErrorEffect("Заполните все обязательные поля")
    }

    private fun recalculateCartSummary(discountSize: Int? = null) {
        setState {
            val discountSize = discountSize ?: cartSummary.discountCategory
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

    private fun submitOrder() {
        viewModelScope.launch {
            //  началась загрузка
            setLoading()
            val order = state.value.toDomain(
                paymentType = state.value.paymentInfo.chosenPaymentTypeDomain
            )

            when (val result = createOrderUseCase(order)) {
                is Resource.Loading -> setLoading()
                is Resource.Success -> {
                    val orderInfo = result.data
                    val status = result.data?.creationStatus
                    when (status) {
                        CreationStatus.IN_PROGRESS -> {
                            setLoading()
                            // Сохраняем orderId и начинаем наблюдение
                            observeOrderUntilSuccess(orderInfo.id)
                        }

                        CreationStatus.SUCCESS -> {
                            onSuccessOrderCreation(orderInfo.id)
                        }

                        CreationStatus.ERROR -> {
                            sendErrorEffect(
                                result.data.errorInfo?.message ?: "Не удалось создать заказ"
                            )
                        }

                        null -> {
                            sendErrorEffect("Ошибка: пустой ответ от сервера")
                        }
                    }
                }

                is Resource.ErrorNoInternet -> sendErrorEffect("Нет подключения к интернету")

                else -> {
                    val msg = result.message ?: "Не удалось отправить заказ"
                    sendErrorEffect(msg)
                }
            }
        }
    }

    private fun observeOrderUntilSuccess(orderId: String) {
        stopObservingOrderStatus()
        observeStatusJob = viewModelScope.launch {
            observeOrderStatus(orderId, ORDER_STATUS_UPD_DELAY).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        when (result.data?.creationStatus) {
                            CreationStatus.SUCCESS -> {
                                onSuccessOrderCreation(orderId)
                            }

                            CreationStatus.ERROR -> {
                                sendErrorEffect(
                                    result.data.errorInfo?.message ?: "Не удалось создать заказ"
                                )
                                Log.d(
                                    "DEBUG ORDER CREATE VM",
                                    "Error: ${result.data.errorInfo?.errorReason} ${result.data.errorInfo?.message}"
                                )
                                stopObservingOrderStatus()
                            }

                            else -> {
                                // Всё ещё в процессе — продолжаем крутить прелоадер
                                setLoading()
                            }
                        }
                    }

                    is Resource.ErrorNoInternet -> sendErrorEffect("Нет подключения к интернету")
                    is Resource.ErrorOther -> sendErrorEffect(
                        result.message ?: "Ошибка получения статуса"
                    )

                    else -> Unit
                }
            }
        }
    }

    private fun stopObservingOrderStatus() {
        observeStatusJob?.cancel()
    }

    private fun onSuccessOrderCreation(id: String) {
        clearState()
        clearCart()
        sendEffect(ShowSuccess(id))
        stopObservingOrderStatus()
    }

    private fun sendErrorEffect(msg: String) {
        setLoading(false)
        sendEffect(ShowError(msg))
    }

    private companion object {
        const val ORDER_STATUS_UPD_DELAY = 1000L
    }
}