package com.mandarinkafe.mandarin.features.auth.presentation.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import com.mandarinkafe.mandarin.features.auth.data.sms.getSmsRetriever
import kotlinx.coroutines.flow.collectLatest

/**
 * Composable функция для автоматического получения SMS-кодов
 *
 * @param onCodeReceived Callback, вызываемый при получении кода
 * @param enabled Флаг, определяющий, должно ли быть включено прослушивание SMS
 */
@Composable
fun rememberSmsRetriever(
    enabled: Boolean = true,
    onCodeReceived: (String) -> Unit,
) {
    val smsRetriever = remember { getSmsRetriever() }

    LaunchedEffect(enabled) {
        if (enabled) {
            smsRetriever.startListening().collectLatest { code ->
                code?.let { onCodeReceived(it) }
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            smsRetriever.stopListening()
        }
    }
}


