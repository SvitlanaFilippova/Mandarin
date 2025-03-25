package com.mandarinkafe.mandarin.core.ui.theme

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight

object Typography {
    val ToCartButtonStyle = TextStyle(
        fontSize = Dimens.TextSizeRegular16,
        fontWeight = FontWeight.Medium,
        color = Colors.White
    )

    val ToCartButtonsBackgroundStyle = ToCartButtonStyle.copy(
        color = Colors.GreyTransparent10
    )

    val RegularTextStyle = TextStyle(
        fontSize = Dimens.TextSizeRegular16,
        fontWeight = FontWeight.Normal,
        color = Colors.White
    )

    val MealSmallTextStyle = TextStyle(
        fontSize = Dimens.TextSizeSmall11,
        fontWeight = FontWeight.Normal,
        color = Colors.Grey
    )

    val CartButtonSmallTextStyle = TextStyle(
        fontSize = Dimens.TextSizeSmall11,
        fontWeight = FontWeight.Medium,
        color = Colors.White
    )

    val MealTitleStyle = TextStyle(
        fontSize = Dimens.TextSize16,
        color = Colors.White
    )

    val MealPriceStyle = TextStyle(
        fontSize = Dimens.TextSize16,
        color = Colors.White
    )

    val PlaceholderButtonStyle = TextStyle(
        fontSize = Dimens.TextSizeRegular16,
        fontWeight = FontWeight.Medium,
        color = Colors.White
    )

    val CustomTabTextStyle = TextStyle(
        fontSize = Dimens.TextSizeRegular14,
        fontWeight = FontWeight.Bold,
        color = Colors.White
    )
}