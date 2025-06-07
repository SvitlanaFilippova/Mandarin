package com.mandarinkafe.mandarin.features.delivery.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.mandarinkafe.mandarin.R
import com.mandarinkafe.mandarin.core.presentation.theme.Colors
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.core.presentation.theme.Typography
import com.mandarinkafe.mandarin.features.delivery.components.LocationPicker

@Preview
@Composable
fun DeliveryScreen() {
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Colors.AppBlack),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .weight(1f)
                .padding(horizontal = Dimens.MarginStandard16)
        ) {
            Text(
                text = context.getString(R.string.delivery_price),
                fontSize = Dimens.TextSizeRegular16,
                color = Colors.White,
                modifier = Modifier.padding(
                    top = Dimens.MarginBig24,
                    bottom = Dimens.MarginSuperSmall4
                ),
                style = Typography.MenuCategoryStyle
            )
            Text(
                text = context.getString(R.string.delivery_price_info),
                fontSize = Dimens.TextSizeRegular14,
                color = Colors.White,
                modifier = Modifier.padding(vertical = Dimens.MarginSuperSmall4)
            )
            Text(
                text = context.getString(R.string.delivery_price_info_other),
                fontSize = Dimens.TextSizeSmall12,
                color = Colors.White,
                modifier = Modifier.padding(vertical = Dimens.MarginSuperSmall4)
            )
            Text(
                text = context.getString(R.string.pickup),
                fontSize = Dimens.TextSizeRegular16,
                color = Colors.White,
                modifier = Modifier.padding(vertical = Dimens.MarginSuperSmall4),
                style = Typography.MenuCategoryStyle
            )
            Text(
                text = context.getString(R.string.pickup_info),
                fontSize = Dimens.TextSizeRegular14,
                color = Colors.White,
                modifier = Modifier.padding(vertical = Dimens.MarginSuperSmall4)
            )
            Text(
                text = context.getString(R.string.pay),
                fontSize = Dimens.TextSizeRegular16,
                color = Colors.White,
                modifier = Modifier.padding(vertical = Dimens.MarginSuperSmall4),
                style = Typography.MenuCategoryStyle
            )
            Text(
                text = context.getString(R.string.pay_info),
                fontSize = Dimens.TextSizeRegular14,
                color = Colors.White,
                modifier = Modifier.padding(vertical = Dimens.MarginSuperSmall4)
            )
        }
        Box(
            modifier = Modifier
                .weight(1f)
        ) {
            LocationPicker()
        }
    }
}