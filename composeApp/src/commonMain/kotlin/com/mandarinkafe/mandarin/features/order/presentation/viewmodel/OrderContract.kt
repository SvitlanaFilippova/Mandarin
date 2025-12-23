package com.mandarinkafe.mandarin.features.order.presentation.viewmodel

import com.mandarinkafe.mandarin.core.domain.models.Address
import com.mandarinkafe.mandarin.core.domain.models.CartItem
import com.mandarinkafe.mandarin.features.order.domain.models.DeliveryType
import com.mandarinkafe.mandarin.features.order.domain.models.OrderPickupPoint
import com.mandarinkafe.mandarin.features.order.domain.models.PaymentType
import com.mandarinkafe.mandarin.features.order.domain.models.Utensil
import com.mandarinkafe.mandarin.features.order.presentation.models.UiPaymentType
import com.mandarinkafe.mandarin.features.order.presentation.viewmodel.state.CartSummary
import com.mandarinkafe.mandarin.features.order.presentation.viewmodel.state.DeliveryInfo
import com.mandarinkafe.mandarin.features.order.presentation.viewmodel.state.PaymentInfo
import com.mandarinkafe.mandarin.features.order.presentation.viewmodel.state.UserInfoUi
import com.mandarinkafe.mandarin.features.order.presentation.viewmodel.state.Utensils
import com.mandarinkafe.mandarin.util.Constants
import com.mandarinkafe.mandarin.util.presentation.BaseContract
import dev.icerock.moko.resources.StringResource

sealed interface OrderContract {
    sealed interface OrderEvent : BaseContract.BaseEvent {
        // Управление адресами
        data object RefreshAddresses : OrderEvent
        data object AddNewAddress : OrderEvent
        data class SetAddress(val address: Address) : OrderEvent
        data class EditAddress(val address: Address) : OrderEvent
        data class RemoveAddress(val id: String) : OrderEvent
        data class SelectAddressById(val id: String) : OrderEvent

        // Ввод персональных данных
        data class SetName(val query: String) : OrderEvent
        data class ToggleSaveUserInfo(val checked: Boolean) : OrderEvent

        // Настройки доставки
        data class SetDeliveryType(val deliveryType: DeliveryType) : OrderEvent

        // Настройки оплаты
        data object GetInitData : OrderEvent
        data class SetPaymentType(val paymentType: UiPaymentType) : OrderEvent

        // Настройки сдачи и приборов
        data class NoChangeToggled(val noChange: Boolean) : OrderEvent
        data class SetChangeFrom(val query: String) : OrderEvent
        data class SetNoNeedUtensils(val noNeedUtensils: Boolean) : OrderEvent
        data class SetChosenUtensils(val utensil: Utensil, val isChosen: Boolean) : OrderEvent

        // Комментарий к заказу
        data class SetComment(val query: String) : OrderEvent

        // Управление заказом
        data object RemovePickupOnly : OrderEvent
        data object OnMissingRequiredInfo : OrderEvent
        data object SubmitOrder : OrderEvent
        data object StopObservingStatus : OrderEvent
    }

    sealed interface OrderEffect : BaseContract.BaseEffect {
        data object AddNewAddress : OrderEffect
        data class EditAddress(val address: Address) : OrderEffect

        // Обработка отправки заказа
        data class ShowSuccess(
            val orderId: String,
            val paymentMethodCode: String? = null,
        ) :
            OrderEffect

        data class StartOnlinePayment(
            val orderId: String,
            val amount: Double,
            val userPhone: String,
            val paymentMethodCode: String? = null,
        ) : OrderEffect

        data class ShowMessage(
            val message: StringResource,
            val details: String? = null,
        ) : OrderEffect
    }

    data class OrderState(
        val userInfo: UserInfoUi = UserInfoUi(),
        val savedNameIsEmpty: Boolean = true,
        val deliveryInfo: DeliveryInfo = DeliveryInfo(),
        val paymentInfo: PaymentInfo = PaymentInfo(),
        val cartSummary: CartSummary = CartSummary(),
        val utensils: Utensils = Utensils(),
        val comment: String = "",
        val isError: Boolean = false,
        val pickupOnly: Boolean = false,
        val pickupOnlyPositions: List<CartItem> = listOf(),
        val pickupOnlyPositionsNames: List<String> = listOf(),
        val containsAlcohol: Boolean = false,
        val pickupPoint: OrderPickupPoint = OrderPickupPoint.CAFE,
        val isLoading: Boolean = false,
        val shouldSaveUserName: Boolean = false,

        ) : BaseContract.BaseState {
        val deliveryCost: Int
            get() = when {
                deliveryInfo.isPickup -> 0
                deliveryInfo.deliveryZone == null -> 0
                cartSummary.cartSumWithDiscount < deliveryInfo.deliveryZone.freeDeliveryThreshold ->
                    deliveryInfo.deliveryZone.deliveryPrice

                else -> 0
            }

        val totalOrderSum: Double
            get() = cartSummary.cartSumWithDiscount + deliveryCost.toDouble()

        /**
         * Отфильтрованный список типов оплаты.
         * Исключает онлайн-оплату для заказов на сумму меньше 1 рубля.
         */
        val filteredPaymentTypes: List<PaymentType>
            get() {
                val minAmountForOnlinePayment = 1.0
                return if (totalOrderSum < minAmountForOnlinePayment) {
                    // Исключаем онлайн-оплату для заказов меньше 1 рубля
                    paymentInfo.availablePaymentTypes.filter {
                        !it.code.equals(Constants.PAYMENT_ONLINE_CODE, ignoreCase = true)
                    }
                } else {
                    paymentInfo.availablePaymentTypes
                }
            }

        /**
         * Проверяет, что имя пользователя заполнено (не пустое после trim).
         */
        val isNameValid: Boolean
            get() = userInfo.name.trim().isNotBlank()

        val canBeSubmitted: Boolean
            get() = !isLoading &&
                    isNameValid &&
                    deliveryInfo.addressIsValid &&
                    paymentInfo.paymentTypeIsChosen
    }
}