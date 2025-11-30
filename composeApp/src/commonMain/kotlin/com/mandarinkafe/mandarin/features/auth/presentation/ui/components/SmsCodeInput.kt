package com.mandarinkafe.mandarin.features.auth.presentation.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.mandarinkafe.mandarin.core.presentation.theme.Colors
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.core.presentation.theme.Typography
import com.mandarinkafe.mandarin.util.Constants.SMS_CODE_LENGTH
import com.mandarinkafe.mandarin.util.presentation.ui.components.OtpTextField
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.ExperimentalTime

@Composable
fun SmsCodeInput(
    code: String,
    isError: Boolean,
    onCodeChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    onComplete: (String) -> Unit,
) {
    val length = SMS_CODE_LENGTH
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    val boxWidth = Dimens.CodeBoxWidth
    val boxHeight = Dimens.CodeBoxHeight
    val boxSpacing = Dimens.MarginSuperSmall4
    val lastCompleteAt = remember { mutableStateOf(0L) }

    @OptIn(ExperimentalTime::class)
    LaunchedEffect(code) {
        if (code.length == length) {
            val now = kotlin.time.Clock.System.now().toEpochMilliseconds()
            // Дебаунс: защита от множественных быстро последовательных вызовов
            if (now - lastCompleteAt.value > 800) {
                lastCompleteAt.value = now
                val c = code
                // Запускаем onComplete асинхронно и даём время на завершение автозаполнения/печати
                // чтобы избежать гонок с нативным кодом
                onComplete(c)
                // Отложенное снятие фокуса
                launch {
                    delay(250)
                    focusManager.clearFocus()
                }
            }
        }
    }
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        // Основной скрытый текстфилд с поддержкой автозаполнения OTP
        OtpTextField(
            value = code,
            onValueChange = { new ->
                val digitsOnly = new.filter { it.isDigit() }.take(length)
                onCodeChange(digitsOnly)
            },
            modifier = Modifier
                .focusRequester(focusRequester)
                .width((boxWidth + boxSpacing) * length - boxSpacing)
                .height(boxHeight)
                .alpha(0.01f), // почти невидим, но не нулевой - iOS не будет считать поле удалённым
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number
            ),
            singleLine = true
        )

        // Рисуем 6 "ячееек" поверх скрытого поля
        Row(
            horizontalArrangement = Arrangement.spacedBy(boxSpacing),
            verticalAlignment = Alignment.CenterVertically
        ) {
            for (i in 0 until length) {
                val char = code.getOrNull(i)?.toString() ?: ""
                val isActive = code.length == i
                val boxBorderColor = when {
                    isError -> Colors.Red
                    isActive -> Colors.Orange
                    else -> Colors.LightGrey
                }

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .height(boxHeight)
                        .width(boxWidth)
                        .border(
                            width = 1.dp,
                            color = boxBorderColor,
                            shape = RoundedCornerShape(Dimens.CornerRadius8)
                        )
                ) {
                    Text(
                        text = char,
                        style = Typography.SmsCodeInputStyle
                    )
                }
            }
        }
    }

    // Автоматически фокусируем при появлении
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
}
