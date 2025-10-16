package com.mandarinkafe.mandarin.features.search.presentation

import com.mandarinkafe.mandarin.core.domain.models.Label
import com.mandarinkafe.mandarin.core.presentation.theme.Colors
import com.mandarinkafe.mandarin.features.search.presentation.model.LabelUiModel
import com.mandarinkafe.mandarin.util.Constants

object SearchMapper {
    fun Label.toUiModel(): LabelUiModel {
        val backgroundColor = when (name.lowercase()) {
            Constants.VEG_LABEL_DEFAULT.lowercase() -> Colors.LabelVegGreen
            Constants.NEW_LABEL_DEFAULT.lowercase() -> Colors.LabelNewBlue
            Constants.HOT_LABEL_DEFAULT.lowercase() -> Colors.LabelHotRed
            Constants.HIT_LABEL_DEFAULT.lowercase() -> Colors.LabelHitOrange
            Constants.SALE_LABEL_DEFAULT.lowercase() -> Colors.LabelSaleRed
            else -> Colors.LabelDefault
        }

        return LabelUiModel(
            name = name,
            backgroundColor = backgroundColor
        )
    }
}





