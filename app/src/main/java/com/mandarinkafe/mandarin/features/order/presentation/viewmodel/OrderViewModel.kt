package com.mandarinkafe.mandarin.features.order.presentation.viewmodel

import androidx.lifecycle.viewModelScope
import com.mandarinkafe.mandarin.core.domain.models.Address
import com.mandarinkafe.mandarin.features.address.address.domain.api.GetDeliveryZoneUseCase
import com.mandarinkafe.mandarin.features.address.savedadresses.domain.api.GetSavedAddressesUseCase
import com.mandarinkafe.mandarin.features.address.savedadresses.domain.api.RemoveAddressUseCase
import com.mandarinkafe.mandarin.features.order.domain.models.DeliveryType
import com.mandarinkafe.mandarin.features.order.domain.models.PaymentType
import com.mandarinkafe.mandarin.features.order.domain.models.Utensil
import com.mandarinkafe.mandarin.features.order.presentation.viewmodel.OrderContract.OrderEffect
import com.mandarinkafe.mandarin.features.order.presentation.viewmodel.OrderContract.OrderEvent
import com.mandarinkafe.mandarin.features.order.presentation.viewmodel.OrderContract.OrderState
import com.mandarinkafe.mandarin.util.Constants.VALID_PHONE_LENGTH
import com.mandarinkafe.mandarin.util.presentation.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrderViewModel @Inject constructor(
    private val getDeliveryZone: GetDeliveryZoneUseCase,
    private val getSavedAddresses: GetSavedAddressesUseCase,
    private val removeAddress: RemoveAddressUseCase
) :
    BaseViewModel<OrderEvent, OrderEffect, OrderState>() {

    init {
        getAddresses()
    }

    override fun setInitialState() = OrderState()

    override fun onEvent(event: OrderEvent) {
        when (event) {
            is OrderEvent.SetChangeFrom -> setChangeFrom(event.query)
            is OrderEvent.SetChosenUtensils -> setChosenUtensils(event.utensil, event.isChosen)
            is OrderEvent.SetComment -> setComment(event.query)
            is OrderEvent.SetDeliveryType -> setDeliveryType(event.deliveryType)
            is OrderEvent.SetName -> setName(event.query)
            is OrderEvent.SetNoNeedUtensils -> setNoNeedUtensils(event.noNeedUtensils)
            is OrderEvent.SetPaymentType -> setPaymentType(event.paymentType)
            is OrderEvent.SetPhone -> setPhone(event.query)
            is OrderEvent.OnMissingRequiredInfo -> setError()
            is OrderEvent.SubmitOrder -> submitOrder()
            is OrderEvent.AddNewAddress -> createNewAddress()
            is OrderEvent.EditAddress -> goToAddressEdit(event.address)
            is OrderEvent.NoChangeToggled -> setNoChange(event.noChange)
            is OrderEvent.SetAddress -> setAddress(event.address)
            is OrderEvent.RemoveAddress -> removeSavedAddress(event.id)
            is OrderEvent.RefreshAddresses -> getAddresses()
            is OrderEvent.SelectAddressById -> selectAddressById(event.id)
        }
    }

    private fun selectAddressById(id: String) {
        val address = state.value.savedAddresses.first { it.id == id }
        setAddress(address)
    }

    private fun removeSavedAddress(id: String) {
        viewModelScope.launch { removeAddress(id) }
        getAddresses()

    }

    private fun getAddresses() {
        viewModelScope.launch {
            val addressList = getSavedAddresses()
            setState { copy(savedAddresses = addressList) }
        }
    }

    private fun setAddress(address: Address) {
        setState {
            copy(
                chosenAddress = address,
                deliveryZone = getDeliveryZone(address.point)
            )
        }
    }

    private fun submitOrder() {
        sendEffect(OrderEffect.SubmitOrder)
    }

    private fun goToAddressEdit(address: Address) {
        sendEffect(OrderEffect.EditAddress(address))
    }

    private fun createNewAddress() {
        sendEffect(OrderEffect.AddNewAddress)
    }

    private fun setNoChange(noChange: Boolean) {
        setState { copy(noChange = noChange) }
    }

    private fun setChangeFrom(query: String) {
        setState { copy(changeFrom = query) }
    }

    private fun setChosenUtensils(utensil: Utensil, isChosen: Boolean) {
        setState {
            copy(
                chosenUtensils = if (isChosen) {
                    chosenUtensils + utensil
                } else {
                    chosenUtensils - utensil
                }
            )
        }
    }

    private fun setComment(query: String) {
        setState { copy(comment = query) }
    }

    private fun setDeliveryType(deliveryType: DeliveryType) {
        setState { copy(deliveryType = deliveryType) }
    }

    private fun setName(query: String) {
        setState { copy(name = query) }
    }

    private fun setNoNeedUtensils(noNeedUtensils: Boolean) {
        setState { copy(noNeedUtensils = noNeedUtensils) }
    }

    private fun setPaymentType(paymentType: PaymentType) {
        setState { copy(paymentType = paymentType) }
    }

    private fun setPhone(query: String) {
        val digitsOnly = query.filter { it.isDigit() }

        // Если первая цифра — 7 или 8, игнорируем
        val normalized = when {
            digitsOnly.startsWith("7") -> digitsOnly.drop(1)
            digitsOnly.startsWith("8") -> digitsOnly.drop(1)
            else -> digitsOnly
        }
        // Ограничиваем до 10 символов
        val limited = normalized.take(VALID_PHONE_LENGTH)

        setState { copy(phone = limited) }
    }

    private fun setError() {
        setState { copy(isError = true) }
    }

    override fun setLoading(isLoading: Boolean) {
        // не применимо к данному экрану
    }

}