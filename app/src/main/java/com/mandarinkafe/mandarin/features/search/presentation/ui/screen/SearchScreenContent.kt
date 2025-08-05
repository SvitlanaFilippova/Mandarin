@file:OptIn(ExperimentalMaterial3Api::class)

package com.mandarinkafe.mandarin.features.search.presentation.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import com.mandarinkafe.mandarin.R
import com.mandarinkafe.mandarin.core.domain.models.CartItem
import com.mandarinkafe.mandarin.core.domain.models.Meal
import com.mandarinkafe.mandarin.core.presentation.theme.Colors
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.features.search.presentation.ui.components.LabelChipsRow
import com.mandarinkafe.mandarin.features.search.presentation.ui.components.SearchResults
import com.mandarinkafe.mandarin.features.search.presentation.viewmodel.SearchContract
import com.mandarinkafe.mandarin.util.presentation.ui.components.SearchBarInputField

/**
 * Компонент с SearchBar - полем для полиска и его результами
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreenContent(
    focusSearchBarInput: Boolean,
    searchState: SearchContract.SearchState,
    cartItems: List<CartItem>,
    favoriteIds: Set<String>,
    onSearchEvent: (SearchContract.SearchEvent) -> Unit,
    onSearchDismiss: () -> Unit,
    onAddToCart: (Meal) -> Unit,
    onRemoveFromCart: (Meal) -> Unit,
    onMealDetailsClick: (Meal) -> Unit,
    onToggleFavorite: (Meal) -> Unit,
) {
    val filteredMenuItems = searchState.filteredMealList
    val latestSearchText = searchState.latestSearchText
    val keyboardController = LocalSoftwareKeyboardController.current
    var isExpanded by remember { mutableStateOf(true) }
    val handleOnClear = {
        onSearchEvent(SearchContract.SearchEvent.ClearSearchInput)
        keyboardController?.show()
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Colors.AppBlack)
            .padding(Dimens.MarginSmall8),
    ) {
        SearchBarInputField(
            query = latestSearchText,
            onQueryChange = { text ->
                if (text.isEmpty()) {
                    handleOnClear()
                } else {
                    onSearchEvent(SearchContract.SearchEvent.SearchMealsByText(text))
                    isExpanded = true
                }
            },
            onClear = { handleOnClear() },
            onDismiss = { onSearchDismiss() },
            autoFocus = focusSearchBarInput,
            placeholderRes = R.string.search_in_menu,
            enabled = true,
            leadingIcon = {
                Icon(
                    Icons.Default.Search,
                    contentDescription = stringResource(R.string.search_in_menu),
                    tint = Colors.White
                )
            }
        )

        LabelChipsRow(
            labels = searchState.allLabels,
            checkedLabels = searchState.checkedLabels,
            onLabelClick = { label, isChecked ->
                onSearchEvent(
                    SearchContract.SearchEvent.OnLabelClick(
                        labelName = label,
                        isChecked = isChecked
                    )
                )
            },
        )
        SearchResults(
            filteredMenuItems = filteredMenuItems,
            latestSearchText = latestSearchText,
            cartItems = cartItems,
            favoriteIds = favoriteIds,
            onSearchDismiss = onSearchDismiss,
            onToggleFavorite = onToggleFavorite,
            onAddToCart = onAddToCart,
            onRemoveFromCart = onRemoveFromCart,
            onMealDetailsClick = onMealDetailsClick,
        )
    }
}

