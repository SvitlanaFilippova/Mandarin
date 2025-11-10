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
 * когда TextField использует KeyboardType.Number.
 *
 * iOS автоматически определяет OTP поля и предлагает коды из SMS,
 * если SMS содержит правильный формат.
 *
 * Требования к SMS для автозаполнения на iOS:
 * 1. SMS должна содержать 6-значный код
 * 2. РЕКОМЕНДУЕТСЯ указать домен приложения в формате:
 *    - "Ваш код для mandarinkafe.com: 123456" или
 *    - "@mandarinkafe.com #123456"
 * 3. (Опционально) В Info.plist можно добавить Associated Domains:
 *    com.apple.developer.associated-domains с webcredentials:mandarinkafe.com
 *
 * Compose Multiplatform автоматически обрабатывает textContentType(.oneTimeCode)
 * через KeyboardType.Number, поэтому явная настройка не требуется.
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









