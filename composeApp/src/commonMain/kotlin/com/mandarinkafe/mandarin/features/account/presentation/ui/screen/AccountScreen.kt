package com.mandarinkafe.mandarin.features.account.presentation.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.mandarinkafe.mandarin.MR
import com.mandarinkafe.mandarin.core.presentation.theme.Colors
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.core.presentation.theme.Typography
import com.mandarinkafe.mandarin.features.account.presentation.ui.components.ActiveSessionsSection
import com.mandarinkafe.mandarin.features.account.presentation.ui.components.PersonalInfoSection
import com.mandarinkafe.mandarin.features.account.presentation.viewmodel.AccountContract.AccountEffect
import com.mandarinkafe.mandarin.features.account.presentation.viewmodel.AccountContract.AccountEvent
import com.mandarinkafe.mandarin.navigation.NavConstants
import com.mandarinkafe.mandarin.shared.presentation.viewmodel.rememberAccountViewModel
import com.mandarinkafe.mandarin.util.presentation.LocalSnackbarHostState
import com.mandarinkafe.mandarin.util.presentation.ui.components.ScreenTitleWithBackButton
import dev.icerock.moko.resources.compose.painterResource
import dev.icerock.moko.resources.compose.stringResource

@Composable
fun AccountScreen(navController: NavHostController) {
    val viewModel = rememberAccountViewModel()
    val state by viewModel.state.collectAsState()
    val snackbarHostState = LocalSnackbarHostState.current

    Column(modifier = Modifier.fillMaxSize()) {
        ScreenTitleWithBackButton(
            name = stringResource(MR.strings.personal_account),
            onBackClick = { navController.popBackStack() }
        )

        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(Dimens.MarginSmall8)
        ) {

            with(state) {
                PersonalInfoSection(
                    phone = userInfo.phone,
                    nameQuery = userInfo.name,
                    onNameEntered = { viewModel.onEvent(AccountEvent.SetName(it)) },
                )

                Spacer(modifier = Modifier.size(Dimens.MarginStandard16))

                ActiveSessionsSection(
                    isLoading = isLoading,
                    sessions = sessions,
                    onRevokeSession = { viewModel.onEvent(AccountEvent.RevokeSession(it)) }
                )
            }
        }

        // Отступ, чтобы прижать кнопку "Выйти из аккаунта" к нижней части экрана
        Spacer(modifier = Modifier.weight(1f))

        // Кнопка "Выйти из аккаунта"
        TextButton(
            onClick = { viewModel.onEvent(AccountEvent.Logout) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = Dimens.MarginStandard16)
        ) {
            Icon(
                modifier = Modifier
                    .size(Dimens.IconSize24)
                    .padding(end = Dimens.MarginSmall8),
                painter = painterResource(MR.images.ic_logout),
                contentDescription = null,
                tint = Colors.Red
            )
            Text(
                text = stringResource(MR.strings.logout_from_account),
                style = Typography.RegularTextStyle,
                color = Colors.Red
            )
        }
    }

    LaunchedEffect(viewModel.effect) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is AccountEffect.ShowError -> {
                    snackbarHostState.showSnackbar(effect.message)
                }

                is AccountEffect.SessionRevoked -> {
                    snackbarHostState.showSnackbar("Сессия завершена")
                }

                is AccountEffect.LoggedOut -> {
                    // Переходим на экран меню после выхода
                    navController.navigate(NavConstants.MENU_SCREEN_ROUTE) {
                        popUpTo(NavConstants.MENU_SCREEN_ROUTE) { inclusive = true }
                    }
                }
            }
        }
    }
}