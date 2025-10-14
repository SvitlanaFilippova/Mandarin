package com.mandarinkafe.mandarin.features.address.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.mandarinkafe.mandarin.R
import com.mandarinkafe.mandarin.core.presentation.theme.Colors
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.core.presentation.theme.Typography
import com.mandarinkafe.mandarin.features.address.domain.models.AddressSearchResult
import com.mandarinkafe.mandarin.util.presentation.ui.components.MyCircularProgressIndicator

@Composable
fun SearchByTextResults(
    modifier: Modifier = Modifier,
    isLoading: Boolean,
    data: List<AddressSearchResult>,
    searchError: String?,
    onItemClick: (AddressSearchResult) -> Unit,
    onDismiss: () -> Unit,
) {
    LazyColumn(
        modifier = modifier
            .fillMaxWidth()
            .background(Colors.DarkGrey)
            .padding(Dimens.MarginSmall8)
    ) {
        item {
            if (isLoading || searchError != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .size(Dimens.MarginForCartButton72),
                    contentAlignment = Alignment.Center
                ) {
                    if (isLoading) {
                        MyCircularProgressIndicator(
                            modifier = Modifier
                                .padding(Dimens.MarginStandard16),
                            strokeWidth = Dimens.ProgressBarStroke6,
                        )
                    }

                    if (searchError != null) {
                        Text(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(Dimens.MarginStandard16),
                            text = searchError,
                            style = Typography.RegularLightTextStyle
                        )
                    }
                }
            }
        }

        items(data) {
            AddressSearchResultItem(
                text = it.addressLineOne,
                extraText = it.addressLineTwo,
                onClick = { onItemClick(it) }
            )
        }

        item {
            IconButton(
                modifier = Modifier
                    .fillMaxWidth(),
                onClick = onDismiss,
            ) {
                Icon(
                    modifier = Modifier
                        .size(Dimens.IconSize24),
                    imageVector = Icons.Default.KeyboardArrowUp,
                    contentDescription = stringResource(R.string.cancel),
                    tint = Colors.LightGrey
                )
            }
        }
    }
}