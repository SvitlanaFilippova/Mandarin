package com.mandarinkafe.mandarin.features.address.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.mandarinkafe.mandarin.MR
import com.mandarinkafe.mandarin.core.presentation.theme.Colors
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.core.presentation.theme.Typography
import com.mandarinkafe.mandarin.features.address.domain.models.AddressSearchResult
import com.mandarinkafe.mandarin.features.map.MapCameraController
import com.mandarinkafe.mandarin.util.ConstantsMap.MAP_DEFAULT_ZOOM_FOR_ADDRESS_SCREEN
import com.mandarinkafe.mandarin.util.presentation.ui.components.MyCircularProgressIndicator
import dev.icerock.moko.resources.StringResource
import dev.icerock.moko.resources.compose.painterResource
import dev.icerock.moko.resources.compose.stringResource

@Composable
fun SearchByTextResults(
    modifier: Modifier = Modifier,
    isLoading: Boolean,
    data: List<AddressSearchResult>,
    searchError: StringResource?,
    onItemClick: (AddressSearchResult) -> Unit,
    onDismiss: () -> Unit,
    cameraController: MapCameraController? = null,
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
                        .size(Dimens.AddressSearchResultsHeight),
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
                            text = stringResource(searchError),
                            style = Typography.RegularLightTextStyle
                        )
                    }
                }
            }
        }

        items(data) { searchResult ->
            AddressSearchResultItem(
                text = searchResult.addressLineOne,
                extraText = searchResult.addressLineTwo,
                onClick = {
                    // Перемещаем камеру к выбранному адресу, если есть координаты и контроллер
                    searchResult.point?.let { point ->
                        cameraController?.moveCamera(point, MAP_DEFAULT_ZOOM_FOR_ADDRESS_SCREEN)
                    }
                    onItemClick(searchResult)
                }
            )
        }

        item {
            IconButton(
                modifier = Modifier
                    .fillMaxWidth(),
                onClick = onDismiss,
            ) {
                Icon(
                    painter = painterResource(MR.images.ic_keyboard_arrow_up),
                    modifier = Modifier
                        .size(Dimens.IconSize24),
                    contentDescription = stringResource(MR.strings.cancel),
                    tint = Colors.LightGrey
                )
            }
        }
    }
}