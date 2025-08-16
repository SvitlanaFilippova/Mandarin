package com.mandarinkafe.mandarin.features.orderinfo.presentation.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import com.mandarinkafe.mandarin.R
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.core.presentation.theme.Typography
import com.mandarinkafe.mandarin.features.orderinfo.domain.models.IncomingOrderItem

@Composable
fun OrderMealItemCard(
    modifier: Modifier = Modifier,
    item: IncomingOrderItem,
    onMealDetailsClick: () -> Unit,
) {
    val textDecoration = if (item.isDiscounted) {
        TextDecoration.LineThrough
    } else {
        null
    }
    Row(
        verticalAlignment = Alignment.Top,
        modifier = modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .clickable { onMealDetailsClick() }
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Название блюда
                Text(
                    modifier = Modifier.weight(1f),
                    text = item.name,
                    style = Typography.MealTitleStyle,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 2,
                )
                // Базовая цена
                Text(
                    text = stringResource(R.string.float_price_template, item.price),
                    style = Typography.MealSmallTextStyle,
                )
            }

            if (item.chosenModifiers.isNotEmpty()) {
                item.chosenModifiers.forEach {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            modifier = Modifier.weight(1f),
                            text = "${it.groupName}: ${it.name}",
                            style = Typography.MealSmallTextStyle,
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 1,
                        )

                        Text(
                            text = stringResource(R.string.float_price_template, it.price),
                            style = Typography.MealSmallTextStyle,
                        )
                    }
                }
            }

            if (item.chosenAdds.isNotEmpty()) {
                item.chosenAdds.forEach {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            modifier = Modifier.weight(1f),
                            text = "+ ${it.name}",
                            style = Typography.MealSmallTextStyle,
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 1,
                        )
                        Text(
                            text = stringResource(R.string.float_price_template, it.price),
                            style = Typography.MealSmallTextStyle,
                        )
                    }
                }
            }

            // Комментарий, если есть
            if (item.comment.isNotEmpty()) {
                Text(
                    text = stringResource(R.string.comment_template, item.comment),
                    style = Typography.MealSmallTextStyle,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 3,
                )
            }

            // Строка с итоговой ценой
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = Dimens.MarginSmall8)
            ) {
                Text(
                    text = stringResource(
                        R.string.quantity_x_template,
                        item.amount
                    ),
                    style = Typography.MealPriceStyle,
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = stringResource(
                        R.string.float_price_template,
                        item.totalPrice * item.amount
                    ),
                    style = Typography.RegularLightTextStyle,
                    textDecoration = textDecoration
                )
                if (item.isDiscounted) {
                    Spacer(modifier = Modifier.width(Dimens.MarginSmall8))
                    Text(
                        text = stringResource(
                            R.string.float_price_template,
                            item.totalDiscountedPrice
                        ),
                        style = Typography.RegularLightTextStyle,
                    )
                }
            }
        }
    }
}
