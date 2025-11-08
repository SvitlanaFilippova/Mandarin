package com.mandarinkafe.mandarin.util.presentation.ui.components

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * Expect функция для создания текстового поля с поддержкой автозаполнения OTP-кодов
 * На iOS использует UITextContentType.oneTimeCode
 * На Android работает стандартно
 */
@Composable
expect fun OtpTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    singleLine: Boolean = true,
)






