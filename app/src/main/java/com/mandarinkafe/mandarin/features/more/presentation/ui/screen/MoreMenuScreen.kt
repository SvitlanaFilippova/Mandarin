package com.mandarinkafe.mandarin.features.more.presentation.ui.screen

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import com.mandarinkafe.mandarin.R
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

@Composable
fun MoreMenuScreen(navController: NavHostController) {
    val ordersHistory = stringResource(id = R.string.more_orders_history)
    val savedAddresses = stringResource(id = R.string.more_saved_addresses)
    val sectionLegal = stringResource(id = R.string.more_section_legal_info)
    val aboutTitle = stringResource(id = R.string.about_title)
    val contactsTitle = stringResource(id = R.string.more_contacts)
    val delivery = stringResource(id = R.string.more_delivery_info)
    val messageForManager = stringResource(id = R.string.more_message_manager)
    var showDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = Dimens.MarginStandard16)
    ) {
        item {
            MandarinDeliveryInfo()
        }

        item {
            MenuItem(title = ordersHistory, iconRes = R.drawable.ic_history, onClick = {
                navController.navigateOrdersHistory()
            })
        }

        item {
            MenuItem(title = savedAddresses, iconRes = R.drawable.ic_cottage, onClick = {
                navController.navigateToSavedAddresses()
            })
        }

        item {
            MenuItem(title = delivery, iconRes = R.drawable.ic_courier, onClick = {
                navController.navigateToDeliveryScreen()
            })
        }

        item {
            MenuItem(title = contactsTitle, iconRes = R.drawable.ic_selfpickup, onClick = {
                navController.navigateToContactsScreen()
            })
        }

        item {
            MenuItem(title = messageForManager, iconRes = R.drawable.ic_email, onClick = {
                showDialog = true
            })
        }

        item {
            MenuItem(title = sectionLegal, iconRes = R.drawable.ic_legal, onClick = {
                navController.navigateToLegalScreen()
            })
        }

        item {
            MenuItem(title = aboutTitle, iconRes = R.drawable.ic_info, onClick = {
                navController.navigateToAboutScreen()
            })
        }
    }



    if (showDialog) {
        FeedbackDialog(
            onDismissRequest = { showDialog = false },
        )
    }
}