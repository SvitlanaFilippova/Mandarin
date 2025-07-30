package com.mandarinkafe.mandarin.features.order.presentation.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import com.mandarinkafe.mandarin.R
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.navigation.extensions.navigateToMenu
import com.mandarinkafe.mandarin.util.presentation.ui.components.buttons.ButtonWithText

@Composable
fun OrderConfirmationScreen(orderID: String, navController: NavHostController) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                modifier = Modifier.padding(Dimens.MarginStandard16),
                text = stringResource(R.string.order_submit_success, orderID)
            )
            ButtonWithText(
                textResID = R.string.back_to_menu,
                onClick = { navController.navigateToMenu() }
            )
        }
    }
}