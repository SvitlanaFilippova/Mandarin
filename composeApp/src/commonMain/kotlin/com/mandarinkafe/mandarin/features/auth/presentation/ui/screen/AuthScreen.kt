package com.mandarinkafe.mandarin.features.auth.presentation.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
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
import com.mandarinkafe.mandarin.core.presentation.models.UiError
import com.mandarinkafe.mandarin.core.presentation.theme.Colors
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.core.presentation.theme.Typography
import com.mandarinkafe.mandarin.features.auth.presentation.ui.components.AskPhoneComponent
import com.mandarinkafe.mandarin.features.auth.presentation.ui.components.HandleEffects
import com.mandarinkafe.mandarin.features.auth.presentation.ui.components.SuccessAuthDialog
import com.mandarinkafe.mandarin.features.auth.presentation.ui.components.VerificationByCallDialog
import com.mandarinkafe.mandarin.features.auth.presentation.ui.components.VerificationBySmsDialog
import com.mandarinkafe.mandarin.features.auth.presentation.viewmodel.AuthContract
import com.mandarinkafe.mandarin.shared.presentation.viewmodel.SharedContract.SharedEvent
import com.mandarinkafe.mandarin.shared.presentation.viewmodel.SharedViewModel
import com.mandarinkafe.mandarin.shared.presentation.viewmodel.rememberAuthViewModel
import com.mandarinkafe.mandarin.util.presentation.LocalSnackbarHostState
import com.mandarinkafe.mandarin.util.presentation.ui.components.ScreenTitleWithBackButton
import com.mandarinkafe.mandarin.util.toTimeFormat
import dev.icerock.moko.resources.compose.stringResource
import dev.materii.pullrefresh.PullRefreshIndicator
import dev.materii.pullrefresh.PullRefreshLayout
import dev.materii.pullrefresh.rememberPullRefreshState

@Composable
fun AuthScreen(
    sharedViewModel: SharedViewModel,
    navController: NavController,
    targetRoute: String?,
    initialPhone: String? = null,
    forDeleteAccount: Boolean = false,
) {
    val viewModel = rememberAuthViewModel()
    val state by viewModel.state.collectAsState()
    val onEvent = viewModel::onEvent
    val onSharedEvent = sharedViewModel::onEvent
    val snackbarHostState = LocalSnackbarHostState.current
    var pendingMessage: String? by remember { mutableStateOf(null) }
    var showCallVerificationDialog by remember { mutableStateOf(false) }
    var showSMSVerificationDialog by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }
    var requestAlreadyActiveSeconds: Int? by remember { mutableStateOf(null) }

    InitializePhone(initialPhone, state.phoneQuery, onEvent)

    val pullRefreshState = rememberPullRefreshState(
        refreshing = state.isLoading,
        onRefresh = { onEvent(AuthContract.AuthEvent.ForceRefresh) },
    )

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
        AuthScreenContent(
            state = state,
            navController = navController,
            onEvent = onEvent,
            onSharedEvent = onSharedEvent,
            showCallVerificationDialog = showCallVerificationDialog,
            showSMSVerificationDialog = showSMSVerificationDialog,
            showSuccessDialog = showSuccessDialog,
            onShowCallVerificationDialogChange = { showCallVerificationDialog = it },
            onShowSMSVerificationDialogChange = { showSMSVerificationDialog = it },
            onShowSuccessDialogChange = { showSuccessDialog = it },
            onPendingMessageChange = { pendingMessage = it },
            requestAlreadyActiveSeconds = requestAlreadyActiveSeconds,
            onRequestAlreadyActiveSecondsChange = { requestAlreadyActiveSeconds = it },
        )

        HandlePendingMessage(pendingMessage, snackbarHostState) {
            pendingMessage = null
        }
    }

    HandleEffects(
        effectFlow = viewModel.effect,
        navController = navController,
        targetRoute = targetRoute,
        forDeleteAccount = forDeleteAccount,
        showSuccessDialog = {
            showSuccessDialog = it
            showSMSVerificationDialog = false
            showCallVerificationDialog = false
        }
    )
}

@Composable
private fun InitializePhone(
    initialPhone: String?,
    currentPhoneQuery: String,
    onEvent: (AuthContract.AuthEvent) -> Unit,
) {
    LaunchedEffect(initialPhone) {
        initialPhone?.let { phone ->
            if (currentPhoneQuery.isEmpty()) {
                onEvent(AuthContract.AuthEvent.SetPhone(phone))
            }
        }
    }
}

@Composable
private fun AuthScreenContent(
    state: AuthContract.AuthState,
    navController: NavController,
    onEvent: (AuthContract.AuthEvent) -> Unit,
    onSharedEvent: (SharedEvent) -> Unit,
    showCallVerificationDialog: Boolean,
    showSMSVerificationDialog: Boolean,
    showSuccessDialog: Boolean,
    onShowCallVerificationDialogChange: (Boolean) -> Unit,
    onShowSMSVerificationDialogChange: (Boolean) -> Unit,
    onShowSuccessDialogChange: (Boolean) -> Unit,
    onPendingMessageChange: (String?) -> Unit,
    requestAlreadyActiveSeconds: Int?,
    onRequestAlreadyActiveSecondsChange: (Int?) -> Unit,
) {
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
                        onShowCallVerificationDialogChange(true)
                    },
                )

                AuthDialogs(
                    state = state,
                    showCallVerificationDialog = showCallVerificationDialog,
                    showSMSVerificationDialog = showSMSVerificationDialog,
                    showSuccessDialog = showSuccessDialog,
                    onEvent = onEvent,
                    onSharedEvent = onSharedEvent,
                    onShowCallVerificationDialogChange = onShowCallVerificationDialogChange,
                    onShowSMSVerificationDialogChange = onShowSMSVerificationDialogChange,
                    onShowSuccessDialogChange = onShowSuccessDialogChange,
                )

                AuthErrorHandler(
                    error = error,
                    requestAlreadyActiveSeconds = requestAlreadyActiveSeconds,
                    onPendingMessageChange = onPendingMessageChange,
                    onRequestAlreadyActiveSecondsChange = onRequestAlreadyActiveSecondsChange,
                )
            }
        }
    }
}

@Composable
private fun AuthDialogs(
    state: AuthContract.AuthState,
    showCallVerificationDialog: Boolean,
    showSMSVerificationDialog: Boolean,
    showSuccessDialog: Boolean,
    onEvent: (AuthContract.AuthEvent) -> Unit,
    onSharedEvent: (SharedEvent) -> Unit,
    onShowCallVerificationDialogChange: (Boolean) -> Unit,
    onShowSMSVerificationDialogChange: (Boolean) -> Unit,
    onShowSuccessDialogChange: (Boolean) -> Unit,
) {
    if (showCallVerificationDialog && state.phoneVerificationData != null) {
        VerificationByCallDialog(
            data = state.phoneVerificationData,
            remainingTimeSeconds = state.remainingTimeToCall,
            onCallClick = {
                onSharedEvent(
                    SharedEvent.OnPhoneClick(
                        state.phoneVerificationData.phoneToCall
                    )
                )
            },
            onWantSmsClick = {
                onEvent(AuthContract.AuthEvent.AskSmsCode)
                onShowCallVerificationDialogChange(false)
                onShowSMSVerificationDialogChange(true)
            },
            onDismissRequest = {
                onShowCallVerificationDialogChange(false)
            },
        )
    }

    if (showSMSVerificationDialog) {
        VerificationBySmsDialog(
            onDismissRequest = { onShowSMSVerificationDialogChange(false) },
            code = state.smsCodeQuery,
            isError = state.smsCheckError,
            userPhone = state.phoneQuery,
            onCodeChange = { onEvent(AuthContract.AuthEvent.SetCodeQuery(it)) },
            onComplete = { onEvent(AuthContract.AuthEvent.CodeEntered) },
            onResendSms = { onEvent(AuthContract.AuthEvent.AskSmsCode) },
            timeToResendLeft = state.remainingTimeToResendSms,
            errorRes = state.smsValidationError,
            isLoading = state.isLoading,
        )
    }

    if (showSuccessDialog) {
        SuccessAuthDialog(
            onDismissRequest = { onShowSuccessDialogChange(false) },
        )
    }
}

@Composable
private fun AuthErrorHandler(
    error: UiError?,
    requestAlreadyActiveSeconds: Int?,
    onPendingMessageChange: (String?) -> Unit,
    onRequestAlreadyActiveSecondsChange: (Int?) -> Unit,
) {
    if (error != null) {
        onPendingMessageChange(stringResource(error.msg))
    }

    requestAlreadyActiveSeconds?.let { seconds ->
        onPendingMessageChange(
            stringResource(
                MR.strings.request_auth_already_active,
                seconds.toTimeFormat()
            )
        )
        onRequestAlreadyActiveSecondsChange(null)
    }
}

@Composable
private fun HandlePendingMessage(
    pendingMessage: String?,
    snackbarHostState: SnackbarHostState,
    onMessageHandled: () -> Unit,
) {
    LaunchedEffect(pendingMessage) {
        val message = pendingMessage ?: return@LaunchedEffect
        snackbarHostState.showSnackbar(
            message = message,
            duration = SnackbarDuration.Short,
            withDismissAction = true,
        )
        onMessageHandled()
    }
}
