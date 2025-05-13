package com.mandarinkafe.mandarin.placeholder.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import com.mandarinkafe.mandarin.R
import com.mandarinkafe.mandarin.core.ui.theme.Colors
import com.mandarinkafe.mandarin.core.ui.theme.Dimens
import com.mandarinkafe.mandarin.core.ui.theme.Typography
import com.mandarinkafe.mandarin.features.menu.ui.view_model.MenuContract.Event
import com.mandarinkafe.mandarin.placeholder.ui.view_model.PlaceholderViewModel
import com.mandarinkafe.mandarin.util.applyTypography

@Composable
fun PlaceholderScreen(
    errorMessage: String,
    onEvent: (Event) -> Unit,
    viewModel: PlaceholderViewModel = hiltViewModel()
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(Dimens.MarginStandard16)
            .background(Colors.Transparent),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(R.drawable.logo_orange),
            contentDescription = stringResource(R.string.logo_cafe),
            modifier = Modifier
                .width(Dimens.PlaceholderImageSize120)
                .padding(bottom = Dimens.MarginStandard16)
        )

        Text(
            text = stringResource(id = R.string.error_something_wrong).applyTypography(),
            style = Typography.RegularTextStyle,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(Dimens.MarginSmall8)
        )

        Text(
            text = errorMessage.applyTypography(),
            style = Typography.RegularTextStyle,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(Dimens.MarginStandard16)
        )

        Text(
            text = stringResource(
                R.string.placeholder_message_call_us,
                stringResource(R.string.cafe_phone_number)
            ).applyTypography(),
            style = Typography.RegularTextStyle,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(Dimens.MarginBig24)
        )

        val buttonWidth = Dimens.ButtonPlaceholderSize200

        Box(modifier = Modifier.width(buttonWidth)) {
            Button(
                onClick = { onEvent(Event.ForceRefreshMenu) },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    contentColor = Colors.White,
                    containerColor = Colors.Orange
                )
            ) {
                Text(text = stringResource(id = R.string.placeholder_retry))
            }
        }

        Spacer(modifier = Modifier.height(Dimens.MarginStandard16))

        Box(modifier = Modifier.width(buttonWidth)) {
            Button(
                onClick = { onEvent(Event.OnPhoneClick) },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    contentColor = Colors.White,
                    containerColor = Colors.Orange
                )
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_call),
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(Dimens.MarginSmall8))
                Text(text = stringResource(id = R.string.placeholder_call))
            }
        }
    }
}
