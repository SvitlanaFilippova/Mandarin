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
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import com.mandarinkafe.mandarin.core.presentation.theme.Colors
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.features.menu.domain.models.Banner
import com.mandarinkafe.mandarin.util.Constants.ANIMATION_DURATION_FAST
import com.mandarinkafe.mandarin.util.Constants.ANIMATION_DURATION_SLOW
import com.mandarinkafe.mandarin.util.Constants.BANNERS_ANIMATION_COMPLETE_THRESHOLD
import com.mandarinkafe.mandarin.util.Constants.BANNERS_ASPECT_RATIO
import com.mandarinkafe.mandarin.util.Constants.BANNERS_AUTO_SCROLL_INTERVAL
import com.mandarinkafe.mandarin.util.presentation.ui.components.images.KamelSubcomposeAsyncImageSimple
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Создаёт расширенный список для бесконечной прокрутки:
 * последний + все элементы + первый
 */
private fun createInfiniteBanners(banners: List<Banner>): List<Banner> {
    return if (banners.isEmpty()) {
        emptyList()
    } else {
        listOf(banners.last()) + banners + listOf(banners.first())
    }
}

/**
 * Вычисляет реальный индекс баннера с учётом дублирования
 */
private fun getRealBannerIndex(
    page: Int,
    infiniteBannersSize: Int,
    bannersSize: Int,
): Int {
    return when {
        page == 0 -> bannersSize - 1 // Дубликат последнего
        page == infiniteBannersSize - 1 -> 0 // Дубликат первого
        else -> page - 1 // Реальные элементы (смещение на 1 из-за дубликата в начале)
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BannerCarousel(
    banners: List<Banner>,
    autoScrollInterval: Long = BANNERS_AUTO_SCROLL_INTERVAL,
    easing: androidx.compose.animation.core.Easing = androidx.compose.animation.core.LinearEasing,
    onBannerClick: (Banner) -> Unit,
) {
    val infiniteBanners = remember(banners) { createInfiniteBanners(banners) }

    // Инициализируем на первом реальном элементе (индекс 1), если список не пустой
    val pagerState = rememberPagerState(initialPage = if (infiniteBanners.isNotEmpty()) 1 else 0) {
        infiniteBanners.size.coerceAtLeast(1)
    }
    val coroutineScope = rememberCoroutineScope()

    // Бесконечная прокрутка: при достижении границ незаметно переключаемся
    // Отслеживаем только когда анимация завершена (offsetFraction близок к 0)
    LaunchedEffect(pagerState) {
        if (banners.isNotEmpty() && infiniteBanners.size > 2) {
            snapshotFlow {
                pagerState.currentPage to kotlin.math.abs(pagerState.currentPageOffsetFraction)
            }.collect { (page, offset) ->
                // Переключаемся только когда анимация завершена
                if (offset < BANNERS_ANIMATION_COMPLETE_THRESHOLD) {
                    when (page) {
                        0 -> {
                            // Достигли дубликата последнего элемента - переключаемся на реальный последний
                            pagerState.scrollToPage(page = banners.size)
                        }
                        infiniteBanners.size - 1 -> {
                            // Достигли дубликата первого элемента - переключаемся на реальный первый
                            pagerState.scrollToPage(page = 1)
                        }
                    }
                }
            }
        }
    }

    // Автопрокрутка
    LaunchedEffect(Unit) {
        if (infiniteBanners.isNotEmpty()) {
            while (true) {
                delay(autoScrollInterval)
                coroutineScope.launch {
                    val nextPage = (pagerState.currentPage + 1) % infiniteBanners.size
                    pagerState.animateScrollToPage(
                        page = nextPage,
                        animationSpec = tween(
                            durationMillis = ANIMATION_DURATION_SLOW,
                            easing = easing
                        )
                    )
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = Dimens.MarginSmall8,
                end = Dimens.MarginSmall8,
                top = Dimens.MarginSmall8
            )
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
            if (infiniteBanners.isNotEmpty() && banners.isNotEmpty()) {
                val realIndex = getRealBannerIndex(page, infiniteBanners.size, banners.size)

                KamelSubcomposeAsyncImageSimple(
                    model = infiniteBanners[page].imageUrl,
                    contentDescription = "Banner ${realIndex + 1}",
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(BANNERS_ASPECT_RATIO)
                        .clip(RoundedCornerShape(Dimens.CornerRadius8))
                        .clickable { onBannerClick(banners[realIndex]) },
                    crossfade = true
                )
            }
        }

        BannerIndicators(
            banners = banners,
            currentPage = pagerState.currentPage,
            infiniteBannersSize = infiniteBanners.size
        )
    }
}

@Composable
private fun BannerIndicators(
    banners: List<Banner>,
    currentPage: Int,
    infiniteBannersSize: Int,
) {
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
            val realCurrentPage = getRealBannerIndex(currentPage, infiniteBannersSize, banners.size)
            val isActive = realCurrentPage == index

            val animatedWidth by animateDpAsState(
                targetValue = if (isActive) Dimens.BannerIndicatorActiveWidth32 else Dimens.BannerIndicatorInactiveWidth8,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioLowBouncy,
                    stiffness = Spring.StiffnessLow
                ),
                label = "indicator_width"
            )

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
