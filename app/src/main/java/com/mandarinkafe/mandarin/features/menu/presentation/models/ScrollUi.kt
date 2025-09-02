package com.mandarinkafe.mandarin.features.menu.presentation.models

import com.mandarinkafe.mandarin.features.menu.domain.models.Banner

data class ScrollUi(
    val isAtTop: () -> Boolean,
    val showBackToTopFAB: () -> Boolean,
    val onBannerClick: (Banner) -> Unit,
    val onBackToTopClick: () -> Unit,
    val scrollToCategory: (Int, List<MenuItem>) -> Unit,
    val scrollToSubCategory: (Int, List<String>, List<MenuItem>) -> Unit,
    val updateCategories: (List<String>) -> Unit
)