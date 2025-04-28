@file:OptIn(ExperimentalMaterial3Api::class)

package com.mandarinkafe.mandarin.search.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import com.mandarinkafe.mandarin.cart.ui.view_model.CartContract
import com.mandarinkafe.mandarin.core.ui.theme.Colors
import com.mandarinkafe.mandarin.core.ui.theme.Dimens
import com.mandarinkafe.mandarin.menu.domain.models.MenuItem
import com.mandarinkafe.mandarin.menu.ui.view_model.MenuContract.Event

/**
 * Компонент с SearchBar - полем для полиска и его результами
 */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MySearchBar(
    filteredMenuItems: List<MenuItem>,
    latestSearchText: String,
    onEvent: (Event) -> Unit,
    onCartEvent: (CartContract.Event) -> Unit,
    onMealClick: () -> Unit,
    onSearchDismiss: () -> Unit,
    focusSearchBarInput: Boolean,
    cartState: CartContract.State,
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    var isExpanded by remember { mutableStateOf(true) }
    val handleOnClear = {
        onEvent(Event.SearchClearInput)
        keyboardController?.show()
        isExpanded = false
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Colors.AppBlack)
    ) {
        SearchBar(
            inputField = {
                SearchBarInputField(
                    query = latestSearchText,
                    onQueryChange = { text ->
                        if (text.isEmpty()) {
                            handleOnClear()
                        } else {
                            onEvent(Event.SearchMealsByText(text))
                            isExpanded = true
                        }
                    },
                    onClear = { handleOnClear() },
                    onSearchDismiss = { onSearchDismiss() },
                    autoFocus = focusSearchBarInput
                )
            },
            expanded = isExpanded,
            onExpandedChange = { isExpanded = it },
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .clip(
                    RoundedCornerShape(Dimens.RadiusSearchField8)
                )
                .padding(Dimens.MarginSmall8),
            shape = RoundedCornerShape(Dimens.RadiusSearchField8),
            colors = SearchBarDefaults.colors(
                containerColor = Colors.GreyTransparent10
            ),
            content = {
                SearchResults(
                    filteredMenuItems = filteredMenuItems,
                    latestSearchText = latestSearchText,
                    onEvent = onEvent,
                    onMealClick = onMealClick,
                    onCartEvent = onCartEvent,
                    cartState = cartState
                )
            }
        )
    }
}
