package com.mandarinkafe.mandarin.features.more.presentation.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextDecoration
import com.mandarinkafe.mandarin.MR
import com.mandarinkafe.mandarin.core.presentation.theme.Colors
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import dev.icerock.moko.resources.compose.painterResource
import dev.icerock.moko.resources.compose.stringResource

@Composable
fun OurAddressesCard(
    modifier: Modifier = Modifier,
    lines: List<Pair<String, () -> Unit>>,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Colors.DarkGrey),
    ) {
        Column(
            modifier = Modifier.padding(Dimens.MarginStandard16),
            verticalArrangement = Arrangement.spacedBy(Dimens.MarginSuperSmall4)
        ) {
            // Заголовок
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                  painter = painterResource(MR.images.ic_location_on),
                    contentDescription = null,
                    tint = Colors.Transparent // псевдоиконка для отступа
                )
                Text(
                    modifier = Modifier.padding(start = Dimens.MarginStandard16),
                    text = stringResource(MR.strings.addresses_title),
                    style = MaterialTheme.typography.titleMedium
                )
            }

            // Список адресов
            lines.forEachIndexed { index, (text, onClick) ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(MR.images.ic_location_on),
                        contentDescription = text,
                        tint = Colors.WhiteTransparent75
                    )
                    Text(
                        text = text,
                        style = MaterialTheme.typography.bodyMedium,
                        textDecoration = TextDecoration.Underline,
                        modifier = Modifier
                            .padding(start = Dimens.MarginStandard16)
                            .clickable { onClick() }
                    )
                }
                // Отступ между элементами
                if (index < lines.lastIndex) {
                    Spacer(modifier = Modifier.height(Dimens.MarginSmall8))
                }
            }
        }
    }
}