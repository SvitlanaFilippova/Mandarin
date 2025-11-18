package com.mandarinkafe.mandarin.features.order.presentation.viewmodel.state

import androidx.compose.runtime.Stable
import com.mandarinkafe.mandarin.features.order.domain.models.PaymentType
import com.mandarinkafe.mandarin.features.order.presentation.models.UiPaymentType
import com.mandarinkafe.mandarin.features.order.presentation.models.toDomain

@Stable
data class PaymentInfo(
    val availablePaymentTypes: List<PaymentType> = emptyList(),
    val chosenPaymentType: UiPaymentType? = null,
    val noChange: Boolean = false,
    val changeFrom: String = "",
) {
    val paymentTypeIsChosen: Boolean
        get() = chosenPaymentType != null

    val chosenPaymentTypeDomain: PaymentType
        get() {
            val chosenCode = chosenPaymentType?.code
            return if (chosenCode != null) {
                // Ищем тип оплаты в доступных типах по коду
                availablePaymentTypes.firstOrNull { it.code.equals(chosenCode, ignoreCase = true) }
                    // Если не найден в списке доступных, создаем из UiPaymentType
                    ?: chosenPaymentType.toDomain()
            } else {
                // Если тип не выбран, возвращаем первый доступный (fallback)
                // Это не должно происходить, так как paymentTypeIsChosen проверяет null
                availablePaymentTypes.firstOrNull()
                    ?: error("No payment types available")
            }
        }
}

