package com.mandarinkafe.mandarin.features.order.presentation.viewmodel

import android.util.Log
import com.mandarinkafe.mandarin.features.order.domain.models.DeliveryType
import com.mandarinkafe.mandarin.features.order.domain.models.PaymentType
import com.mandarinkafe.mandarin.features.order.domain.models.Utensil
import com.mandarinkafe.mandarin.features.order.presentation.viewmodel.OrderContract.OrderEffect
import com.mandarinkafe.mandarin.features.order.presentation.viewmodel.OrderContract.OrderEvent
import com.mandarinkafe.mandarin.features.order.presentation.viewmodel.OrderContract.OrderState
import com.mandarinkafe.mandarin.util.presentation.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class OrderViewModel @Inject constructor() :
    BaseViewModel<OrderEvent, OrderEffect, OrderState>() {
    override fun setInitialState() = OrderState()

    override fun onEvent(event: OrderEvent) {
        when (event) {
            is OrderEvent.SetAddress -> setAddress(event.query)
            is OrderEvent.SetApartmentDetails -> setApartmentDetails(event.query)
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
            is OrderEvent.GetLocation -> getLocation()
        }
    }

    private fun getLocation() {
        Log.d("DEBUG ORDER", "getLocation clicked")
        //  TODO()
    }

    private fun submitOrder() {
        Log.d("DEBUG ORDER", "submitOrder clicked")
        sendEffect(OrderEffect.SubmitOrder)
    }

    private fun setError() {
        setState { copy(isError = true) }
    }

    private fun setAddress(query: String) {
        setState { copy(address = query) }
    }

    private fun setApartmentDetails(query: String) {
        setState { copy(apartmentDetails = query) }
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
        setState { copy(noNeedUtensils = noNeedUtensils, chosenUtensils = listOf()) }
    }

    private fun setPaymentType(paymentType: PaymentType) {
        setState { copy(paymentType = paymentType) }
    }

    private fun setPhone(query: String) {
        val digitsOnly = query.filter { it.isDigit() }

        // Если первая цифра — 7, 8 или плюс, игнорируем
        val normalized = when {
            digitsOnly.startsWith("7") -> digitsOnly.drop(1)
            digitsOnly.startsWith("8") -> digitsOnly.drop(1)
            else -> digitsOnly
        }

        // Ограничиваем до 10 символов
        val limited = normalized.take(10)

        setState { copy(phone = limited) }
    }

    override fun setLoading(isLoading: Boolean) {
        // не применимо к данному экрану
    }
}