package com.mandarinkafe.mandarin.features.auth.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.window.Dialog
import com.mandarinkafe.mandarin.MR
import com.mandarinkafe.mandarin.core.presentation.theme.Colors
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.core.presentation.theme.Typography
import com.mandarinkafe.mandarin.core.presentation.theme.Typography.ButtonTextStyle
import com.mandarinkafe.mandarin.features.auth.presentation.models.PhoneVerificationDataUi
import com.mandarinkafe.mandarin.util.formatPhoneNumberForUi
import com.mandarinkafe.mandarin.util.presentation.ui.components.buttons.ButtonWithText
import com.mandarinkafe.mandarin.util.toTimeFormat
import dev.icerock.moko.resources.compose.painterResource
import dev.icerock.moko.resources.compose.stringResource
import dev.materii.pullrefresh.PullRefreshIndicator
import dev.materii.pullrefresh.PullRefreshLayout
import dev.materii.pullrefresh.rememberPullRefreshState

@Composable
fun VerificationByCallDialog(
    data: PhoneVerificationDataUi,
    remainingTimeSeconds: Int?,
    onCallClick: () -> Unit,
    onWantSmsClick: () -> Unit,
    onDismissRequest: () -> Unit,
    onForceRefresh: () -> Unit,
    isVerified: Boolean,
    isLoading: Boolean = false, //TODO
) {

    val pullRefreshState = rememberPullRefreshState(
        refreshing = isLoading,
        onRefresh = onForceRefresh
    )

    Dialog(
        onDismissRequest = onDismissRequest
    ) {
        PullRefreshLayout(
            modifier = Modifier.fillMaxSize(),
            state = pullRefreshState,
            indicator = {
                PullRefreshIndicator(
                    state = pullRefreshState,
                    contentColor = Colors.Orange,
                )
            }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Colors.DarkGrey)
                    .padding(Dimens.MarginBig32),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (isVerified) {
                    SuccessAuth()
                } else {
                    Text(
                        text = stringResource(
                            MR.strings.verification_by_phone_instruction, data.userPhone,
                            data.phoneToCall.formatPhoneNumberForUi()
                        ),
                        style = Typography.RegularLightTextStyle
                    )

                    // Таймер обратного отсчёта
                    Spacer(modifier = Modifier.padding(top = Dimens.MarginStandard16))

                    remainingTimeSeconds?.let {
                        Text(
                            text = remainingTimeSeconds.toTimeFormat(),
                            style = Typography.RegularLightTextStyle.copy(
                                color = if (remainingTimeSeconds < 60) Colors.Red else Colors.White,
                                fontSize = Typography.TitleStyle.fontSize
                            ),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.padding(top = Dimens.MarginSmall8))
                    }

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
    }
}

