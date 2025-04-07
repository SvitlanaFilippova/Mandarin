@file:OptIn(ExperimentalMaterial3Api::class)

package com.mandarinkafe.mandarin.menu.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import com.mandarinkafe.mandarin.R
import com.mandarinkafe.mandarin.core.ui.theme.Colors
import com.mandarinkafe.mandarin.core.ui.theme.Dimens
import com.mandarinkafe.mandarin.menu.domain.models.Meal
import com.mandarinkafe.mandarin.menu.domain.models.MenuRVItem
import com.mandarinkafe.mandarin.menu.ui.MenuContract.Event

@Composable
fun SearchBar(
    filteredMenuItems: List<MenuRVItem>,
    latestSearchText: String,
    onMealClick: (Meal) -> Unit,
    onEvent: (Event) -> Unit,

    ) {
    val keyboardController = LocalSoftwareKeyboardController.current
    var isActive by remember { mutableStateOf(false) }

    SearchBar(
        //логика работы
        query = latestSearchText,
        onQueryChange = { text ->
            onEvent(Event.SearchMealsByText(text))
        },
        onSearch = { text ->
            keyboardController?.hide()
            onEvent(Event.SearchMealsByText(text))
        },
        active = isActive,
        onActiveChange = {
            isActive = it
        },

        //внешний вид
        placeholder = { Text(stringResource(id = R.string.search_in_menu)) },
        shape = RoundedCornerShape(Dimens.RadiusSearchField8),
        colors = SearchBarDefaults.colors(
            containerColor = Colors.GreyTransparent10,
            inputFieldColors = TextFieldDefaults.colors(
                focusedTextColor = Colors.White,
                unfocusedTextColor = Colors.White,
                cursorColor = Colors.Orange

            )
        ),
        leadingIcon = {
            Icon(
                Icons.Default.Search,
                contentDescription = stringResource(id = R.string.search_in_menu)
            )
        },
        trailingIcon = {
            if (isActive) {
                IconButton(onClick = {
                    if (latestSearchText.isNotEmpty()) {
                        onEvent(Event.ClearSearchInput)
                        keyboardController?.show()
                    } else {
                        isActive = false
                    }
                }) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = stringResource(id = R.string.clear_text)
                    )
                }
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(Dimens.MarginSmall8)
    ) {
        // Результаты поиска
        if (filteredMenuItems.isNotEmpty()) {
            SearchResults(
                filteredMenuItems = filteredMenuItems,
                onMealClick = { meal ->
                    onMealClick(meal)
                    isActive = false
                },
                onEvent = onEvent,
            )
        } else {
            if (!latestSearchText.isEmpty()) {
                Text(
                    text = stringResource(id = R.string.nothing_found),
                    color = Colors.White,
                    modifier = Modifier.padding(
                        Dimens.MarginStandard16
                    )
                )
            }
        }
    }
}
