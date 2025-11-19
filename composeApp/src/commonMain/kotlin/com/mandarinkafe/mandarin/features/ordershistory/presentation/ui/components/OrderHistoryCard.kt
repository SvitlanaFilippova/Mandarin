package com.mandarinkafe.mandarin.features.ordershistory.presentation.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import com.mandarinkafe.mandarin.MR
import com.mandarinkafe.mandarin.core.presentation.theme.Colors
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.core.presentation.theme.Typography
import com.mandarinkafe.mandarin.features.order.presentation.models.toUi
import com.mandarinkafe.mandarin.features.ordershistory.domain.models.SavedOrder
import com.mandarinkafe.mandarin.shared.BuildKonfig
import com.mandarinkafe.mandarin.util.presentation.ui.components.images.KamelSubcomposeAsyncImage
import dev.icerock.moko.resources.compose.stringResource

@Composable
fun OrderHistoryCard(
    modifier: Modifier = Modifier,
    order: SavedOrder,
    onClick: () -> Unit,
    onItemClick: (id: String) -> Unit,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = Colors.DarkGrey),
    ) {
        Column(modifier = Modifier.padding(Dimens.MarginStandard16)) {
            DateAndStatusSection(
                orderStatus = order.status,
                whenCreated = order.whenCreated
            )

            Spacer(modifier = Modifier.height(Dimens.MarginSmall8))

            // Тип и номер заказа
            order.orderType?.let {
                val text = if (order.number.isNotEmpty()) {
                    stringResource(it.toUi().nameRes) + " • №${order.number}"
                } else {
                    stringResource(it.toUi().nameRes)
                }
                Text(
                    text = text,
                    style = Typography.RegularTextStyle,
                )
            }

            // Адрес
            if (order.addressLine1.isNotEmpty()) {
                Text(
                    text = order.addressLine1,
                    style = Typography.RegularLightTextStyle,
                )
            }
            if (order.addressDetails.isNotEmpty()) {
                Text(
                    text = order.addressDetails,
                    style = Typography.SmallTextStyle,
                    fontWeight = FontWeight.Light,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1
                )
            }
            Spacer(modifier = Modifier.height(Dimens.MarginSmall8))


            when {
                // Изображения блюд, если есть
                order.mealIds.isNotEmpty() -> {
                    val baseUrl = BuildKonfig.SERVER_BASE_URL.removeSuffix("/")
                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(Dimens.MarginSmall8)
                    ) {
                        items(order.mealIds) { mealId ->
                            val imageUrl = "$baseUrl/images_previews/$mealId.jpg"
                            val blurredPreviewUrl =
                                "$baseUrl/images_previews/${mealId}_placeholder.jpg"
                            KamelSubcomposeAsyncImage(
                                modifier = Modifier
                                    .size(Dimens.MealSuperSmallImage)
                                    .aspectRatio(1f)
                                    .clip(RoundedCornerShape(Dimens.CornerRadius4))
                                    .clickable(onClick = { onItemClick(mealId) }),
                                model = imageUrl,
                                previewModel = blurredPreviewUrl,
                                contentDescription = "Изображение блюда",
                                placeholder = MR.images.placeholder_meal_no_photo,
                                error = MR.images.placeholder_meal_no_photo,
                                crossfade = true
                            )
                        }
                    }
                }
                // либо Блюда в заказе строкой
                order.mealNames.isNotEmpty() -> Text(
                    text = order.mealNames,
                    style = Typography.SmallTextStyle,
                    color = Colors.White,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 3
                )
            }


            Spacer(modifier = Modifier.height(Dimens.MarginSmall8))
        }
    }
}