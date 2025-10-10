package com.mandarinkafe.mandarin.features.order.domain.models

enum class CreationStatus(val apiName: String) {
    SUCCESS("Success"),
    IN_PROGRESS("InProgress"),
    ERROR("Error");

    companion object {
        fun fromApiName(apiName: String): CreationStatus? {
            return entries.find { it.apiName.equals(apiName, ignoreCase = true) }
        }
    }
}