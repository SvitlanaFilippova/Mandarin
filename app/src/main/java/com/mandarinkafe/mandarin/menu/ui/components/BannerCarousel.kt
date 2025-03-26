package com.mandarinkafe.mandarin.menu.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import coil.compose.AsyncImage
import com.mandarinkafe.mandarin.core.ui.theme.Colors
import com.mandarinkafe.mandarin.core.ui.theme.Dimens
import com.mandarinkafe.mandarin.menu.domain.models.Banner
import com.mandarinkafe.mandarin.menu.domain.models.mockBannersList
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Preview
@Composable
fun BannerCarouselPreview() {
    val banners = mockBannersList
    BannerCarousel(banners = banners)
}

@Composable
fun BannerCarousel(
    banners: List<Banner>, // URL изображений
    autoScrollInterval: Long = 5000L // Интервал автопрокрутки
) {
    val pagerState = rememberPagerState { banners.size }
    val coroutineScope = rememberCoroutineScope()

    // Автопрокрутка
    LaunchedEffect(Unit) {
        while (true) {
            delay(autoScrollInterval)
            coroutineScope.launch {
                val nextPage = (pagerState.currentPage + 1) % banners.size
                pagerState.animateScrollToPage(nextPage)
            }
        }
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Dimens.MarginSuperSmall4)
    ) {
        // Горизонтальный баннер с прокруткой
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxWidth()
        ) { page ->
            AsyncImage(
                model = banners[page].imageUrl,
                contentDescription = "Баннер $page",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(2.91f) // Пропорции баннера
                    .clip(RoundedCornerShape(Dimens.CornerRadius8))
            )
        }

        // Индикаторы страниц
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    top = Dimens.MarginSmall8,
                    start = Dimens.MarginBig32,
                    end = Dimens.MarginBig32
                ),
            horizontalArrangement = Arrangement.Absolute.SpaceEvenly
        ) {
            banners.forEachIndexed { index, _ ->
                val color = if (pagerState.currentPage == index) Colors.Orange else Colors.Grey
                Box(
                    modifier = Modifier
                        .size(if (pagerState.currentPage == index) Dimens.DotsIndicatorSizeSelected8 else Dimens.DotsIndicatorSize4)
                        .background(color, shape = CircleShape)
                        .padding(Dimens.MarginStandard16)
                )
            }
        }
    }
}
