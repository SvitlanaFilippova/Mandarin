package com.mandarinkafe.mandarin.features.discounts.data

data class LoyaltyCustomerByPhoneRequest(
    val phone: String,
    val type: String = TYPE_PHONE,
    val organizationId: String,
) {
    private companion object {
        const val TYPE_PHONE = "phone"
    }
}