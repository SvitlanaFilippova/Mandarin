package com.mandarinkafe.mandarin.features.auth.presentation.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import com.mandarinkafe.mandarin.MR
import com.mandarinkafe.mandarin.core.presentation.theme.Colors
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.core.presentation.theme.Typography
import com.mandarinkafe.mandarin.util.Constants.SMS_CODE_DEBOUNCE_DELAY_MS
import com.mandarinkafe.mandarin.util.Constants.SMS_CODE_LENGTH
import com.mandarinkafe.mandarin.util.formatPhoneNumberForUi
import com.mandarinkafe.mandarin.util.presentation.ui.components.MyCircularProgressIndicator
import com.mandarinkafe.mandarin.util.presentation.ui.components.buttons.ButtonWithText
import com.mandarinkafe.mandarin.util.presentation.ui.components.dialogs.DialogContainer
import com.mandarinkafe.mandarin.util.toTimeFormat
import dev.icerock.moko.resources.StringResource
import dev.icerock.moko.resources.compose.stringResource
import kotlin.time.ExperimentalTime

@Composable
fun VerificationBySmsDialog(
    onDismissRequest: () -> Unit,
    code: String,
    isError: Boolean,
    isLoading: Boolean,
    userPhone: String,
    timeToResendLeft: Int?,
    onCodeChange: (String) -> Unit,
    onComplete: (String) -> Unit,
    onResendSms: () -> Unit,
    errorRes: StringResource?,
) {
    // Дебаунс для onComplete: защита от множественных быстро последовательных вызовов
    val lastCompleteAt = remember { mutableStateOf(0L) }

    @OptIn(ExperimentalTime::class)
    val debouncedOnComplete = remember(onComplete) {
        { codeToComplete: String ->
            val now = kotlin.time.Clock.System.now().toEpochMilliseconds()
            if (now - lastCompleteAt.value > SMS_CODE_DEBOUNCE_DELAY_MS) {
                lastCompleteAt.value = now
                onComplete(codeToComplete)
            }
        }
    }

    // Автоматическое получение SMS-кодов
    rememberSmsRetriever(
        enabled = code.isEmpty(), // Включаем только если код еще не введен
        onCodeReceived = { receivedCode: String ->
            onCodeChange(receivedCode)
            debouncedOnComplete(receivedCode)
        }
    )

    DialogContainer(
        dismissOnClickOutside = false,
        onDismissRequest = onDismissRequest,
    ) {
        Text(
            text = stringResource(MR.strings.enter_code_from_sms),
            style = Typography.RegularTextStyle
        )
        Spacer(modifier = Modifier.height(Dimens.MarginSmall8))

        if (isLoading) {
            MyCircularProgressIndicator()
        } else {
            Text(
                text = stringResource(MR.strings.code_sent_to_number),
                style = Typography.SmallTextStyle
            )
            Text(
                text = userPhone.formatPhoneNumberForUi(),
                style = Typography.SmallTextStyle
            )
            Spacer(modifier = Modifier.height(Dimens.MarginSmall8))

            errorRes?.let {
                Text(
                    text = stringResource(errorRes),
                    style = Typography.ErrorTextStyle
                )
            }
            SmsCodeInput(
                code = code,
                onCodeChange = onCodeChange,
                isError = isError,
                onComplete = debouncedOnComplete
            )

            Spacer(modifier = Modifier.height(Dimens.MarginStandard16))

            timeToResendLeft?.let {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    text = stringResource(
                        MR.strings.resend_sms_timer,
                        timeToResendLeft.toTimeFormat()
                    ),
                    style = Typography.SmallTextStyle
                )
            }

            if (timeToResendLeft == 0 || timeToResendLeft == null) {
                Text(
                    modifier = Modifier.clickable(onClick = onResendSms).fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    text = stringResource(MR.strings.resend_sms_ask),
                    style = Typography.SmallTextStyle,
                    textDecoration = TextDecoration.Underline,
                    color = Colors.Orange,
                )
            }

            Spacer(modifier = Modifier.height(Dimens.MarginStandard16))

            ButtonWithText(
                modifier = Modifier.fillMaxWidth(),
                shouldBeActive = code.length == SMS_CODE_LENGTH && !isLoading,
                text = stringResource(MR.strings.send),
                onClick = { debouncedOnComplete(code) },
                onMissingRequiredInfo = {},
            )
        }
    }
}
