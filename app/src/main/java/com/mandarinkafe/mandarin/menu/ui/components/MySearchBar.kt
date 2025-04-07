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
import com.mandarinkafe.mandarin.core.ui.theme.Colors
import com.mandarinkafe.mandarin.core.ui.theme.Dimens
import com.mandarinkafe.mandarin.menu.domain.models.Meal
import com.mandarinkafe.mandarin.menu.domain.models.MenuRVItem

@Composable
fun MySearchBar(
    filteredMenuItems: List<MenuRVItem>,
    latestSearchText: String,
    onSearch: (String) -> Unit,
    onClearSearch: () -> Unit,
    onAddToCart: (Meal) -> Unit,
    onRemoveFromCart: (Meal) -> Unit,
    onCustomizeClick: (Meal) -> Unit,
    onToggleFavorite: (Meal) -> Unit,
    onMealClick: (Meal) -> Unit

) {
    val keyboardController = LocalSoftwareKeyboardController.current
    var isActive by remember { mutableStateOf(false) }

    SearchBar(
        //логика работы
        query = latestSearchText,
        onQueryChange = { text ->
            onSearch(text)
        },
        onSearch = { text ->
            keyboardController?.hide()
            onSearch(text)
        },
        active = isActive,
        onActiveChange = {
            isActive = it
//            onClearSearch()
        },

        //внешний вид
        placeholder = { Text("Поиск по меню") },
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
            Icon(Icons.Default.Search, contentDescription = "Иконка поиска")
        },
        trailingIcon = {
            if (isActive) {
                IconButton(onClick = {
                    if (latestSearchText.isNotEmpty()) {
                        onClearSearch()
                    } else {
                        isActive = false
                    }
                }) {
                    Icon(Icons.Default.Close, contentDescription = "Закрыть")
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
                onAddToCart = onAddToCart,
                onRemoveFromCart = onRemoveFromCart,
                onCustomizeClick = onCustomizeClick,
                onToggleFavorite = onToggleFavorite,
                onMealClick = { meal ->
                    onMealClick(meal)
                    isActive = false
                },
            )
        } else {
            if (!latestSearchText.isEmpty()) {
                Text(
                    text = "Ничего не найдено :(",
                    color = Colors.White,
                    modifier = Modifier.padding(
                        Dimens.MarginStandard16
                    )
                )
            }
        }
    }
}

