package com.mandarinkafe.mandarin.features.orderconfirmation.presentation.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.mandarinkafe.mandarin.core.domain.models.Address
import com.mandarinkafe.mandarin.core.domain.models.getDetailsString
import com.mandarinkafe.mandarin.core.presentation.theme.Colors
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens

@Composable
fun CustomerInfo(
    phone: String?,
    comment: String?,
    customerName: String?,
    address: Address?
) {
    Card(colors = CardDefaults.cardColors(containerColor = Colors.DarkGrey)) {
        Column(
            Modifier.padding(Dimens.MarginStandard16),
            verticalArrangement = Arrangement.spacedBy(Dimens.MarginSuperSmall4)
        ) {
            phone?.let { LabelValue("Телефон", it) }
            customerName?.let { LabelValue("Клиент", it) }
            address?.let {
                Spacer(Modifier.height(Dimens.MarginSmall8))
                Label("Адрес")
                Value(it.streetAndBuilding)

                val details = remember { it.getDetailsString() }
                if (details.isNotEmpty()) {
                    Spacer(Modifier.height(Dimens.MarginSmall8))
                    Label("Детали")
                    Value(details)
                }
            }
            Spacer(Modifier.height(Dimens.MarginSmall8))
            comment?.let {
                Label("Комментарий к заказу")
                Value(it)
            }
        }
    }
}
