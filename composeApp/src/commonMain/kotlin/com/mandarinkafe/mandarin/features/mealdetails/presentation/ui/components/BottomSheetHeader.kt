package com.mandarinkafe.mandarin.features.mealdetails.presentation.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import com.mandarinkafe.mandarin.MR
import com.mandarinkafe.mandarin.core.domain.models.Meal
import com.mandarinkafe.mandarin.core.presentation.theme.Colors
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.core.presentation.theme.Typography
import dev.icerock.moko.resources.compose.painterResource
import dev.icerock.moko.resources.compose.stringResource

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
                painter = painterResource(MR.images.ic_arrow_back),
                contentDescription = stringResource(MR.strings.back),
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
                    strokeWidth = Dimens.ProgressBarStroke6,
                    trackColor = Colors.DarkGrey
                )
            }
        } else {
            FavoriteButtonForMealDetails(
                isFavorite = isFavorite,
                onClick = onToggleFavorite
            )
        }
    }
}