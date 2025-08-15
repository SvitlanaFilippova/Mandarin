package com.mandarinkafe.mandarin.features.menu.presentation.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.features.menu.domain.models.Banner
import com.mandarinkafe.mandarin.util.Constants.BANNERS_ASPECT_RATIO
import com.mandarinkafe.mandarin.util.presentation.ui.components.MyCircularProgressIndicator

@Composable
fun BannersSection(
    bannersAreLoading: Boolean,
    banners: List<Banner>,
    onBannerClick: (Banner) -> Unit
) {
    if (bannersAreLoading) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimens.MarginStandard16)
                .aspectRatio(BANNERS_ASPECT_RATIO),
            contentAlignment = Alignment.Center
        ) {
            MyCircularProgressIndicator(
                strokeWidth = Dimens.ProgressBarStroke6,
            )
        }
    } else {
        if (!banners.isEmpty()) {
            BannerCarousel(
                banners = banners,
                onBannerClick = { banner -> onBannerClick(banner) }
            )
        }
    }
}
