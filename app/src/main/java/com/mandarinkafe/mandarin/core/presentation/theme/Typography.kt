package com.mandarinkafe.mandarin.core.presentation.theme

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import com.mandarinkafe.mandarin.util.Constants.TITLE_FONT_WEIGHT

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
        color = Colors.WhiteTransparent75
    )

    val TitleStyle = TextStyle(
        fontSize = Dimens.TextSizeBig20,
        fontWeight = FontWeight(TITLE_FONT_WEIGHT),
        color = Colors.White
    )

    val SmallTextStyle = TextStyle(
        fontSize = Dimens.TextSizeSmall12,
        fontWeight = FontWeight.Normal,
        color = Colors.WhiteTransparent75
    )

    val ConsentSmallTextStyle = TextStyle(
        fontSize = Dimens.TextSizeSmall11,
        fontWeight = FontWeight.Light,
        color = Colors.WhiteTransparent75
    )

    val ExtraSmallTextStyle = TextStyle(
        fontSize = Dimens.TextSizeSuperSmall10,
        fontWeight = FontWeight.Light,
        color = Colors.White
    )

    val MealLabelTextStyle = TextStyle(
        fontSize = Dimens.TextSizeRegular14,
        fontWeight = FontWeight.Medium,
        color = Colors.White
    )

    val MealLabelBigTextStyle = TextStyle(
        fontSize = Dimens.TextSizeRegular16,
        fontWeight = FontWeight.Medium,
        color = Colors.White
    )

    val MealLabelSmallTextStyle = TextStyle(
        fontSize = Dimens.TextSizeSuperSmall10,
        fontWeight = FontWeight.Light,
        color = Colors.White
    )

    val MealSmallTextStyle = TextStyle(
        fontSize = Dimens.TextSizeSmall12,
        fontWeight = FontWeight.Light,
        color = Colors.LightGrey
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
        fontWeight = FontWeight(TITLE_FONT_WEIGHT)
    )
    val MenuSubCategoryStyle = TitleStyle

    val MealPriceStyle = TextStyle(
        fontSize = Dimens.TextSizeRegular16,
        color = Colors.White
    )

    val PlaceholderTitleStyle = TextStyle(
        fontSize = Dimens.TextSizeBig20,
        fontWeight = FontWeight.Medium,
        color = Colors.White
    )

    val ErrorTextStyle = TextStyle(
        fontSize = Dimens.TextSizeSmall12,
        fontWeight = FontWeight.Light,
        color = Colors.ErrorRed
    )
}