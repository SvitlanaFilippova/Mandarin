package com.mandarinkafe.mandarin.features.address.presentation.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.mandarinkafe.mandarin.MR
import com.mandarinkafe.mandarin.core.domain.models.Address
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.util.presentation.ui.components.ScreenTitleWithBackButton
import dev.icerock.moko.resources.compose.stringResource

@Composable
fun AddressMapScreen(
    navController: NavController,
    initAddress: Address?,
    returnToRoute: String
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(Dimens.MarginSmall8)
    ) {


        ScreenTitleWithBackButton(
            name = stringResource(MR.strings.address_screen_title),
            onBackClick = { navController.popBackStack() }
        )

        Box(
            modifier = Modifier.fillMaxSize().padding(horizontal = Dimens.MarginStandard16),
            contentAlignment = Alignment.Center
        ) {
            Text("Тут будет карта, когда починю её. Наверно. Надеюсь.")
        }

//        AddressMapContentScreen(
//            navController = navController,
//            initAddress = initAddress,
//            returnToRoute = returnToRoute
//        )
    }
}

