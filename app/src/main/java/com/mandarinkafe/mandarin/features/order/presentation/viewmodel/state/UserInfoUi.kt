package com.mandarinkafe.mandarin.features.order.presentation.viewmodel.state

import com.mandarinkafe.mandarin.util.Constants.VALID_PHONE_LENGTH

data class UserInfoUi(
    val name: String = "",
    val phone: String = ""
) {
    val phoneIsValid: Boolean
        get() = phone.length == VALID_PHONE_LENGTH
}