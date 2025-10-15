package com.mandarinkafe.mandarin.features.order.presentation.viewmodel.state

import androidx.compose.runtime.Stable
import com.mandarinkafe.mandarin.features.order.domain.models.PaymentType
import com.mandarinkafe.mandarin.features.order.presentation.models.UiPaymentType

@Stable
data class PaymentInfo(
    val availablePaymentTypes: List<PaymentType> = emptyList(),
    val chosenPaymentType: UiPaymentType? = null,
    val noChange: Boolean = false,
    val changeFrom: String = ""
) {
    val paymentTypeIsChosen: Boolean
        get() = chosenPaymentType != null

    val chosenPaymentTypeDomain: PaymentType
        get() = availablePaymentTypes.first { it.code == chosenPaymentType?.code }
}
