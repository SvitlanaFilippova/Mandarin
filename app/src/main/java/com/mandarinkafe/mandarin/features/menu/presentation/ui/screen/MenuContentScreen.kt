package com.mandarinkafe.mandarin.features.menu.presentation.ui.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.mandarinkafe.mandarin.core.domain.models.CustomizedMeal
import com.mandarinkafe.mandarin.core.domain.models.Meal
import com.mandarinkafe.mandarin.core.presentation.theme.Colors
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.features.menu.domain.models.Banner
import com.mandarinkafe.mandarin.features.menu.presentation.models.MenuItem
import com.mandarinkafe.mandarin.features.menu.presentation.ui.components.BackToTopFAB
import com.mandarinkafe.mandarin.features.menu.presentation.ui.components.BannerCarousel
import com.mandarinkafe.mandarin.features.menu.presentation.ui.components.MenuList
import com.mandarinkafe.mandarin.features.menu.presentation.ui.components.SearchBar
import com.mandarinkafe.mandarin.features.menu.presentation.ui.components.categorytabs.CategoryTabsRow
import com.mandarinkafe.mandarin.features.menu.presentation.ui.components.categorytabs.SubCategoryTabsRow
import com.mandarinkafe.mandarin.features.menu.presentation.viewmodel.MenuContract.MenuEvent
import com.mandarinkafe.mandarin.shared.ui.viewmodel.SharedContract.SharedEvent
import com.mandarinkafe.mandarin.util.Constants.BANNERS_ASPECT_RATIO
import com.mandarinkafe.mandarin.util.presentation.ui.components.buttons.MyCircularProgressIndicator

@Composable
fun MenuContentScreen(
    listState: LazyListState,
    cartItems: Map<CustomizedMeal, Int>,
    menuItems: List<MenuItem>,
    banners: List<Banner>,
    bannersAreLoading: Boolean,
    favoriteIds: Set<String>,
    selectedTabIndex: Int,
    selectedSubTabIndex: Int,
    selectedMenuItemIndex: Int,
    onMenuEvent: (MenuEvent) -> Unit,
    onSharedEvent: (SharedEvent) -> Unit,
    onToggleFavorite: (Meal) -> Unit,
    onAddToCart: (Meal) -> Unit,
    onRemoveFromCart: (Meal) -> Unit,
    onMealDetailsClick: (Meal) -> Unit,
) {
    val categories = remember(menuItems) {
        menuItems.filterIsInstance<MenuItem.HeaderItem>()
    }

    val categoriesNames = remember(categories) {
        categories.map { it.categoryName }
    }

    val scrollUi = rememberScrollUi(
        listState = listState,
        selectedTabIndex = selectedTabIndex,
        selectedSubTabIndex = selectedSubTabIndex,
        selectedMenuItemIndex = selectedMenuItemIndex,
        menuItems = menuItems,
        categoriesNames = categoriesNames,
        onMenuEvent = onMenuEvent,
        onSharedEvent = onSharedEvent,
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Colors.AppBlack)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Бар с поиском и фильтрами появляется всегда, когда пользователь вверху или скроллит вверх
            AnimatedVisibility(
                visible = scrollUi.showMenuTopBar,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                SearchBar(
                    onSearchClick = { onMenuEvent(MenuEvent.SearchOnOpenSearchClick) },
                )
            }

            // Баннеры видны только если пользователь в самом верху
            AnimatedVisibility(
                visible = scrollUi.isAtTop,
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
                            onBannerClick = { banner -> scrollUi.onBannerClick(banner) }
                        )
                    }
                }
            }

            // Табы-категории видны всегда
            CategoryTabsRow(
                categories = categories,
                selectedTabIndex = selectedTabIndex,
                onTabSelected = { index -> scrollUi.scrollToCategory(index) }
            )

            if (selectedTabIndex >= 0) {
                val currentSubCategories = categories[selectedTabIndex].subCategoriesNames

                // Табы-подкатегории, появляются при наличии в текущей категории
                if (!currentSubCategories.isNullOrEmpty()) {
                    AnimatedVisibility(
                        visible = currentSubCategories.isNotEmpty(),
                        enter = fadeIn() + expandVertically(),
                        exit = fadeOut() + shrinkVertically()
                    ) {
                        SubCategoryTabsRow(
                            categories = currentSubCategories,
                            selectedTabIndex = selectedSubTabIndex,
                            onTabSelected = { index ->
                                scrollUi.scrollToSubCategory(index, currentSubCategories)
                            }
                        )
                    }
                }
            }

            // Основное меню
            MenuList(
                modifier = Modifier
                    .weight(1f),
                menuItems = menuItems,
                listState = listState,
                cartItems = cartItems,
                onMealDetailsClick = onMealDetailsClick,
                onToggleFavorite = onToggleFavorite,
                onAddToCart = onAddToCart,
                onRemoveFromCart = onRemoveFromCart,
                favoriteIds = favoriteIds,
            )
        }

        // FAB для возврата наверх, видна когда юзер не скролит вниз и не находится наверху экрана
        AnimatedVisibility(
            visible = scrollUi.showBackToTopFAB,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(Dimens.MarginStandard16)
        ) {
            BackToTopFAB(
                onClick = { scrollUi.onBackToTopClick() }
            )
        }
    }
}
