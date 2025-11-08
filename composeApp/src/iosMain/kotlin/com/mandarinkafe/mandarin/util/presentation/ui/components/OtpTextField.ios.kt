package com.mandarinkafe.mandarin.util.presentation.ui.components

import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction

/**
 * iOS реализация OTP текстового поля
 *
 * На iOS автозаполнение SMS-кодов работает автоматически через систему,
 * если TextField использует KeyboardType.Number или KeyboardType.NumberPassword.
 *
 * iOS автоматически определяет OTP поля и предлагает коды из SMS,
 * особенно если SMS содержит правильный формат.
 *
 * Для лучшей работы автозаполнения на iOS:
 * 1. SMS должна содержать 6-значный код
 * 2. Желательно, чтобы SMS содержала домен приложения в формате: @yourdomain.com #123456
 * 3. В Info.plist можно добавить Associated Domains для webcredentials
 */
@Composable
actual fun OtpTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier,
    keyboardOptions: KeyboardOptions,
    singleLine: Boolean,
) {
    // На iOS используем стандартный BasicTextField
    // UIKit автоматически распознает поля для OTP кодов
    // и предлагает автозаполнение из SMS
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        keyboardOptions = keyboardOptions.copy(
            imeAction = ImeAction.Done
        ),
        singleLine = singleLine
    )
}






