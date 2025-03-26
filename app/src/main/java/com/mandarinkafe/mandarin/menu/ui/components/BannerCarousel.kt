package com.mandarinkafe.mandarin.menu.ui.components

import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerDefaults
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import com.mandarinkafe.mandarin.core.ui.theme.Colors
import com.mandarinkafe.mandarin.core.ui.theme.Dimens
import com.mandarinkafe.mandarin.menu.domain.models.Banner
import com.mandarinkafe.mandarin.menu.domain.models.mockBannersList
import com.mandarinkafe.mandarin.util.Constants.ANIMATION_DURATION_FAST
import com.mandarinkafe.mandarin.util.Constants.ANIMATION_DURATION_SLOW
import com.mandarinkafe.mandarin.util.Constants.AUTO_SCROLL_INTERVAL
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun BannerCarousel(
    banners: List<Banner> = mockBannersList,
    autoScrollInterval: Long = AUTO_SCROLL_INTERVAL, // Интервал автопрокрутки
    easing: Easing = LinearEasing,
    onBannerClick: (String) -> Job
) {
    val pagerState = rememberPagerState { banners.size }
    val coroutineScope = rememberCoroutineScope()

    // Автопрокрутка
    LaunchedEffect(Unit) {
        while (true) {
            delay(autoScrollInterval)
            coroutineScope.launch {
                pagerState.animateScrollToPage(
                    page = (pagerState.currentPage + 1) % banners.size,
                    animationSpec = tween(
                        durationMillis = ANIMATION_DURATION_SLOW,
                        easing = easing
                    )
                )
            }
        }
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        HorizontalPager(
            state = pagerState,
            pageSpacing = Dimens.MarginStandard16,
            modifier = Modifier
                .fillMaxWidth()
                .graphicsLayer {
                    // Рендеринг в offscreen buffer для плавности
                    compositingStrategy = CompositingStrategy.Offscreen
                },

            // настройки "ручной" прокрутки
            flingBehavior = PagerDefaults.flingBehavior(
                state = pagerState,
                snapAnimationSpec = tween(
                    durationMillis = ANIMATION_DURATION_FAST,
                    easing = easing
                )
            )
        ) { page ->
            AsyncImage(
                model = banners[page].imageUrl,
                contentDescription = "Баннер $page",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(2.91f)
                    .clip(RoundedCornerShape(Dimens.CornerRadius8))
                    .clickable { onBannerClick(banners[page].goToIdOnClick) }
            )
        }

        // Индикаторы страниц
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    top = Dimens.MarginSmall8,
                    bottom = Dimens.MarginStandard16
                ),
            horizontalArrangement = Arrangement.spacedBy(
                Dimens.MarginSmall8,
                Alignment.CenterHorizontally
            )

        ) {
            banners.forEachIndexed { index, _ ->
                val color = if (pagerState.currentPage == index) Colors.Orange else Colors.Grey
                Box(
                    modifier = Modifier
                        .width(Dimens.BannerIndicatorWidth24)
                        .height(Dimens.BannerIndicatorHeight4)
                        .background(color, shape = RoundedCornerShape(Dimens.RadiusImageCorner2))
                )
            }
        }
    }
}
