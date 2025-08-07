package com.mandarinkafe.mandarin.features.mealdetails.presentation.ui.screen

import android.util.Log
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
import com.mandarinkafe.mandarin.R
import com.mandarinkafe.mandarin.core.domain.models.CartItem
import com.mandarinkafe.mandarin.core.domain.models.CustomizedMeal
import com.mandarinkafe.mandarin.core.domain.models.isCustomized
import com.mandarinkafe.mandarin.core.domain.models.isFavorite
import com.mandarinkafe.mandarin.core.presentation.theme.Colors
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.features.cart.presentation.components.FavoriteVariantChoiceDialog
import com.mandarinkafe.mandarin.features.cart.presentation.viewmodel.CartContract.CartEvent.AddToCart
import com.mandarinkafe.mandarin.features.cart.presentation.viewmodel.CartContract.CartEvent.UpdateMealInCart
import com.mandarinkafe.mandarin.features.cart.presentation.viewmodel.CartViewModel
import com.mandarinkafe.mandarin.features.mealdetails.presentation.viewmodel.MealDetailsContract.MealDetailsEffect
import com.mandarinkafe.mandarin.features.mealdetails.presentation.viewmodel.MealDetailsContract.MealDetailsEvent
import com.mandarinkafe.mandarin.features.mealdetails.presentation.viewmodel.MealDetailsViewModel
import com.mandarinkafe.mandarin.shared.ui.viewmodel.SharedContract.SharedEvent
import com.mandarinkafe.mandarin.shared.ui.viewmodel.SharedViewModel
import com.mandarinkafe.mandarin.util.presentation.ui.components.InformationDialog
import com.mandarinkafe.mandarin.util.presentation.ui.components.LoadingScreen
import com.mandarinkafe.mandarin.util.presentation.ui.screen.PlaceholderScreen
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MealDetailsBottomSheet(
    viewModel: MealDetailsViewModel = hiltViewModel(),
    sharedViewModel: SharedViewModel,
    cartViewModel: CartViewModel,
    initItem: CartItem?,
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
    val customizedMeal = state.customizedMeal ?: initItem.customizedMeal
    var showFavoriteVariantChoiceDialog by remember { mutableStateOf(false) }
    var showRequiredModifiersDialog by remember { mutableStateOf(false) }
    var showMaxModifiersQuantity by remember { mutableStateOf(false) }

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
        viewModel.onEvent(MealDetailsEvent.SetInitItem(initItem))
    }

    LaunchedEffect(Unit) {
        sheetState.show()
    }


    RequiredModifiersDialog(
        show = showRequiredModifiersDialog,
        onDismiss = { showRequiredModifiersDialog = false }
    )

    MaxModifiersDialog(
        show = showMaxModifiersQuantity,
        onDismiss = { showMaxModifiersQuantity = false }
    )

    FavoriteVariantDialog(
        show = showFavoriteVariantChoiceDialog,
        onBaseSelected = {
            onSharedEvent(SharedEvent.ToggleFavorite(meal = customizedMeal.meal))
        },
        onCustomSelected = {
            onToggleFavorite(customizedMeal)
        },
        onDismiss = { showFavoriteVariantChoiceDialog = false }
    )
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
                    customizedMeal = customizedMeal,
                    onEvent = viewModel::onEvent,
                    selectedTabIndex = state.selectedTabIndex,
                    addons = state.addons,
                    isFavorite = isFavorite,
                    isEditMode = isEditMode,
                    onClose = onClose,
                    onAddToCart = {
                        onCartEvent(
                            AddToCart(
                                initItem.copy(
                                    customizedMeal = customizedMeal,
                                    comment = state.comment
                                )
                            )
                        )
                    },
                    onEdit = {
                        Log.d(
                            "Cart DEBUG MealDetails",
                            "call onEdit, newItemCustMeal: $customizedMeal "
                        )
                        onCartEvent(
                            UpdateMealInCart(
                                newItem = initItem.copy(
                                    customizedMeal = customizedMeal,
                                    comment = state.comment
                                )
                            )
                        )
                    },
                    onToggleFavorite = {
                        if (!isFavorite && customizedMeal.isCustomized) {
                            onSharedEvent(SharedEvent.ShowFavoriteDialog(customizedMeal))
                        } else {
                            onToggleFavorite(customizedMeal)
                        }
                    },
                    comment = state.comment
                )
            }
    }

    LaunchedEffect(effectFlow) {
        effectFlow.collect { effect ->
            when (effect) {
                is MealDetailsEffect.ShowRequiredModifiersDialog -> {
                    showRequiredModifiersDialog = true
                }

                is MealDetailsEffect.ShowMaxModifiersQuantity -> {
                    showMaxModifiersQuantity = true
                }
            }
        }
    }
}

@Composable
private fun RequiredModifiersDialog(
    show: Boolean,
    onDismiss: () -> Unit,
) {
    if (show) {
        InformationDialog(
            textRes = R.string.make_mandatory_choice_before_cart,
            onDismiss = onDismiss
        )
    }
}

@Composable
private fun MaxModifiersDialog(
    show: Boolean,
    onDismiss: () -> Unit,
) {
    if (show) {
        InformationDialog(
            textRes = R.string.maximum_modifier,
            onDismiss = onDismiss
        )
    }
}

@Composable
private fun FavoriteVariantDialog(
    show: Boolean,
    onBaseSelected: () -> Unit,
    onCustomSelected: () -> Unit,
    onDismiss: () -> Unit,
) {
    if (show) {
        FavoriteVariantChoiceDialog(
            onBaseSelected = {
                onBaseSelected()
                onDismiss()
            },
            onCustomSelected = {
                onCustomSelected()
                onDismiss()
            },
            onDismiss = onDismiss
        )
    }
}
