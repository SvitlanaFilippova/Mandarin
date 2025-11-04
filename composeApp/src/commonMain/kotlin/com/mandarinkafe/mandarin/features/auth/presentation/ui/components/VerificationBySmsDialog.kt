package com.mandarinkafe.mandarin.features.auth.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.window.Dialog
import com.mandarinkafe.mandarin.MR
import com.mandarinkafe.mandarin.core.presentation.theme.Colors
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.core.presentation.theme.Typography
import com.mandarinkafe.mandarin.util.formatPhoneNumberForUi
import com.mandarinkafe.mandarin.util.toTimeFormat
import dev.icerock.moko.resources.compose.stringResource

@Composable
fun VerificationBySmsDialog(
    onDismissRequest: () -> Unit,
    code: String,
    isError: Boolean,
    userPhone: String,
    timeToResendLeft: Int?,
    onCodeChange: (String) -> Unit,
    onComplete: (String) -> Unit,
    onResendSms: () -> Unit,
) {
    Dialog(
        onDismissRequest = onDismissRequest
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Colors.DarkGrey)
                .padding(Dimens.MarginBig32),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Dimens.MarginSmall8)
        ) {
            Text(
                text = stringResource(MR.strings.enter_code_from_sms),
                style = Typography.RegularTextStyle
            )

            Spacer(modifier = Modifier.height(Dimens.MarginStandard16))
            Text(
                text = stringResource(MR.strings.code_sent_to_number),
                style = Typography.SmallTextStyle
            )
            Text(
                text = userPhone.formatPhoneNumberForUi(),
                style = Typography.SmallTextStyle
            )
            Spacer(modifier = Modifier.height(Dimens.MarginStandard16))
            SmsCodeInput(
                code = code,
                onCodeChange = onCodeChange,
                isError = isError,
                onComplete = onComplete
            )
            Spacer(modifier = Modifier.height(Dimens.MarginStandard16))

            timeToResendLeft?.let {
                Text(
                    text = stringResource(
                        MR.strings.resend_sms_timer,
                        timeToResendLeft.toTimeFormat()
                    ),
                    style = Typography.SmallTextStyle
                )
            }

            if (timeToResendLeft == 0 || timeToResendLeft == null) {
                Text(
                    text = stringResource(MR.strings.resend_sms_ask),
                    style = Typography.SmallTextStyle,
                    textDecoration = TextDecoration.Underline,
                    color = Colors.Orange,
                    modifier = Modifier.clickable(onClick = onResendSms)
                )
            }
        }
    }
}