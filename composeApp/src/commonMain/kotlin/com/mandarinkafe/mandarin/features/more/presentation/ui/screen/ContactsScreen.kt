package com.mandarinkafe.mandarin.features.more.presentation.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.mandarinkafe.mandarin.MR
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.features.more.presentation.ui.components.MapWithCafePins
import com.mandarinkafe.mandarin.features.more.presentation.ui.components.OurAddressesCard
import com.mandarinkafe.mandarin.util.presentation.ui.components.InfoCard
import com.mandarinkafe.mandarin.util.presentation.ui.components.intents.MakeCall
import com.mandarinkafe.mandarin.util.presentation.ui.components.intents.OpenGeoLocation
import com.mandarinkafe.mandarin.util.presentation.ui.components.ScreenTitleWithBackButton
import dev.icerock.moko.resources.compose.painterResource
import dev.icerock.moko.resources.compose.stringResource

@Composable
fun ContactsScreen(onBackClick: () -> Unit) {
    val addresses = listOf(
        MR.strings.pickup_cafe_address,
        MR.strings.pickup_pizzeria_address
    )
    
    val phoneNumber = stringResource(MR.strings.cafe_phone_number)
    var shouldMakePhoneCall by remember { mutableStateOf(false) }
    var addressToOpen by remember { mutableStateOf<String?>(null) }
    
    if (shouldMakePhoneCall) {
        MakeCall(
            phoneNumber = phoneNumber
        )
        LaunchedEffect(Unit) {
            shouldMakePhoneCall = false
        }
    }
    
    addressToOpen?.let { address ->
        OpenGeoLocation(address = address)
        LaunchedEffect(Unit) {
            addressToOpen = null
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(Dimens.MarginSmall8),
        verticalArrangement = Arrangement.spacedBy(Dimens.MarginSmall8)
    ) {
        // Заголовок экрана
        ScreenTitleWithBackButton(
            name = stringResource(MR.strings.contacts_screen_title),
            onBackClick = { onBackClick() },
        )

        // График
        InfoCard(
            iconPainter = painterResource(MR.images.ic_clock),
            title = stringResource(MR.strings.working_hours_title),
            lines = listOf(
                stringResource(MR.strings.working_hours_value) to null
            )
        )

        // Телефон
        InfoCard(
            iconPainter = painterResource(MR.images.ic_phone),
            title = stringResource(MR.strings.phone_title),
            lines = listOf(
                phoneNumber to {
                    shouldMakePhoneCall = true
                }
            )
        )
        OurAddressesCard(
            lines = addresses.map { resId ->
                val address = stringResource(resId)
                address to {
                    addressToOpen = address
                }
            }
        )

        MapWithCafePins()
    }
}
