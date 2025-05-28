package com.mandarinkafe.mandarin.features.meal_details.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import com.mandarinkafe.mandarin.R
import com.mandarinkafe.mandarin.core.domain.models.Meal
import com.mandarinkafe.mandarin.core.ui.theme.Colors
import com.mandarinkafe.mandarin.core.ui.theme.Dimens
import com.mandarinkafe.mandarin.core.ui.theme.Typography
import com.mandarinkafe.mandarin.util.ui.components.buttons.FavoriteButton

@Composable
fun BottomSheetHeader(
    meal: Meal,
    isFavorite: Boolean?,
    onToggleFavorite: () -> Unit,
    onClose: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = Dimens.MarginSmall8,
                end = Dimens.MarginSmall8,
                bottom = Dimens.MarginSmall8
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {

        IconButton(
            onClick = onClose,
            modifier = Modifier.size(Dimens.ButtonBox32)
        ) {

            Icon(
                modifier = Modifier.size(Dimens.IconSize24),
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = stringResource(id = R.string.back),
                tint = Colors.White
            )
        }

        Box(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = Dimens.MarginSmall8),
            contentAlignment = Alignment.Center

        ) {
            Text(
                text = meal.name,
                style = Typography.TitleStyle,
                maxLines = 31,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center
            )
        }

        if (isFavorite == null) {
            // Если инфо по наличию в избранном ещё не получено - показываем ProgressIndicator
            Box(modifier = Modifier.size(Dimens.ButtonBox32), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(
                    modifier = Modifier.size(Dimens.IconSize24),
                    color = Colors.LightGrey,
                    strokeWidth = Dimens.ProgressBarSmallWidth8,
                    trackColor = Colors.DarkGrey
                )
            }
        } else {
            FavoriteButton(
                isFavorite = isFavorite,
                onClick = onToggleFavorite
            )
        }
    }
}