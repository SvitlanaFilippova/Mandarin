package com.mandarinkafe.mandarin.features.order.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.mandarinkafe.mandarin.features.order.domain.api.GetDeliveryZoneUseCase
import com.mandarinkafe.mandarin.features.order.domain.models.DeliveryType
import com.mandarinkafe.mandarin.features.order.domain.models.PaymentType
import com.mandarinkafe.mandarin.features.order.domain.models.Utensil
import com.mandarinkafe.mandarin.features.order.presentation.models.UiAddress
import com.mandarinkafe.mandarin.features.order.presentation.viewmodel.OrderContract.OrderEffect
import com.mandarinkafe.mandarin.features.order.presentation.viewmodel.OrderContract.OrderEvent
import com.mandarinkafe.mandarin.features.order.presentation.viewmodel.OrderContract.OrderState
import com.mandarinkafe.mandarin.util.Constants.VALID_PHONE_LENGTH
import com.mandarinkafe.mandarin.util.debounce
import com.mandarinkafe.mandarin.util.presentation.BaseViewModel
import com.yandex.mapkit.geometry.Point
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class OrderViewModel @Inject constructor(
    private val getDeliveryZone: GetDeliveryZoneUseCase,
) : BaseViewModel<OrderEvent, OrderEffect, OrderState>() {

    private val getCoordinatesDebounce = debounce<String>(
        GET_COORDINATES_DELAY,
        viewModelScope,
        useLastParam = true
    ) { expression ->
        validateAddress(expression)
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
            is OrderEvent.CreateNewAddress -> goToLocationScreen()
            is OrderEvent.EditLocation -> goToLocationScreen(event.address)
            is OrderEvent.NoChangeToggled -> setNoChange(event.noChange)
        }
    }

    fun cancelSearchDebounce() {
        getCoordinatesDebounce.cancel()
    }

    private fun submitOrder() {
        Log.d("DEBUG ORDER", "submitOrder clicked")
        sendEffect(OrderEffect.SubmitOrder)
    }

    private fun setAddressMainInfo(query: String) {
        cancelSearchDebounce()
        getCoordinatesDebounce.invoke(query)
    }

    private fun goToLocationScreen(address: UiAddress? = null) {
        sendEffect(OrderEffect.GoToAddressScreen)
    }

    private fun validateAddress(query: String) {
        Log.d("DEBUG ORDER", "validateAddress,  started")
    }

    private fun onLocationReceived(point: Point) {
        val zones = getDeliveryZone(point)
        val bestZone = zones.minByOrNull { it.id }
        if (bestZone != null) {
            setState {
                copy(
                    deliveryZone = bestZone,
                    addressValidated = true,
                    addressValidationInProgress = false
                )
            }
            Log.d("DEBUG ORDER", "onLocationReceived,  deliveryZone:  $bestZone")
        } else setAddressValidationError()
    }

    private fun setAddressValidationError() {
        setState {
            copy(addressValidated = false, addressValidationInProgress = false)
        }
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

    private fun checkDeliveryCost() {

    }

    private fun setError() {
        setState { copy(isError = true) }
    }

    override fun setLoading(isLoading: Boolean) {
        // не применимо к данному экрану
    }

    private companion object {
        private const val GET_COORDINATES_DELAY = 3000L
    }
}