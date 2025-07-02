package com.mandarinkafe.mandarin.features.menu.presentation.models

import com.mandarinkafe.mandarin.features.menu.domain.models.Banner

internal data class ScrollUi(
    val isAtTop: Boolean,
    val showMenuTopBar: Boolean,
    val showBackToTopFAB: Boolean,
    val onBannerClick: (Banner) -> Unit,
    val onBackToTopClick: () -> Unit,
    val scrollToCategory: (Int) -> Unit,
    val scrollToSubCategory: (Int, currentSubCategories: List<String>) -> Unit
)