package com.mandarinkafe.mandarin.core.ui.theme

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight

object Typography {
    val ToCartButtonStyle = TextStyle(
        fontSize = Dimens.TextSizeSmall12,
        fontWeight = FontWeight.Normal,
        color = Colors.White
    )

    val ToCartButtonBigStyle = TextStyle(
        fontSize = Dimens.TextSizeRegular16,
        fontWeight = FontWeight.Normal,
        color = Colors.White
    )

    val RegularTextStyle = TextStyle(
        fontSize = Dimens.TextSizeRegular16,
        fontWeight = FontWeight.Normal,
        color = Colors.White
    )

    val RegularLightTextStyle = TextStyle(
        fontSize = Dimens.TextSizeRegular14,
        fontWeight = FontWeight.Light,
        color = Colors.Grey
    )

    val TitleStyle = TextStyle(
        fontSize = Dimens.TextSizeBig20,
        fontWeight = FontWeight(800),
        color = Colors.White
    )

    val SmallTextStyle = TextStyle(
        fontSize = Dimens.TextSizeSmall12,
        fontWeight = FontWeight.Normal,
        color = Colors.White
    )

    val MealSmallTextStyle = TextStyle(
        fontSize = Dimens.TextSizeSmall11,
        fontWeight = FontWeight.Light,
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
    val MenuSubCategoryStyle = TitleStyle

    val MealPriceStyle = TextStyle(
        fontSize = Dimens.TextSizeRegular16,
        color = Colors.White
    )
}