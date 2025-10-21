package com.mandarinkafe.mandarin.features.more.presentation.ui.screen

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.mandarinkafe.mandarin.MR
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.features.more.presentation.ui.components.FeedbackDialog
import com.mandarinkafe.mandarin.features.more.presentation.ui.components.MandarinDeliveryInfo
import com.mandarinkafe.mandarin.features.more.presentation.ui.components.MenuItem
import com.mandarinkafe.mandarin.navigation.extensions.navigateOrdersHistory
import com.mandarinkafe.mandarin.navigation.extensions.navigateToAboutScreen
import com.mandarinkafe.mandarin.navigation.extensions.navigateToContactsScreen
import com.mandarinkafe.mandarin.navigation.extensions.navigateToDeliveryScreen
import com.mandarinkafe.mandarin.navigation.extensions.navigateToLegalScreen
import com.mandarinkafe.mandarin.navigation.extensions.navigateToSavedAddresses
import dev.icerock.moko.resources.compose.stringResource
import moe.tlaster.precompose.navigation.Navigator

@Composable
fun MoreMenuScreen(navigator: Navigator) {
    val ordersHistory = stringResource(MR.strings.more_orders_history)
    val savedAddresses = stringResource(MR.strings.more_saved_addresses)
    val sectionLegal = stringResource(MR.strings.more_section_legal_info)
    val aboutTitle = stringResource(MR.strings.about_title)
    val contactsTitle = stringResource(MR.strings.more_contacts)
    val delivery = stringResource(MR.strings.more_delivery_info)
    val messageForManager = stringResource(MR.strings.more_message_manager)
    var showDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = Dimens.MarginStandard16)
    ) {
        item {
            MandarinDeliveryInfo()
            Spacer(modifier = Modifier.height(Dimens.MarginStandard16))
        }

        item {
            MenuItem(
                title = ordersHistory,
                iconRes = MR.images.ic_history,
                onClick = {
                    navigator.navigateOrdersHistory()
                })
        }

        item {
            MenuItem(
                title = savedAddresses,
                iconRes = MR.images.ic_cottage,
                onClick = {
                    navigator.navigateToSavedAddresses()
                })
        }

        item {
            MenuItem(
                title = delivery,
                iconRes = MR.images.ic_courier,
                onClick = {
                    navigator.navigateToDeliveryScreen()
                })
        }

        item {
            MenuItem(
                title = contactsTitle,
                iconRes = MR.images.ic_selfpickup,
                onClick = {
                    navigator.navigateToContactsScreen()
                })
        }

        item {
            MenuItem(
                title = messageForManager,
                iconRes = MR.images.ic_email,
                onClick = {
                    showDialog = true
                })
        }

        item {
            MenuItem(
                title = sectionLegal,
                iconRes = MR.images.ic_legal,
                onClick = {
                    navigator.navigateToLegalScreen()
                })
        }

        item {
            MenuItem(
                title = aboutTitle,
                iconRes = MR.images.ic_info,
                onClick = {
                    navigator.navigateToAboutScreen()
                })
        }
    }

    if (showDialog) {
        FeedbackDialog(
            onDismissRequest = { showDialog = false },
        )
    }
}