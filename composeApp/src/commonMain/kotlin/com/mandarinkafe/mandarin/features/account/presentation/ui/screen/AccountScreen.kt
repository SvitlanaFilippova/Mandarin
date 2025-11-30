package com.mandarinkafe.mandarin.features.account.presentation.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.mandarinkafe.mandarin.MR
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.features.account.presentation.ui.components.AccountActionsSection
import com.mandarinkafe.mandarin.features.account.presentation.ui.components.ActiveSessionsSection
import com.mandarinkafe.mandarin.features.account.presentation.ui.components.PersonalInfoSection
import com.mandarinkafe.mandarin.features.account.presentation.viewmodel.AccountContract.AccountEffect
import com.mandarinkafe.mandarin.features.account.presentation.viewmodel.AccountContract.AccountEvent
import com.mandarinkafe.mandarin.navigation.NavConstants
import com.mandarinkafe.mandarin.shared.presentation.viewmodel.rememberAccountViewModel
import com.mandarinkafe.mandarin.util.presentation.LocalSnackbarHostState
import com.mandarinkafe.mandarin.util.presentation.ui.components.ScreenTitleWithBackButton
import com.mandarinkafe.mandarin.util.presentation.ui.components.dialogs.RemoveConfirmationDialog
import dev.icerock.moko.resources.StringResource
import dev.icerock.moko.resources.compose.stringResource

@Composable
fun AccountScreen(
    navController: NavHostController,
) {
    val viewModel = rememberAccountViewModel()
    val state by viewModel.state.collectAsState()
    val snackbarHostState = LocalSnackbarHostState.current
    var pendingMessageRes: StringResource? by remember { mutableStateOf(null) }
    var showDeleteAccountDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.onEvent(AccountEvent.GetInitData)
    }

    Column(modifier = Modifier.fillMaxSize()) {
        ScreenTitleWithBackButton(
            name = stringResource(MR.strings.personal_account),
            onBackClick = { navController.popBackStack() }
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(Dimens.MarginSmall8)
        ) {
            with(state) {
                PersonalInfoSection(
                    phone = userInfo.phone,
                    nameQuery = userInfo.name,
                    onNameEntered = { viewModel.onEvent(AccountEvent.SetName(it)) },
                    onPhoneClick = { viewModel.onEvent(AccountEvent.OnPhoneClick) },
                    saveNameNow = { viewModel.onEvent(AccountEvent.SaveNameNow) },
                    showNameChangeButtons = showNameChangeButtons
                )

                Spacer(modifier = Modifier.size(Dimens.MarginSmall8))

                ActiveSessionsSection(
                    isLoading = isLoading,
                    sessions = sessions,
                    onRevokeSession = { viewModel.onEvent(AccountEvent.RevokeSession(it)) }
                )
            }
        }

        AccountActionsSection(
            onLogoutClick = { viewModel.onEvent(AccountEvent.Logout) },
            onDeleteAccountClick = { showDeleteAccountDialog = true }
        )
    }

    // Диалог подтверждения удаления аккаунта
    if (showDeleteAccountDialog) {
        RemoveConfirmationDialog(
            title = stringResource(MR.strings.delete_account_confirmation_title),
            text = stringResource(MR.strings.delete_account_confirmation_message),
            onConfirm = {
                showDeleteAccountDialog = false
                viewModel.onEvent(AccountEvent.ConfirmDeleteAccount)
            },
            onDismiss = {
                showDeleteAccountDialog = false
            }
        )
    }



    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is AccountEffect.ShowMessage -> {
                    pendingMessageRes = effect.message
                }

                is AccountEffect.LoggedOut -> {
                    // Переходим на экран меню после выхода
                    navController.navigate(NavConstants.MENU_SCREEN_ROUTE) {
                        popUpTo(NavConstants.MENU_SCREEN_ROUTE) { inclusive = true }
                    }
                }

                is AccountEffect.AccountDeleted -> {
                    // Переходим на экран меню после удаления аккаунта
                    navController.navigate(NavConstants.MENU_SCREEN_ROUTE) {
                        popUpTo(NavConstants.MENU_SCREEN_ROUTE) { inclusive = true }
                    }
                }
            }
        }
    }

    val pendingMessage: String? = pendingMessageRes?.let { stringResource(it) }

    LaunchedEffect(pendingMessage) {
        val msg = pendingMessage ?: return@LaunchedEffect
        snackbarHostState.showSnackbar(msg)
        pendingMessageRes = null
    }
}