@file:OptIn(ExperimentalMaterial3Api::class)

package com.mandarinkafe.mandarin.features.search.presentation.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import com.mandarinkafe.mandarin.MR
import com.mandarinkafe.mandarin.core.domain.models.CartItem
import com.mandarinkafe.mandarin.core.domain.models.Meal
import com.mandarinkafe.mandarin.core.presentation.theme.Colors
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.features.search.presentation.ui.components.LabelChipsRow
import com.mandarinkafe.mandarin.features.search.presentation.ui.components.SearchResults
import com.mandarinkafe.mandarin.features.search.presentation.viewmodel.SearchContract
import com.mandarinkafe.mandarin.util.presentation.ui.components.ScreenTitleWithBackButton
import com.mandarinkafe.mandarin.util.presentation.ui.components.textfields.SearchBarInputField
import dev.icerock.moko.resources.compose.painterResource
import dev.icerock.moko.resources.compose.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreenContent(
    focusSearchBarInput: Boolean,
    searchState: SearchContract.SearchState,
    cartItems: List<CartItem>,
    favoriteIds: Set<String>,
    inProgressItems: Set<String>,
    onSearchEvent: (SearchContract.SearchEvent) -> Unit,
    onAddToCart: (Meal) -> Unit,
    onRemoveFromCart: (Meal) -> Unit,
    onMealDetailsClick: (Meal) -> Unit,
    onToggleFavorite: (Meal) -> Unit,
) {
    val filteredMenuItems = searchState.filteredMealList
    val latestSearchText = searchState.latestSearchText
    val keyboardController = LocalSoftwareKeyboardController.current
    val handleOnClear = {
        onSearchEvent(SearchContract.SearchEvent.ClearSearchInput)
        keyboardController?.show()
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Colors.AppBlack)
            .padding(horizontal = Dimens.MarginSmall8),
    ) {
         SearchBarInputField(
            query = latestSearchText,
            onQueryChange = { text ->
                if (text.isEmpty()) {
                    handleOnClear()
                } else {
                    onSearchEvent(SearchContract.SearchEvent.SearchMealsByText(text))
                }
            },
            onClear = { handleOnClear() },
            autoFocus = focusSearchBarInput,
            placeholderText = stringResource(MR.strings.search_by_meal_or_category),
            enabled = true,
            leadingIcon = {
                Icon(
                    painterResource(MR.images.ic_search),
                    contentDescription = stringResource(MR.strings.search_in_menu),
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
            onToggleFavorite = onToggleFavorite,
            onAddToCart = onAddToCart,
            onRemoveFromCart = onRemoveFromCart,
            onMealDetailsClick = onMealDetailsClick,
            inProgressItems = inProgressItems,
        )
    }
}

