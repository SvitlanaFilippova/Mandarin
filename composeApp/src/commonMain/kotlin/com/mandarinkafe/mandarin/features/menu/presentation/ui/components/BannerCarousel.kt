package com.mandarinkafe.mandarin.features.menu.presentation.ui.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import com.mandarinkafe.mandarin.core.presentation.theme.Colors
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.features.menu.domain.models.Banner
import com.mandarinkafe.mandarin.util.Constants.ANIMATION_DURATION_FAST
import com.mandarinkafe.mandarin.util.Constants.ANIMATION_DURATION_SLOW
import com.mandarinkafe.mandarin.util.Constants.BANNERS_ASPECT_RATIO
import com.mandarinkafe.mandarin.util.Constants.BANNERS_AUTO_SCROLL_INTERVAL
import com.mandarinkafe.mandarin.util.presentation.ui.components.images.KamelSubcomposeAsyncImageSimple
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BannerCarousel(
    banners: List<Banner>,
    autoScrollInterval: Long = BANNERS_AUTO_SCROLL_INTERVAL,
    easing: androidx.compose.animation.core.Easing = androidx.compose.animation.core.LinearEasing,
    onBannerClick: (Banner) -> Unit
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

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Dimens.MarginSmall8)
    ) {
        HorizontalPager(
            state = pagerState,
            pageSpacing = Dimens.MarginStandard16,
            modifier = Modifier
                .fillMaxWidth()
                .graphicsLayer {
                    // Рендеринг в offscreen buffer для плавности
                    compositingStrategy = androidx.compose.ui.graphics.CompositingStrategy.Offscreen
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
            KamelSubcomposeAsyncImageSimple(
                model = banners[page].imageUrl,
                contentDescription = "Banner ${page + 1}",
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(BANNERS_ASPECT_RATIO)
                    .clickable { onBannerClick(banners[page]) },
                crossfade = true
            )
        }

        // Индикаторы
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    top = Dimens.MarginSmall8,
                    bottom = Dimens.MarginSmall8
                ),
            horizontalArrangement = Arrangement.spacedBy(
                Dimens.MarginSmall8,
                Alignment.CenterHorizontally
            )
        ) {
            banners.forEachIndexed { index, _ ->
                val isActive = pagerState.currentPage == index

                // Анимированная ширина
                val animatedWidth by animateDpAsState(
                    targetValue = if (isActive) Dimens.BannerIndicatorActiveWidth32 else Dimens.BannerIndicatorInactiveWidth8,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioLowBouncy,
                        stiffness = Spring.StiffnessLow
                    ),
                    label = "indicator_width"
                )

                // Цвет индикатора
                val indicatorColor = if (isActive) Colors.Orange else Colors.LightGrey

                Box(
                    modifier = Modifier
                        .width(animatedWidth)
                        .height(Dimens.BannerIndicatorSize4)
                        .background(
                            color = indicatorColor,
                            shape = RoundedCornerShape(Dimens.RadiusImageCorner2)
                        )
                )
            }
        }
    }
}
