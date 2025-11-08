package com.mandarinkafe.mandarin.features.more.presentation.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.mandarinkafe.mandarin.MR
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.features.more.presentation.ui.components.FeedbackDialog
import com.mandarinkafe.mandarin.features.more.presentation.ui.components.MandarinDeliveryInfo
import com.mandarinkafe.mandarin.features.more.presentation.ui.components.MenuItem
import com.mandarinkafe.mandarin.navigation.extensions.navigateOrdersHistory
import com.mandarinkafe.mandarin.navigation.extensions.navigateToAboutScreen
import com.mandarinkafe.mandarin.navigation.extensions.navigateToAccountScreen
import com.mandarinkafe.mandarin.navigation.extensions.navigateToContactsScreen
import com.mandarinkafe.mandarin.navigation.extensions.navigateToDeliveryScreen
import com.mandarinkafe.mandarin.navigation.extensions.navigateToLegalScreen
import com.mandarinkafe.mandarin.navigation.extensions.navigateToSavedAddresses
import dev.icerock.moko.resources.compose.stringResource

@Composable
fun MoreMenuScreen(navController: NavController) {
    var showDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(vertical = Dimens.MarginStandard16)
    ) {
        MandarinDeliveryInfo()
        Spacer(modifier = Modifier.height(Dimens.MarginStandard16))

        MenuItem(
            title = stringResource(MR.strings.personal_account),
            iconRes = MR.images.ic_account_circle,
            onClick = {
                navController.navigateToAccountScreen()
            }
        )

        MenuItem(
            title = stringResource(MR.strings.more_orders_history),
            iconRes = MR.images.ic_history,
            onClick = {
                navController.navigateOrdersHistory()
            }
        )

        MenuItem(
            title = stringResource(MR.strings.more_saved_addresses),
            iconRes = MR.images.ic_cottage,
            onClick = {
                navController.navigateToSavedAddresses()
            }
        )

        MenuItem(
            title = stringResource(MR.strings.more_delivery_info),
            iconRes = MR.images.ic_courier,
            onClick = {
                navController.navigateToDeliveryScreen()
            }
        )

        MenuItem(
            title = stringResource(MR.strings.more_contacts),
            iconRes = MR.images.ic_selfpickup,
            onClick = {
                navController.navigateToContactsScreen()
            }
        )

        MenuItem(
            title = stringResource(MR.strings.more_message_manager),
            iconRes = MR.images.ic_email,
            onClick = {
                showDialog = true
            }
        )

        MenuItem(
            title = stringResource(MR.strings.more_section_legal_info),
            iconRes = MR.images.ic_legal,
            onClick = {
                navController.navigateToLegalScreen()
            }
        )

        MenuItem(
            title = stringResource(MR.strings.about_title),
            iconRes = MR.images.ic_info,
            onClick = {
                navController.navigateToAboutScreen()
            }
        )
    }


    if (showDialog) {
        FeedbackDialog(
            onDismissRequest = { showDialog = false },
        )
    }
}