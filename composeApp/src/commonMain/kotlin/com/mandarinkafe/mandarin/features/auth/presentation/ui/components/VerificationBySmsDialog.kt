package com.mandarinkafe.mandarin.features.auth.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Dialog
import com.mandarinkafe.mandarin.MR
import com.mandarinkafe.mandarin.core.presentation.theme.Colors
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.core.presentation.theme.Typography
import com.mandarinkafe.mandarin.util.presentation.ui.components.buttons.ButtonWithText
import dev.icerock.moko.resources.compose.stringResource

@Composable
fun VerificationBySmsDialog(
    onDismissRequest: () -> Unit,
    code: String,
    onCodeChange: (String) -> Unit,
    onComplete: (String) -> Unit,
    onCantGetSmsClick: () -> Unit,
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

            SmsCodeInput(
                code = code,
                onCodeChange = onCodeChange,
                onComplete = onComplete
            )

            ButtonWithText(
                modifier = Modifier.width(Dimens.ButtonPlaceholderSize200)
                    .padding(vertical = Dimens.MarginSmall8),
                text = stringResource(MR.strings.cant_get_sms),
                onClick = onCantGetSmsClick
            )
        }
    }
}