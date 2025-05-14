package com.mandarinkafe.mandarin.features.search

import com.mandarinkafe.mandarin.core.domain.models.Label
import com.mandarinkafe.mandarin.core.ui.theme.Colors
import com.mandarinkafe.mandarin.features.search.ui.model.LabelUiModel
import com.mandarinkafe.mandarin.util.Constants.HIT_LABEL
import com.mandarinkafe.mandarin.util.Constants.HOT_LABEL
import com.mandarinkafe.mandarin.util.Constants.NEW_LABEL
import com.mandarinkafe.mandarin.util.Constants.VEG_LABEL

object SearchMapper {

    fun Label.toUiModel(): LabelUiModel {
        val backgroundColor = when (name.lowercase()) {
            VEG_LABEL.lowercase() -> Colors.LabelVegGreen
            NEW_LABEL.lowercase() -> Colors.LabelNewBlue
            HOT_LABEL.lowercase() -> Colors.LabelHotRed
            HIT_LABEL.lowercase() -> Colors.LabelHitOrange
            else -> Colors.LabelDefault
        }

        return LabelUiModel(
            id = id,
            name = name,
            backgroundColor = backgroundColor
        )
    }
}