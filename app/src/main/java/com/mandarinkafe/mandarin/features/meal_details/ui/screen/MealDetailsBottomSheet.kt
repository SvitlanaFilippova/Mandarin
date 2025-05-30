package com.mandarinkafe.mandarin.features.meal_details.ui.screen

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.mandarinkafe.mandarin.core.domain.models.CustomizedMeal
import com.mandarinkafe.mandarin.core.ui.theme.Colors
import com.mandarinkafe.mandarin.core.ui.theme.Dimens
import com.mandarinkafe.mandarin.features.meal_details.ui.view_model.MealDetailsContract.MealDetailsEvent
import com.mandarinkafe.mandarin.features.meal_details.ui.view_model.MealDetailsViewModel
import com.mandarinkafe.mandarin.shared.cart.ui.view_model.CartContract.CartEvent.AddToCart
import com.mandarinkafe.mandarin.shared.cart.ui.view_model.CartContract.CartEvent.ReplaceMealInCart
import com.mandarinkafe.mandarin.shared.cart.ui.view_model.CartViewModel
import com.mandarinkafe.mandarin.shared.ui.view_model.SharedContract.SharedEvent
import com.mandarinkafe.mandarin.shared.ui.view_model.SharedViewModel
import com.mandarinkafe.mandarin.util.ui.components.LoadingScreen
import com.mandarinkafe.mandarin.util.ui.screen.PlaceholderScreen
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MealDetailsBottomSheet(
    viewModel: MealDetailsViewModel = hiltViewModel(),
    sharedViewModel: SharedViewModel,
    cartViewModel: CartViewModel,
    initItem: CustomizedMeal?,
    onClose: () -> Unit,
    isEditMode: Boolean,
) {
    if (initItem == null) return
    LaunchedEffect(Unit) {
        viewModel.onEvent(MealDetailsEvent.SetItem(initItem))
    }
    val state by viewModel.state.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true,
    )
    val favorites by sharedViewModel.favoritesItemsFlow.collectAsState()

    val onSharedEvent = sharedViewModel::onEvent
    val onCartEvent = cartViewModel::onEvent

    LaunchedEffect(Unit) {
        sheetState.show()
    }

    val onClose: () -> Unit = remember(sheetState, coroutineScope) {
        {
            coroutineScope.launch {
                sheetState.hide()
                onClose()
            }
        }
    }
    val error = state.error
    when {
        state.isLoading -> LoadingScreen()
        error != null -> PlaceholderScreen(error = error)
        else ->
            ModalBottomSheet(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = Dimens.BSMarginForStatusBar40),
                onDismissRequest = onClose,
                sheetState = sheetState,
                containerColor = Colors.AppBlack,
                tonalElevation = Dimens.Elevation2,
                scrimColor = Colors.GreyTransparent75,

                ) {
                MealDetailsContentScreen(
                    state = state,
                    initItem = initItem,
                    favorites = favorites,
                    onEvent = viewModel::onEvent,
                    onAddToCart = { item -> onCartEvent(AddToCart(item)) },
                    onClose = onClose,
                    onToggleFavorite = { item ->
                        onSharedEvent(
                            SharedEvent.ToggleFavorite(
                                item = item
                            )
                        )
                    },
                    isEditMode = isEditMode,
                    onEdit = { item ->
                        onCartEvent(
                            ReplaceMealInCart(
                                newItem = item,
                                oldItem = initItem
                            )
                        )
                    },
                )
            }
    }
}
