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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import com.mandarinkafe.mandarin.R
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.core.presentation.theme.Typography
import com.mandarinkafe.mandarin.features.orderinfo.domain.models.IncomingMealAdditional
import com.mandarinkafe.mandarin.features.orderinfo.domain.models.IncomingModifier
import com.mandarinkafe.mandarin.features.orderinfo.domain.models.IncomingOrderItem

@Composable
fun OrderMealItemCard(
    modifier: Modifier = Modifier,
    item: IncomingOrderItem,
    onOpenMealDetails: () -> Unit,
    showNoLongerInMenuMessage: (String) -> Unit,
) {
    val discountedDecoration = remember(item) {
        if (item.isDiscounted) TextDecoration.LineThrough else null
    }
    val deletedDecoration = remember(item) {
        if (item.isDeleted) TextDecoration.LineThrough else null
    }

    val noLongerInMenuMessage = if (!item.isValidated) {
        stringResource(
            R.string.item_is_no_longer_available,
            item.name
        )
    } else {
        ""
    }

    val onMealDetailsClick = {
        if (item.isValidated) {
            onOpenMealDetails()
        } else {
            showNoLongerInMenuMessage(noLongerInMenuMessage)
        }
    }

    Row(
        verticalAlignment = Alignment.Top,
        modifier = modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .clickable { onMealDetailsClick() }
    ) {
        Column {
            MealHeader(
                name = item.name,
                price = item.price,
                deletedDecoration = deletedDecoration
            )

            ModifiersList(
                modifiers = item.chosenModifiers,
                deletedDecoration = deletedDecoration
            )

            AddsList(adds = item.chosenAdds)

            if (item.comment.isNotEmpty()) {
                MealComment(comment = item.comment)
            }

            MealPriceRow(
                item = item,
                isDeleted = item.isDeleted,
                deletedDecoration = deletedDecoration,
                discountedDecoration = discountedDecoration
            )
        }
    }
}

@Composable
private fun MealHeader(name: String, price: Double, deletedDecoration: TextDecoration?) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = Modifier.weight(1f),
            text = name,
            textDecoration = deletedDecoration,
            style = Typography.MealTitleStyle,
            overflow = TextOverflow.Ellipsis,
            maxLines = 2,
        )
        Text(
            text = stringResource(R.string.float_price_template, price),
            style = Typography.MealSmallTextStyle,
            textDecoration = deletedDecoration,
        )
    }
}

@Composable
private fun ModifiersList(
    modifiers: List<IncomingModifier>,
    deletedDecoration: TextDecoration?,
) {
    modifiers.forEach {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier.weight(1f),
                text = "${it.groupName}: ${it.name}",
                textDecoration = deletedDecoration,
                style = Typography.MealSmallTextStyle,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
            )
            Text(
                text = stringResource(R.string.float_price_template, it.price),
                style = Typography.MealSmallTextStyle,
                textDecoration = deletedDecoration,
            )
        }
    }
}

@Composable
private fun AddsList(
    adds: List<IncomingMealAdditional>,
) {
    adds.forEach {
        AddItemRow(item = it)
    }
}

@Composable
private fun AddItemRow(item: IncomingMealAdditional) {
    val deletedDecoration = remember(item) {
        if (item.isDeleted) TextDecoration.LineThrough else null
    }
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = Modifier.weight(1f),
            text = "+ ${item.name}",
            style = Typography.MealSmallTextStyle,
            textDecoration = deletedDecoration,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1,
        )
        Text(
            text = stringResource(R.string.float_price_template, item.price),
            style = Typography.MealSmallTextStyle,
            textDecoration = deletedDecoration,
        )
    }
}

@Composable
private fun MealComment(comment: String) {
    Text(
        text = stringResource(R.string.comment_template, comment),
        style = Typography.MealSmallTextStyle,
        overflow = TextOverflow.Ellipsis,
        maxLines = 3,
    )
}

@Composable
private fun MealPriceRow(
    item: IncomingOrderItem,
    isDeleted: Boolean,
    deletedDecoration: TextDecoration?,
    discountedDecoration: TextDecoration?,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = Dimens.MarginSmall8)
    ) {
        Text(
            text = stringResource(R.string.quantity_x_template, item.amount),
            style = Typography.MealPriceStyle,
        )
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = stringResource(
                R.string.float_price_template,
                item.totalPrice
            ),
            style = Typography.RegularLightTextStyle,
            textDecoration = if (isDeleted) deletedDecoration else discountedDecoration
        )
        if (item.isDiscounted) {
            Spacer(modifier = Modifier.width(Dimens.MarginSmall8))
            Text(
                text = stringResource(
                    R.string.float_price_template,
                    item.totalDiscountedPrice
                ),
                style = Typography.RegularLightTextStyle,
                textDecoration = deletedDecoration,
            )
        }
    }
}

