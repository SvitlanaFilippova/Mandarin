package com.mandarinkafe.mandarin.features.orderconfirmation.presentation.ui.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun CustomerInfo(phone: String?, comment: String?, customerName: String?) {
    phone?.let { Text("Телефон: $it") }
    comment?.let { Text("Комментарий: $it") }
    customerName?.let { Text("Клиент: $it") }
}