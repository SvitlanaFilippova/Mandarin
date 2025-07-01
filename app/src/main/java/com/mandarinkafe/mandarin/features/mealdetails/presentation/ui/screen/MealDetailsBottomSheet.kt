package com.mandarinkafe.mandarin.features.mealdetails.presentation.ui.screen

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.mandarinkafe.mandarin.core.domain.models.CustomizedMeal
import com.mandarinkafe.mandarin.core.domain.models.extensions.isCustomized
import com.mandarinkafe.mandarin.core.domain.models.extensions.isFavorite
import com.mandarinkafe.mandarin.core.presentation.theme.Colors
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.features.cart.presentation.components.FavoriteVariantChoiceDialog
import com.mandarinkafe.mandarin.features.cart.presentation.view_model.CartContract.CartEvent.AddToCart
import com.mandarinkafe.mandarin.features.cart.presentation.view_model.CartContract.CartEvent.ReplaceMealInCart
import com.mandarinkafe.mandarin.features.cart.presentation.view_model.CartViewModel
import com.mandarinkafe.mandarin.features.mealdetails.presentation.ui.components.RequiredModifiersDialog
import com.mandarinkafe.mandarin.features.mealdetails.presentation.view_model.MealDetailsContract.MealDetailsEffect
import com.mandarinkafe.mandarin.features.mealdetails.presentation.view_model.MealDetailsContract.MealDetailsEvent
import com.mandarinkafe.mandarin.features.mealdetails.presentation.view_model.MealDetailsViewModel
import com.mandarinkafe.mandarin.shared.ui.view_model.SharedContract.SharedEvent
import com.mandarinkafe.mandarin.shared.ui.view_model.SharedViewModel
import com.mandarinkafe.mandarin.util.presentation.ui.components.LoadingScreen
import com.mandarinkafe.mandarin.util.presentation.ui.screen.PlaceholderScreen
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

    val state by viewModel.state.collectAsState()

    val coroutineScope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true,
    )
    val favorites by sharedViewModel.favoritesItemsFlow.collectAsState()
    val onSharedEvent = sharedViewModel::onEvent
    val onCartEvent = cartViewModel::onEvent
    val effectFlow = viewModel.effect
    val onToggleFavorite = { item: CustomizedMeal ->
        onSharedEvent(SharedEvent.ToggleFavorite(item = item))
    }
    val customizedMeal = state.customizedMeal ?: initItem
    var showFavoriteVariantChoiceDialog by remember { mutableStateOf(false) }
    var showRequiredModifiersDialog by remember { mutableStateOf(false) }
    val error = state.error
    val onClose: () -> Unit = remember(sheetState, coroutineScope) {
        {
            coroutineScope.launch {
                sheetState.hide()
                onClose()
            }
        }
    }
    val isFavorite by remember(customizedMeal, favorites) {
        derivedStateOf { customizedMeal.isFavorite(favorites) }
    }
    LaunchedEffect(Unit) {
        viewModel.onEvent(MealDetailsEvent.SetItem(initItem))
    }

    LaunchedEffect(Unit) {
        sheetState.show()
    }


    if (showRequiredModifiersDialog) {
        RequiredModifiersDialog(onDismiss = {
            showRequiredModifiersDialog = false
        })
    }
    if (showFavoriteVariantChoiceDialog) {
        FavoriteVariantChoiceDialog(
            onBaseSelected = {
                onSharedEvent(SharedEvent.ToggleFavorite(meal = customizedMeal.meal))
                showFavoriteVariantChoiceDialog = false
            },
            onCustomSelected = {
                onToggleFavorite(customizedMeal)
                showFavoriteVariantChoiceDialog = false
            },
            onDismiss = { showFavoriteVariantChoiceDialog = false }
        )
    }

    when {
        state.isLoading -> LoadingScreen()
        error != null -> PlaceholderScreen(
            error = error,
            onCallClick = { onSharedEvent(SharedEvent.OnPhoneClick) },
        )
        else ->
            ModalBottomSheet(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = Dimens.BSMarginForStatusBar40),
                onDismissRequest = onClose,
                sheetState = sheetState,
                containerColor = Colors.AppBlack,
                tonalElevation = Dimens.Elevation2,
                scrimColor = Colors.LightGreyTransparent75,
                ) {
                MealDetailsContentScreen(
                    state = state,
                    initItem = initItem,
                    onEvent = viewModel::onEvent,
                    onAddToCart = { onCartEvent(AddToCart(customizedMeal)) },
                    onClose = onClose,
                    onToggleFavorite = {
                        if (!isFavorite && customizedMeal.isCustomized()) {
                            onSharedEvent(SharedEvent.ShowFavoriteDialog(customizedMeal))
                        } else {
                            onToggleFavorite(customizedMeal)
                        }
                    },
                    isFavorite = isFavorite,
                    isEditMode = isEditMode,
                    onEdit = {
                        onCartEvent(
                            ReplaceMealInCart(
                                newItem = customizedMeal,
                                oldItem = initItem
                            )
                        )
                    },
                )
            }
    }

    LaunchedEffect(effectFlow) {
        effectFlow.collect { effect ->
            when (effect) {
                is MealDetailsEffect.ShowRequiredModifiersDialog -> {
                    showRequiredModifiersDialog = true
                }
            }
        }
    }
}




