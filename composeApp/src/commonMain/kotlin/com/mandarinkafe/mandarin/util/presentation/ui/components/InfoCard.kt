package com.mandarinkafe.mandarin.util.presentation.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import com.mandarinkafe.mandarin.core.presentation.theme.Colors
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens

@Composable
fun InfoCard(
    modifier: Modifier = Modifier,
    iconVector: ImageVector? = null,
    iconPainter: Painter? = null,
    title: String,
    lines: List<Pair<String, (() -> Unit)?>> = emptyList(),
    backgroundColor: Color = Colors.DarkGrey,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Row(
            modifier = Modifier
                .padding(Dimens.MarginStandard16)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            when {
                iconVector != null -> Icon(
                    imageVector = iconVector,
                    contentDescription = title,
                    tint = Colors.WhiteTransparent75
                )

                iconPainter != null -> Icon(
                    painter = iconPainter,
                    contentDescription = title,
                    tint = Colors.WhiteTransparent75
                )
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = Dimens.MarginStandard16),
                verticalArrangement = Arrangement.spacedBy(Dimens.MarginSuperSmall4)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium
                )
                lines.forEach { (text, onClick) ->
                    if (onClick != null) {
                        Text(
                            text = text,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.clickable { onClick() }
                        )
                    } else {
                        Text(
                            text = text,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}


