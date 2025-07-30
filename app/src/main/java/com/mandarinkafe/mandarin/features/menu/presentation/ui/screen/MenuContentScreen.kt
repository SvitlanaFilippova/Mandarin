package com.mandarinkafe.mandarin.features.menu.presentation.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.mandarinkafe.mandarin.core.domain.models.CustomizedMeal
import com.mandarinkafe.mandarin.core.domain.models.Meal
import com.mandarinkafe.mandarin.core.presentation.theme.Colors
import com.mandarinkafe.mandarin.features.menu.domain.models.Banner
import com.mandarinkafe.mandarin.features.menu.presentation.models.MenuItem
import com.mandarinkafe.mandarin.features.menu.presentation.ui.components.BackToTopFAB
import com.mandarinkafe.mandarin.features.menu.presentation.ui.components.BannersSection
import com.mandarinkafe.mandarin.features.menu.presentation.ui.components.MenuList
import com.mandarinkafe.mandarin.features.menu.presentation.ui.components.TabsSection
import com.mandarinkafe.mandarin.features.menu.presentation.viewmodel.MenuContract.MenuEvent
import com.mandarinkafe.mandarin.shared.ui.viewmodel.SharedContract.SharedEvent

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

    val scrollUi = createMenuScrollUi(
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
            // Баннеры видны только если пользователь в самом верху
            BannersSection(
                visible = scrollUi.isAtTop,
                bannersAreLoading = bannersAreLoading,
                banners = banners,
                onBannerClick = { banner -> scrollUi.onBannerClick(banner) }
            )

            // Табы-категории видны всегда
            TabsSection(
                categories = categories,
                selectedTabIndex = selectedTabIndex,
                selectedSubTabIndex = selectedSubTabIndex,
                onTabSelected = { index -> scrollUi.scrollToCategory(index) },
                onSubTabSelected = { index, currentSubCategories ->
                    scrollUi.scrollToSubCategory(index, currentSubCategories)
                },
                onSearchClick = { onMenuEvent(MenuEvent.SearchOnOpenSearchClick) }
            )

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
        BackToTopFAB(
            modifier = Modifier.align(Alignment.BottomStart),
            visible = scrollUi.showBackToTopFAB,
            onClick = { scrollUi.onBackToTopClick() }
        )
    }
}
