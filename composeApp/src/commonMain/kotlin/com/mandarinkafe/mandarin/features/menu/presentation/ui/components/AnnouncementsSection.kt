package com.mandarinkafe.mandarin.features.menu.presentation.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.util.presentation.ui.components.TooltipText

@Composable
fun AnnouncementsSection(announcements: List<String>) {
    announcements.forEach { announcement ->
        if (announcement.isNotEmpty()) {
            TooltipText(
                modifier = Modifier.padding(Dimens.MarginSmall8),
                text = announcement
            )
        }
    }
}