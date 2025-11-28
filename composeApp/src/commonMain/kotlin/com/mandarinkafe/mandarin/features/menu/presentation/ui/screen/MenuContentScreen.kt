package com.mandarinkafe.mandarin.features.menu.presentation.ui.screen

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.Dp
import androidx.navigation.NavController
import com.mandarinkafe.mandarin.core.domain.models.CartItem
import com.mandarinkafe.mandarin.core.domain.models.Meal
import com.mandarinkafe.mandarin.core.presentation.theme.Colors
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.features.menu.domain.models.Banner
import com.mandarinkafe.mandarin.features.menu.presentation.models.MenuItem
import com.mandarinkafe.mandarin.features.menu.presentation.ui.components.ActiveOrderCard
import com.mandarinkafe.mandarin.features.menu.presentation.ui.components.AnnouncementsSection
import com.mandarinkafe.mandarin.features.menu.presentation.ui.components.BackToTopFAB
import com.mandarinkafe.mandarin.features.menu.presentation.ui.components.BannersSection
import com.mandarinkafe.mandarin.features.menu.presentation.ui.components.MenuItemCard
import com.mandarinkafe.mandarin.features.menu.presentation.ui.components.ScrollUiState
import com.mandarinkafe.mandarin.features.menu.presentation.ui.components.TabsSection
import com.mandarinkafe.mandarin.features.menu.presentation.ui.components.rememberScrollUiState
import com.mandarinkafe.mandarin.features.ordershistory.domain.models.SavedOrder
import com.mandarinkafe.mandarin.navigation.extensions.navigateToOrderInfo
import com.mandarinkafe.mandarin.shared.presentation.viewmodel.SharedContract.SharedEffect
import com.mandarinkafe.mandarin.util.Constants
import com.mandarinkafe.mandarin.util.Constants.MENU_IMAGE_COLUMN_COUNT
import com.mandarinkafe.mandarin.util.Constants.MENU_IMAGE_SPACING_COUNT
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

@Composable
fun MenuContentScreen(
    menuItems: List<MenuItem>,
    favoriteIds: Set<String>,
    inProgressItems: Set<String>,
    cartItems: List<CartItem>,
    onAddToCart: (Meal) -> Unit,
    onRemoveFromCart: (Meal) -> Unit,
    onBannersClick: (Banner) -> Unit,
    onToggleFavorite: (Meal) -> Unit,
    onMealDetailsClick: (Meal) -> Unit,
    bannersAreLoading: Boolean,
    selectedMenuItemIndex: Int,
    banners: List<Banner>,
    announcements: List<String>,
    sharedEffectFlow: SharedFlow<SharedEffect>,
    activeOrders: List<SavedOrder>,
    navController: NavController,
) {
    val categoryPositions = rememberCategoryPositions(menuItems)
    val subCategoryPositionsMap = rememberSubCategoryPositionsMap(menuItems, categoryPositions)
    val scrollUi = rememberScrollUiState(categoryPositions, subCategoryPositionsMap)
    val scope = rememberCoroutineScope()
    val activeTabIndex = remember { mutableIntStateOf(0) }
    val activeSubTabIndex = remember { mutableIntStateOf(-1) }

    val isScrollingUp by scrollUi.isScrollingUp.collectAsState()
    val isAtTop by scrollUi.isAtTop.collectAsState()

    var showFab by remember { mutableStateOf(false) }
    val showFabTemporarily: CoroutineScope.() -> Unit = {
        launch {
            showFab = true
            delay(Constants.FORCE_SHOW_FAB_DURATION_MS)
            showFab = false
        }
    }

    val imageSize = rememberImageSize()

    MenuScrollEffects(
        scrollUi = scrollUi,
        activeTabIndex = activeTabIndex,
        activeSubTabIndex = activeSubTabIndex,
        selectedMenuItemIndex = selectedMenuItemIndex,
        scope = scope,
        showFabTemporarily = showFabTemporarily
    )

    MenuSharedEffectHandler(
        sharedEffectFlow = sharedEffectFlow,
        scrollUi = scrollUi
    )

    Box(modifier = Modifier.fillMaxSize()) {
        MenuLazyColumn(
            menuItems = menuItems,
            announcements = announcements,
            activeOrders = activeOrders,
            banners = banners,
            bannersAreLoading = bannersAreLoading,
            cartItems = cartItems,
            favoriteIds = favoriteIds,
            inProgressItems = inProgressItems,
            imageSize = imageSize,
            activeTabIndex = activeTabIndex.intValue,
            activeSubTabIndex = activeSubTabIndex.intValue,
            scrollUi = scrollUi,
            scope = scope,
            showFabTemporarily = showFabTemporarily,
            onBannersClick = onBannersClick,
            onAddToCart = onAddToCart,
            onRemoveFromCart = onRemoveFromCart,
            onToggleFavorite = onToggleFavorite,
            onMealDetailsClick = onMealDetailsClick,
            onCategorySelected = { index ->
                activeTabIndex.intValue = index
                activeSubTabIndex.intValue = 0
            },
            onSubCategorySelected = { subIndex ->
                activeSubTabIndex.intValue = subIndex
            },
            navController = navController
        )

        BackToTopFAB(
            modifier = Modifier
                .align(Alignment.BottomStart),
            visible = (showFab || isScrollingUp) && !isAtTop,
            onClick = {
                scope.launch {
                    scrollUi.scrollToTop()
                }
            }
        )
    }
}


@Composable
private fun rememberCategoryPositions(menuItems: List<MenuItem>): List<Int> {
    return remember(menuItems) {
        menuItems.mapIndexedNotNull { index, item ->
            if (item is MenuItem.HeaderItem) index else null
        }
    }
}

@Composable
private fun rememberSubCategoryPositionsMap(
    menuItems: List<MenuItem>,
    categoryPositions: List<Int>,
): Map<Int, List<Int>> {
    return remember(menuItems, categoryPositions) {
        categoryPositions.associateWith { headerIndex ->
            menuItems.mapIndexedNotNull { index, item ->
                if (index > headerIndex && item is MenuItem.SubHeaderItem) index else null
            }
        }
    }
}

@Composable
private fun rememberImageSize(): Dp {
    val windowInfo = LocalWindowInfo.current
    val density = LocalDensity.current
    val horizontalPadding = Dimens.MarginSmall8
    return remember(windowInfo, density) {
        val screenWidthDp = with(density) { windowInfo.containerSize.width.toDp() }
        (screenWidthDp - horizontalPadding * MENU_IMAGE_SPACING_COUNT) / MENU_IMAGE_COLUMN_COUNT
    }
}

@Composable
private fun MenuScrollEffects(
    scrollUi: ScrollUiState,
    activeTabIndex: androidx.compose.runtime.MutableIntState,
    activeSubTabIndex: androidx.compose.runtime.MutableIntState,
    selectedMenuItemIndex: Int,
    scope: CoroutineScope,
    showFabTemporarily: CoroutineScope.() -> Unit,
) {
    LaunchedEffect(scrollUi.listState) {
        snapshotFlow { scrollUi.listState.firstVisibleItemIndex }
            .collect {
                val newIndex = scrollUi.getActiveTabIndex()
                if (activeTabIndex.intValue != newIndex) {
                    activeTabIndex.intValue = newIndex
                    activeSubTabIndex.intValue = 0
                }
                val newSubIndex = scrollUi.getActiveSubTabIndexForHeader(activeTabIndex.intValue)
                if (activeSubTabIndex.intValue != newSubIndex) {
                    activeSubTabIndex.intValue = newSubIndex
                }
            }
    }

    LaunchedEffect(selectedMenuItemIndex) {
        if (selectedMenuItemIndex != Constants.DEFAULT_UNSELECTED_INDEX) {
            scrollUi.listState.scrollToItem(selectedMenuItemIndex + 1)
            scope.showFabTemporarily()
        }
    }
}

@Composable
private fun MenuSharedEffectHandler(
    sharedEffectFlow: SharedFlow<SharedEffect>,
    scrollUi: ScrollUiState,
) {
    LaunchedEffect(Unit) {
        sharedEffectFlow.collect { effect ->
            if (effect is SharedEffect.ScrollToTop) {
                scrollUi.scrollToTop()
            }
        }
    }
}

@Composable
private fun MenuLazyColumn(
    menuItems: List<MenuItem>,
    announcements: List<String>,
    activeOrders: List<SavedOrder>,
    banners: List<Banner>,
    bannersAreLoading: Boolean,
    cartItems: List<CartItem>,
    favoriteIds: Set<String>,
    inProgressItems: Set<String>,
    imageSize: Dp,
    activeTabIndex: Int,
    activeSubTabIndex: Int,
    scrollUi: ScrollUiState,
    scope: CoroutineScope,
    showFabTemporarily: CoroutineScope.() -> Unit,
    onBannersClick: (Banner) -> Unit,
    onAddToCart: (Meal) -> Unit,
    onRemoveFromCart: (Meal) -> Unit,
    onToggleFavorite: (Meal) -> Unit,
    onMealDetailsClick: (Meal) -> Unit,
    onCategorySelected: (Int) -> Unit,
    onSubCategorySelected: (Int) -> Unit,
    navController: NavController,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        state = scrollUi.listState,
        verticalArrangement = Arrangement.spacedBy(Dimens.MarginSmall8),
    ) {
        item {
            AnnouncementsSection(announcements = announcements)
        }

        if (activeOrders.isNotEmpty()) {
            items(
                items = activeOrders,
                key = { order -> order.id }
            ) { order ->
                ActiveOrderCard(
                    modifier = Modifier.fillMaxWidth(),
                    order = order,
                    onClick = {
                        navController.navigateToOrderInfo(
                            orderId = order.id,
                            paymentMethodCode = order.paymentMethodCode
                        )
                    }
                )
            }
        }

        item {
            BannersSection(
                banners = banners,
                bannersAreLoading = bannersAreLoading,
                onBannerClick = onBannersClick
            )
        }

        stickyHeader {
            MenuStickyHeader(
                menuItems = menuItems,
                activeTabIndex = activeTabIndex,
                activeSubTabIndex = activeSubTabIndex,
                onCategorySelected = { index ->
                    onCategorySelected(index)
                    scope.launch {
                        scrollUi.scrollToCategory(index)
                        scope.showFabTemporarily()
                    }
                },
                onSubCategorySelected = { subIndex ->
                    onSubCategorySelected(subIndex)
                    scope.launch {
                        scrollUi.scrollToSubCategory(activeTabIndex, subIndex)
                        scope.showFabTemporarily()
                    }
                }
            )
        }

        itemsIndexed(
            items = menuItems,
            key = { _, item -> item.id }
        ) { index, item ->
            val previousItem = menuItems.getOrNull(index - 1)
            MenuItemCard(
                item = item,
                previousItem = previousItem,
                cartItems = cartItems,
                favoriteIds = favoriteIds,
                inProgressItems = inProgressItems,
                imageSize = imageSize,
                onAddToCart = onAddToCart,
                onRemoveFromCart = onRemoveFromCart,
                onToggleFavorite = onToggleFavorite,
                onMealDetailsClick = onMealDetailsClick
            )
        }

        item {
            Spacer(modifier = Modifier.height(Dimens.MarginBig32))
        }
    }
}

@Composable
private fun MenuStickyHeader(
    menuItems: List<MenuItem>,
    activeTabIndex: Int,
    activeSubTabIndex: Int,
    onCategorySelected: (Int) -> Unit,
    onSubCategorySelected: (Int) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(animationSpec = tween(durationMillis = Constants.ANIMATION_DURATION_FAST))
            .background(color = Colors.AppBlack)
    ) {
        // Табы категорий
        val headers = menuItems.filterIsInstance<MenuItem.HeaderItem>()
        TabsSection(
            headers = headers,
            activeTabIndex = activeTabIndex,
            activeSubTabIndex = activeSubTabIndex,
            onCategorySelected = onCategorySelected,
            onSubCategorySelected = onSubCategorySelected
        )
    }
}
