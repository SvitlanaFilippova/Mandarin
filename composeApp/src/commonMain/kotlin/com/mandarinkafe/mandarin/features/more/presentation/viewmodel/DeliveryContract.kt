package com.mandarinkafe.mandarin.features.more.presentation.viewmodel

import com.mandarinkafe.mandarin.features.address.presentation.ui.models.UiDeliveryArea
import com.mandarinkafe.mandarin.util.presentation.BaseContract
import com.mandarinkafe.mandarin.util.ConstantsMap.MANDARIN_CENTER_LATITUDE
import com.mandarinkafe.mandarin.util.ConstantsMap.MANDARIN_CENTER_LONGITUDE

sealed interface DeliveryContract {
    sealed interface DeliveryEvent : BaseContract.BaseEvent {
        data class CameraMoved(val center: Any) : DeliveryEvent
    }

    sealed interface DeliveryEffect : BaseContract.BaseEffect

    data class DeliveryState(
        val isLoading: Boolean = true,
        val initPinPoint: Any = createDefaultPoint(), // TODO: Create expect/actual function
        val deliveryAreas: List<UiDeliveryArea> = listOf(),
        val currentPinPoint: Any? = null,
        val displayAddress: String? = null,
        val deliveryArea: UiDeliveryArea? = null,
        val error: String? = null,
        val fetchAddressInProgress: Boolean = false,

        ) : BaseContract.BaseState {
        val locationChosen: Boolean
            get() = displayAddress?.isNotEmpty() == true && error == null && !fetchAddressInProgress
    }
}

// TODO: Move to expect/actual
private fun createDefaultPoint(): Any {
    // This will be replaced with expect/actual implementation
    return Any()
}
