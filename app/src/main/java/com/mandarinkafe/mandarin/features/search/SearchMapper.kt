package com.mandarinkafe.mandarin.features.search

import com.mandarinkafe.mandarin.core.domain.models.Label
import com.mandarinkafe.mandarin.core.ui.theme.Colors
import com.mandarinkafe.mandarin.features.search.ui.model.LabelUiModel
import com.mandarinkafe.mandarin.util.Constants.HIT_LABEL_DEFAULT
import com.mandarinkafe.mandarin.util.Constants.HOT_LABEL_DEFAULT
import com.mandarinkafe.mandarin.util.Constants.NEW_LABEL_DEFAULT
import com.mandarinkafe.mandarin.util.Constants.VEG_LABEL_DEFAULT

object SearchMapper {

    fun Label.toUiModel(): LabelUiModel {
        val backgroundColor = when (name.lowercase()) {
            VEG_LABEL_DEFAULT.lowercase() -> Colors.LabelVegGreen
            NEW_LABEL_DEFAULT.lowercase() -> Colors.LabelNewBlue
            HOT_LABEL_DEFAULT.lowercase() -> Colors.LabelHotRed
            HIT_LABEL_DEFAULT.lowercase() -> Colors.LabelHitOrange
            else -> Colors.LabelDefault
        }

        return LabelUiModel(
            id = id,
            name = name,
            backgroundColor = backgroundColor
        )
    }
}