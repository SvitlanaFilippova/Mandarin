package com.mandarinkafe.mandarin.features.more.presentation.ui.screen

import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Phone
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.core.net.toUri
import com.mandarinkafe.mandarin.R
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.util.presentation.ui.components.InfoCard
import com.mandarinkafe.mandarin.util.presentation.ui.components.ScreenTitleWithBackButton

@Composable
fun ContactsScreen(onBackClick: () -> Boolean) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(Dimens.MarginSmall8),
        verticalArrangement = Arrangement.spacedBy(Dimens.MarginSmall8)
    ) {
        // Заголовок экрана
        ScreenTitleWithBackButton(
            name = stringResource(R.string.contacts_screen_title),
            onBackClick = { onBackClick() },
        )

        // График
        InfoCard(
            iconPainter = painterResource(R.drawable.ic_clock),
            title = stringResource(R.string.working_hours_title),
            lines = listOf(
                stringResource(R.string.working_hours_value) to null
            )
        )

        // Телефон
        InfoCard(
            iconVector = Icons.Default.Phone,
            title = stringResource(R.string.phone_title),
            lines = listOf(
                stringResource(R.string.cafe_phone_number) to {
                    val intent = Intent(
                        Intent.ACTION_DIAL,
                        "tel:${context.getString(R.string.cafe_phone_number)}".toUri()
                    )
                    context.startActivity(intent)
                }
            )
        )

        // Адреса
        InfoCard(
            iconVector = Icons.Default.LocationOn,
            title = stringResource(R.string.addresses_title),
            lines = listOf(
                stringResource(R.string.pickup_cafe_address) to {
                    val intent = Intent(
                        Intent.ACTION_VIEW,
                        "geo:0,0?q=${context.getString(R.string.pickup_cafe_address)}".toUri()
                    )
                    context.startActivity(intent)
                },
                stringResource(R.string.pickup_pizzeria_address) to {
                    val intent = Intent(
                        Intent.ACTION_VIEW,
                        "geo:0,0?q=${context.getString(R.string.pickup_pizzeria_address)}".toUri()
                    )
                    context.startActivity(intent)
                }
            )
        )
    }
}