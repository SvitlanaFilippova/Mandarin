package com.mandarinkafe.mandarin.features.orderconfirmation.presentation.ui.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun WhenInfo(
    whenCreated: String?,
    whenConfirmed: String?,
    whenCookingCompleted: String?,
    whenPacked: String?,
    whenSended: String?,
    whenDelivered: String?,
    whenPrinted: String?,
    whenClosed: String?,
) {
    whenCreated?.let { Text("Создан: $it") }
    whenConfirmed?.let { Text("Подтверждён: $it") }
    whenCookingCompleted?.let { Text("Готово: $it") }
    whenPacked?.let { Text("Упаковано: $it") }
    whenSended?.let { Text("Отправлено: $it") }
    whenDelivered?.let { Text("Доставлено: $it") }
    whenPrinted?.let { Text("Напечатано: $it") }
    whenClosed?.let { Text("Закрыто: $it") }
}