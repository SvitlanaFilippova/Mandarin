package com.mandarinkafe.mandarin.features.auth.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Dialog
import com.mandarinkafe.mandarin.MR
import com.mandarinkafe.mandarin.core.presentation.theme.Colors
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.core.presentation.theme.Typography
import com.mandarinkafe.mandarin.core.presentation.theme.Typography.ButtonTextStyle
import com.mandarinkafe.mandarin.features.auth.presentation.models.PhoneVerificationDataUi
import com.mandarinkafe.mandarin.util.formatPhoneNumberForUi
import com.mandarinkafe.mandarin.util.presentation.ui.components.buttons.ButtonWithText
import dev.icerock.moko.resources.compose.painterResource
import dev.icerock.moko.resources.compose.stringResource

@Composable
fun VerificationByCallDialog(
    data: PhoneVerificationDataUi,
    onCallClick: () -> Unit,
    onWantSmsClick: () -> Unit,
    onDismissRequest: () -> Unit,
) {
    Dialog(
        onDismissRequest = onDismissRequest
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Colors.DarkGrey)
                .padding(Dimens.MarginBig32),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(
                    MR.strings.verification_by_phone_instruction, data.userPhone,
                    data.phoneToCall.formatPhoneNumberForUi()
                ),
                style = Typography.RegularLightTextStyle
            )

            // Кнопка "Позвонить"
            Button(
                onClick = onCallClick,
                shape = RoundedCornerShape(Dimens.CornerRadius8),
                modifier = Modifier.width(Dimens.ButtonPlaceholderSize200)
                    .padding(vertical = Dimens.MarginStandard16),
                colors = ButtonDefaults.buttonColors(
                    contentColor = Colors.White,
                    containerColor = Colors.Orange
                )
            ) {
                Icon(
                    painter = painterResource(MR.images.ic_call),
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(Dimens.MarginSmall8))
                Text(
                    text = stringResource(MR.strings.placeholder_call),
                    style = ButtonTextStyle
                )
            }

            // Кнопка "Лучше SMS"
            ButtonWithText(
                modifier = Modifier.width(Dimens.ButtonPlaceholderSize200),
                text = stringResource(MR.strings.want_sms),
                onClick = onWantSmsClick
            )
        }
    }
}

