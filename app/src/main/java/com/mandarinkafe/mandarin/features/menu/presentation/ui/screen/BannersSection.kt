package com.mandarinkafe.mandarin.features.menu.presentation.ui.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.features.menu.domain.models.Banner
import com.mandarinkafe.mandarin.features.menu.presentation.ui.components.BannerCarousel
import com.mandarinkafe.mandarin.util.Constants.BANNERS_ASPECT_RATIO
import com.mandarinkafe.mandarin.util.presentation.ui.components.buttons.MyCircularProgressIndicator

@Composable
fun BannersSection(
    visible: Boolean,
    bannersAreLoading: Boolean,
    banners: List<Banner>,
    onBannerClick: (Banner) -> Unit
) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn() + expandVertically(),
        exit = fadeOut() + shrinkVertically()
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
                    strokeWidth = Dimens.ProgressBarSmallWidth8,
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
}