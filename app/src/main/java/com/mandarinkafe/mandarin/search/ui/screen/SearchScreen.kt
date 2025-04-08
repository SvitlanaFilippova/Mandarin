package com.mandarinkafe.mandarin.search.ui.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.mandarinkafe.mandarin.menu.ui.view_model.MenuContract
import com.mandarinkafe.mandarin.search.ui.view_model.SearchViewModel

@Composable
fun SearchScreen(
    viewModel: SearchViewModel = hiltViewModel(),
    onMenuEvent: (MenuContract.Event) -> Unit
) {

}
