package com.mandarinkafe.mandarin.features.order.presentation.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.mandarinkafe.mandarin.R
import com.mandarinkafe.mandarin.core.domain.models.Address
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.features.address.savedadresses.presentation.ui.components.SavedAddressCard
import com.mandarinkafe.mandarin.features.order.presentation.viewmodel.OrderContract.OrderEvent
import com.mandarinkafe.mandarin.util.Constants.DEFAULT_SAVED_ADDRESSES_NUMBER
import com.mandarinkafe.mandarin.util.presentation.ui.components.MyClickableText
import com.mandarinkafe.mandarin.util.presentation.ui.components.TooltipText

@Composable
fun SavedAddressesSection(
    allSavedAddresses: List<Address>,
    selectedAddress: Address?,
    onEvent: (OrderEvent) -> Unit,
    onDeleteRequest: (String) -> Unit,
    showAllAddresses: Boolean,
    onToggleShowAll: () -> Unit,
    visible: Boolean
) {
    val addressesToShow = if (showAllAddresses) {
        allSavedAddresses
    } else {
        allSavedAddresses.take(
            DEFAULT_SAVED_ADDRESSES_NUMBER
        )
    }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn() + expandVertically(),
        exit = fadeOut() + shrinkVertically()
    ) {
        Column {
            if (addressesToShow.isNotEmpty()) {
                addressesToShow.forEach { item ->
                    SavedAddressCard(
                        address = item,
                        onAddressChosen = { onEvent(OrderEvent.SetAddress(item)) },
                        onEditAddress = { onEvent(OrderEvent.EditAddress(item)) },
                        selected = item == selectedAddress,
                        onRemoveAddress = { onDeleteRequest(item.id) },
                    )
                }
                // Кнопка "Показать ещё" или "Скрыть"
                if (allSavedAddresses.size > DEFAULT_SAVED_ADDRESSES_NUMBER) {
                    MyClickableText(
                        onClick = onToggleShowAll,
                        text = if (showAllAddresses) {
                            stringResource(R.string.addresses_hide)
                        } else {
                            stringResource(
                                R.string.addresses_show_more,
                                allSavedAddresses.size - DEFAULT_SAVED_ADDRESSES_NUMBER
                            )
                        }
                    )
                }

            } else {
                TooltipText(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            horizontal = Dimens.MarginSmall8,
                            vertical = Dimens.MarginStandard16
                        ),
                    textRes = R.string.no_saved_addresses
                )
            }

            MyClickableText(
                textRes = R.string.add_address,
                onClick = { onEvent(OrderEvent.AddNewAddress) }
            )
        }
    }
}