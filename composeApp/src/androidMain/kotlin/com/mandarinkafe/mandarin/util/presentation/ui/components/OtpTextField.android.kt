package com.mandarinkafe.mandarin.util.presentation.ui.components

import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * Android реализация OTP текстового поля
 * Использует стандартный BasicTextField
 */
@Composable
actual fun OtpTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier,
    keyboardOptions: KeyboardOptions,
    singleLine: Boolean,
) {
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        keyboardOptions = keyboardOptions,
        singleLine = singleLine
    )
}




