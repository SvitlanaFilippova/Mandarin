package com.mandarinkafe.mandarin.features.order.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.mandarinkafe.mandarin.core.domain.api.ObserveCartItemsUseCase
import com.mandarinkafe.mandarin.core.domain.models.Address
import com.mandarinkafe.mandarin.core.domain.models.CustomizedMeal
import com.mandarinkafe.mandarin.core.domain.models.totalPrice
import com.mandarinkafe.mandarin.features.address.address.domain.api.GetDeliveryZoneUseCase
import com.mandarinkafe.mandarin.features.address.savedadresses.domain.api.GetSavedAddressesUseCase
import com.mandarinkafe.mandarin.features.address.savedadresses.domain.api.RemoveAddressUseCase
import com.mandarinkafe.mandarin.features.order.domain.api.CheckDiscountByPhoneUseCase
import com.mandarinkafe.mandarin.features.order.domain.models.DeliveryType
import com.mandarinkafe.mandarin.features.order.domain.models.PaymentType
import com.mandarinkafe.mandarin.features.order.domain.models.Utensil
import com.mandarinkafe.mandarin.features.order.presentation.viewmodel.OrderContract.OrderEffect
import com.mandarinkafe.mandarin.features.order.presentation.viewmodel.OrderContract.OrderEvent
import com.mandarinkafe.mandarin.features.order.presentation.viewmodel.OrderContract.OrderState
import com.mandarinkafe.mandarin.util.Constants.VALID_PHONE_LENGTH
import com.mandarinkafe.mandarin.util.Resource
import com.mandarinkafe.mandarin.util.presentation.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrderViewModel @Inject constructor(
    private val getDeliveryZone: GetDeliveryZoneUseCase,
    private val getSavedAddressesUseCase: GetSavedAddressesUseCase,
    private val removeAddress: RemoveAddressUseCase,
    private val observeCartItemsUseCase: ObserveCartItemsUseCase,
    private val checkDiscountByPhone: CheckDiscountByPhoneUseCase
) :
    BaseViewModel<OrderEvent, OrderEffect, OrderState>() {

    init {
        getSavedAddresses()
        observeCartItems()
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
            is OrderEvent.RefreshAddresses -> getSavedAddresses()
            is OrderEvent.SelectAddressById -> selectAddressById(event.id)
        }
    }

    private fun selectAddressById(id: String) {
        val address = state.value.savedAddresses.first { it.id == id }
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
                    val containNotDiscountable = items.keys.any { !it.meal.discountable }
                    val isPickupOnly = items.keys.any { it.meal.isPickupOnly }
                    val adjustedDeliveryType =
                        if (isPickupOnly && deliveryType == DeliveryType.DELIVERY) null else deliveryType
                    val adjustedAddress =
                        if (isPickupOnly && deliveryType == DeliveryType.DELIVERY) null else chosenAddress

                    copy(
                        cartItems = items,
                        containNotDiscountable = containNotDiscountable,
                        deliveryType = adjustedDeliveryType,
                        chosenAddress = adjustedAddress
                    )
                }
                // вызываем перерасчёт скидки и стоимости доставки
                with(state.value) {
                    recalculateCartState(
                        items = items,
                        discountSize = discountSize,
                        deliveryFreeThreshold = deliveryFreeThreshold,
                        deliveryBasePrice = deliveryBasePrice
                    )
                }

            }
        }
    }

    private fun getDeliveryCost(
        freeDeliveryThreshold: Int?,
        deliveryBasePrice: Int?,
        totalCartSumWithDiscount: Double?
    ): Int? {
        if (totalCartSumWithDiscount == null) return deliveryBasePrice

        val threshold = freeDeliveryThreshold?.toFloat() ?: 0f

        val deliveryCost = if (totalCartSumWithDiscount < threshold) {
            deliveryBasePrice
        } else {
            0
        }
        return deliveryCost
    }

    private fun getCartSumWithDiscount(
        items: Map<CustomizedMeal, Int>,
        discountAmount: Int
    ): Double {
        return items.entries.sumOf { (customizedMeal, quantity) ->
            val mealPrice = customizedMeal.meal.price.toDouble()
            val addsPrice = customizedMeal.adds.sumOf { it.price.toDouble() }
            val modifiersPrice = customizedMeal.modifiers.sumOf { group ->
                group.items.sumOf { it.price.toDouble() }
            }
            val fullPricePerItem = mealPrice + addsPrice + modifiersPrice
            val discountModifier = 1 - discountAmount / 100.0
            val discountedPricePerItem = if (customizedMeal.meal.discountable) {
                // если блюдо discountable, то скидка работает на всё
                fullPricePerItem * discountModifier
            } else {
                // иначе - только на добавки и модиаифкаторы, но не на само блюдо
                mealPrice + (addsPrice + modifiersPrice) * discountModifier
            }
            val total = discountedPricePerItem * quantity
            total
        }
    }

    private fun getSavedAddresses() {
        viewModelScope.launch {
            val addressList = getSavedAddressesUseCase().reversed()
            setState { copy(savedAddresses = addressList) }
        }
    }

    private fun setAddress(address: Address) {
        setState {
            val deliveryZone = getDeliveryZone(address.point)

            val deliveryCost = getDeliveryCost(
                freeDeliveryThreshold = deliveryZone?.freeDeliveryThreshold,
                deliveryBasePrice = deliveryZone?.deliveryPrice,
                totalCartSumWithDiscount = totalCartSumWithDiscount
            )

            copy(
                chosenAddress = address,
                deliveryFreeThreshold = deliveryZone?.freeDeliveryThreshold,
                deliveryBasePrice = deliveryZone?.deliveryPrice,
                deliveryRealCost = deliveryCost
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

        if (limited.length == VALID_PHONE_LENGTH) {
            checkDiscount(limited)
        }
    }

    private fun setError() {
        setState { copy(isError = true) }
    }

    override fun setLoading(isLoading: Boolean) {
        // не применимо к данному экрану
    }

    private fun checkDiscount(phone: String) {
        viewModelScope.launch {
            val result = checkDiscountByPhone.invoke(phone)
            val discount = when (result) {
                is Resource.Success -> result.data
                else -> null
            }
            discount?.let {
                setState { copy(discountSize = discount) }
                // Пересчёт после установки скидки
                with(state.value) {
                    recalculateCartState(
                        items = cartItems,
                        discountSize = it,
                        deliveryFreeThreshold = deliveryFreeThreshold,
                        deliveryBasePrice = deliveryBasePrice
                    )
                }
            }
        }
    }

    private fun recalculateCartState(
        items: Map<CustomizedMeal, Int>,
        discountSize: Int,
        deliveryFreeThreshold: Int?,
        deliveryBasePrice: Int?
    ) {
        Log.d("DEBUG DISCOUNT", "VM recalculateCartState, discountAmount: $discountSize")
        val totalCartSum = items.entries.sumOf { (item, quantity) ->
            item.totalPrice() * quantity
        }
        val cartSumWithDiscount = getCartSumWithDiscount(items, discountSize)
        val deliveryCost = getDeliveryCost(
            freeDeliveryThreshold = deliveryFreeThreshold,
            deliveryBasePrice = deliveryBasePrice,
            totalCartSumWithDiscount = cartSumWithDiscount
        )
        val discountSum = totalCartSum - cartSumWithDiscount
        val totalOrderSum = cartSumWithDiscount + (deliveryCost ?: 0)
        setState {
            copy(
                totalCartSum = totalCartSum,
                containNotDiscountable = containNotDiscountable,
                discountSum = discountSum,
                totalCartSumWithDiscount = cartSumWithDiscount,
                totalOrderSum = totalOrderSum,

                )
        }
    }
}