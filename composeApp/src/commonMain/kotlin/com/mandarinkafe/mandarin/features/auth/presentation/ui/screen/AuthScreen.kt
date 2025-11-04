package com.mandarinkafe.mandarin.features.auth.presentation.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.mandarinkafe.mandarin.MR
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.core.presentation.theme.Typography
import com.mandarinkafe.mandarin.features.auth.presentation.ui.components.AskPhoneComponent
import com.mandarinkafe.mandarin.features.auth.presentation.ui.components.VerificationByCallDialog
import com.mandarinkafe.mandarin.features.auth.presentation.ui.components.VerificationBySmsDialog
import com.mandarinkafe.mandarin.features.auth.presentation.viewmodel.AuthContract
import com.mandarinkafe.mandarin.shared.presentation.viewmodel.SharedContract.SharedEvent
import com.mandarinkafe.mandarin.shared.presentation.viewmodel.SharedViewModel
import com.mandarinkafe.mandarin.shared.presentation.viewmodel.rememberAuthViewModel
import com.mandarinkafe.mandarin.util.presentation.LocalSnackbarHostState
import com.mandarinkafe.mandarin.util.presentation.ui.components.ScreenTitleWithBackButton
import dev.icerock.moko.resources.compose.stringResource

@Composable
fun AuthScreen(sharedViewModel: SharedViewModel, navController: NavController) {
    val viewModel = rememberAuthViewModel()
    val state by viewModel.state.collectAsState()
    val onEvent = viewModel::onEvent
    val onSharedEvent = sharedViewModel::onEvent
    val snackbarHostState = LocalSnackbarHostState.current
    var pendingMessage: String? by remember { mutableStateOf(null) }
    var showCallVerificationDialog by remember { mutableStateOf(false) }
    var showSMSVerificationDialog by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize()) {
        ScreenTitleWithBackButton(
            name = stringResource(MR.strings.auth_screen_title),
            onBackClick = { navController.popBackStack() }
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Dimens.MarginStandard16),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                modifier = Modifier.padding(vertical = Dimens.MarginStandard16),
                text = stringResource(MR.strings.why_need_auth),
                style = Typography.RegularLightTextStyle
            )

            with(state) {

                AskPhoneComponent(
                    phoneQuery = phoneQuery,
                    isPhoneValid = isPhoneValid,
                    onValueChange = { onEvent(AuthContract.AuthEvent.SetPhone(it)) },
                    onRequestAuth = {
                        onEvent(AuthContract.AuthEvent.RequestAuth)
                        showCallVerificationDialog =
                                //TODO временно для отладки СМС, потом заменить на showCallVerificationDialog
                            true
                    },
                )


                if (showCallVerificationDialog && phoneVerificationData != null) {
                    VerificationByCallDialog(
                        data = phoneVerificationData,
                        remainingTimeSeconds = remainingTimeToCall,
                        isVerified = isVerified,
                        onCallClick = {
                            onSharedEvent(
                                SharedEvent.OnPhoneClick(
                                    phoneVerificationData.phoneToCall
                                )
                            )
                        },
                        onWantSmsClick = {
                            onEvent(AuthContract.AuthEvent.AskSmsCode)
                            showCallVerificationDialog = false
                            showSMSVerificationDialog = true
                        },
                        onDismissRequest = {
                            showCallVerificationDialog = false
                        },
                        onForceRefresh = { onEvent(AuthContract.AuthEvent.ForceRefresh) },
                    )
                }

                if (showSMSVerificationDialog) {
                    VerificationBySmsDialog(
                        onDismissRequest = { showSMSVerificationDialog = false },
                        code = smsCodeQuery,
                        isError = smsCheckError,
                        userPhone = phoneQuery,
                        onCodeChange = { onEvent(AuthContract.AuthEvent.SetCodeQuery(it)) },
                        onComplete = { onEvent(AuthContract.AuthEvent.CodeEntered) },
                        onResendSms = { onEvent(AuthContract.AuthEvent.AskSmsCode) },
                        timeToResendLeft = state.remainingTimeToResendSms,
                    )
                }

                if (error != null) {
                    pendingMessage = stringResource(error.msg)
                }
            }
        }
    }

    LaunchedEffect(pendingMessage) {
        val message = pendingMessage ?: return@LaunchedEffect
        snackbarHostState.showSnackbar(
            message = message,
            duration = SnackbarDuration.Long,
            withDismissAction = true,
        )
        pendingMessage = null
    }
}
