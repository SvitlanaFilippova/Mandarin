package com.mandarinkafe.mandarin.core.ui.theme

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight

object Typography {
    val ToCartButtonStyle = TextStyle(
        fontSize = Dimens.TextSizeSmall12,
        fontWeight = FontWeight.Normal,
        color = Colors.White
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
        fontSize = Dimens.TextSizeRegular16,
        color = Colors.White
    )
    val MenuCategoryStyle = TextStyle(
        fontSize = Dimens.TextSizeBig24,
        color = Colors.White,
        fontWeight = FontWeight(800)
    )
    val MenuSubCategoryStyle = TextStyle(
        fontSize = Dimens.TextSizeBig20,
        color = Colors.White,
        fontWeight = FontWeight(800)
    )

    val MealPriceStyle = TextStyle(
        fontSize = Dimens.TextSizeRegular16,
        color = Colors.White
    )

    val PlaceholderButtonStyle = TextStyle(
        fontSize = Dimens.TextSizeRegular16,
        fontWeight = FontWeight.Medium,
        color = Colors.White
    )
}