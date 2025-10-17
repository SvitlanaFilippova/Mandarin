package com.mandarinkafe.mandarin.features.menu.presentation.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults.SecondaryIndicator
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import com.mandarinkafe.mandarin.MR
import com.mandarinkafe.mandarin.core.presentation.theme.Colors
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.features.menu.presentation.models.MenuItem
import com.mandarinkafe.mandarin.util.presentation.ui.components.KamelSubcomposeAsyncImage
import dev.icerock.moko.resources.compose.painterResource
import dev.icerock.moko.resources.compose.stringResource

@Composable
fun TabsSection(
    headers: List<MenuItem.HeaderItem>,
    activeTabIndex: Int,
    activeSubTabIndex: Int,
    onCategorySelected: (Int) -> Unit,
    onSubCategorySelected: (Int) -> Unit,
) {
    Column(Modifier.background(Colors.AppBlack)) {
        CategoryTabsRow(
            categories = headers,
            selectedTabIndex = activeTabIndex,
            onTabSelected = { index -> onCategorySelected(index) },
        )

        val subCategoriesForActive =
            headers.getOrNull(activeTabIndex)?.subCategoriesNames.orEmpty()
        if (subCategoriesForActive.isNotEmpty()) {
            SubCategoryTabsRow(
                categories = subCategoriesForActive,
                selectedTabIndex = activeSubTabIndex,
                onTabSelected = { subIndex -> onSubCategorySelected(subIndex) }
            )
        }
    }
}

@Composable
private fun CategoryTabsRow(
    categories: List<MenuItem.HeaderItem>,
    selectedTabIndex: Int,
    onTabSelected: (Int) -> Unit,
) {
    val selectedTab = remember(selectedTabIndex) { selectedTabIndex }

    Column(modifier = Modifier.fillMaxWidth()) {
        ScrollableTabRow(
            containerColor = Colors.AppBlack,
            selectedTabIndex = selectedTab,
            edgePadding = Dimens.ZeroDp0,
            indicator = { tabPositions ->
                if (selectedTabIndex >= 0) {
                    SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = Colors.Orange,
                        height = Dimens.TabActivatedIndicatorHeight2
                    )
                }
            },
            divider = { }
        ) {
            categories.forEachIndexed { index, category ->
                CategoryTabItem(
                    name = category.categoryName,
                    icon = category.tabIcon,
                    isSelected = selectedTabIndex == index,
                    onClick = { onTabSelected(index) },
                )
            }
        }

        HorizontalDivider(
            modifier = Modifier.fillMaxWidth(),
            thickness = Dimens.DividerHeight1,
            color = Colors.LightGrey.copy(alpha = 0.3f)
        )
    }
}

@Composable
private fun CategoryTabItem(
    name: String,
    icon: String?,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val colorFilter = ColorFilter.tint(
        if (isSelected) Colors.Orange else Color.White
    )
    Tab(
        selected = isSelected,
        onClick = onClick,
        text = {
            Text(
                name,
                color = if (isSelected) Colors.Orange else Color.White
            )
        },
        icon = {
            if (icon != null) {
                KamelSubcomposeAsyncImage(
                    model = icon,
                    contentDescription = stringResource(MR.strings.icon_of_category, name),
                    modifier = Modifier.size(Dimens.IconSize24),
                    contentScale = ContentScale.Fit,
                    tint = if (isSelected) Colors.Orange else Color.White,
                    placeholder = MR.images.logo_orange_simplified,
                    error = MR.images.logo_orange_simplified
                )
            } else {
                Image(
                    painter = painterResource(MR.images.logo_orange_simplified),
                    contentDescription = null,
                    modifier = Modifier.size(Dimens.IconSize24),
                    colorFilter = colorFilter
                )
            }
        },
        selectedContentColor = Colors.Orange,
    )
}

@Composable
private fun SubCategoryTabsRow(
    categories: List<String>,
    selectedTabIndex: Int,
    onTabSelected: (Int) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        ScrollableTabRow(
            containerColor = Colors.AppBlack,
            edgePadding = Dimens.ZeroDp0,
            selectedTabIndex = selectedTabIndex,
            indicator = { },
            divider = { },
        ) {
            categories.forEachIndexed { index, category ->
                SubCategoryTabItem(
                    category = category,
                    isSelected = selectedTabIndex == index,
                    onClick = { onTabSelected(index) }
                )
            }
        }
        HorizontalDivider(
            modifier = Modifier.fillMaxWidth(),
            thickness = Dimens.DividerHeight1,
            color = Colors.LightGrey.copy(alpha = 0.3f)
        )
    }
}

@Composable
private fun SubCategoryTabItem(category: String, isSelected: Boolean, onClick: () -> Unit) {
    Tab(
        selected = isSelected,
        onClick = onClick,
        text = {
            Text(
                category,
                color = if (isSelected) Colors.Orange else Color.White
            )
        },
        selectedContentColor = Colors.Orange,
    )
}
