package com.mandarinkafe.mandarin.features.order.presentation.viewmodel

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
            is OrderEvent.SetAddress -> TODO()
            is OrderEvent.SetApartmentDetails -> TODO()
            is OrderEvent.SetChangeFrom -> TODO()
            is OrderEvent.SetChosenUtensils -> TODO()
            is OrderEvent.SetComment -> TODO()
            is OrderEvent.SetDeliveryType -> TODO()
            is OrderEvent.SetName -> TODO()
            is OrderEvent.SetNoNeedUtensils -> TODO()
            is OrderEvent.SetPaymentType -> TODO()
            is OrderEvent.SetPhone -> TODO()
        }
    }

    override fun setLoading(isLoading: Boolean) {
        // не применимо к данному экрану
    }
}