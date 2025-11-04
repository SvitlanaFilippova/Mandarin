package com.mandarinkafe.mandarin.features.auth.presentation.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
    val boxSize = Dimens.CodeBoxSize
    val boxSpacing = Dimens.MarginSmall8

    LaunchedEffect(code) {
        if (code.length == length) {
            onComplete(code)
            focusManager.clearFocus()
        }
    }
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        // Основной скрытый текстфилд
        BasicTextField(
            value = code,
            onValueChange = { new ->
                val digitsOnly = new.filter { it.isDigit() }.take(length)
                onCodeChange(digitsOnly)
            },
            modifier = Modifier
                .focusRequester(focusRequester)
                .width((boxSize + boxSpacing) * length - boxSpacing)
                .height(boxSize)
                .alpha(0f), // полностью скрыт
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number
            ),
            singleLine = true
        )

        // Рисуем 4 "ячейки" поверх скрытого поля
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
                        .size(boxSize)
                        .border(
                            width = 1.dp,
                            color = boxBorderColor,
                            shape = RoundedCornerShape(Dimens.CornerRadius8)
                        )
                ) {
                    Text(
                        text = char,
                        style = Typography.RegularTextStyle
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
