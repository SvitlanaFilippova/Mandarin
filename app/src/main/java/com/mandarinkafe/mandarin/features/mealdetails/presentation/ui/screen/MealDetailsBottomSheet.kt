package com.mandarinkafe.mandarin.features.mealdetails.presentation.ui.screen

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.mandarinkafe.mandarin.features.cart.data.CartMapper.toCartItem
import com.mandarinkafe.mandarin.features.cart.presentation.components.FavoriteVariantChoiceDialog
import com.mandarinkafe.mandarin.features.cart.presentation.viewmodel.CartContract.CartEffect
import com.mandarinkafe.mandarin.features.cart.presentation.viewmodel.CartContract.CartEvent.RequestAddMeal
import com.mandarinkafe.mandarin.features.cart.presentation.viewmodel.CartContract.CartEvent.UpdateMealInCart
import com.mandarinkafe.mandarin.features.cart.presentation.viewmodel.CartViewModel
import com.mandarinkafe.mandarin.features.mealdetails.presentation.models.ReplaceOrAddData
import com.mandarinkafe.mandarin.features.mealdetails.presentation.viewmodel.MealDetailsContract.MealDetailsEffect.ShowMaxModifiersQuantity
import com.mandarinkafe.mandarin.features.mealdetails.presentation.viewmodel.MealDetailsContract.MealDetailsEffect.ShowRequiredModifiersDialog
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
    mealId: String?,
) {
    if (initItem == null && mealId == null) return
    val state by viewModel.state.collectAsState()
    LaunchedEffect(initItem, mealId) {
        viewModel.onEvent(
            MealDetailsEvent.SetInitData(
                item = initItem,
                isEditMode = isEditMode,
                mealId = mealId
            )
        )
    }

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

    var showFavoriteVariantChoiceDialog by remember { mutableStateOf(false) }
    var showRequiredModifiersDialog by remember { mutableStateOf(false) }
    var showMaxModifiersQuantity by remember { mutableStateOf(false) }
    var replaceOrAddData by remember { mutableStateOf<ReplaceOrAddData?>(null) }

    val error = state.error
    val onClose: () -> Unit = remember(sheetState, coroutineScope) {
        {
            coroutineScope.launch {
                sheetState.hide()
                onClose()
            }
        }
    }

    val customizedMeal = state.customizedMeal ?: initItem?.customizedMeal
    customizedMeal?.let {
        val isFavorite by remember(customizedMeal, favorites) {
            derivedStateOf { customizedMeal.isFavorite(favorites) }
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

        replaceOrAddData?.let { data ->
            ReplaceOrAddDialog(
                message = data.message,
                onDismiss = { replaceOrAddData = null },
                onAddNew = data.onAddNew,
                onReplace = data.onReplace
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
                    tonalElevation = Dimens.Elevation4,
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
                        onRequestAddMeal = { onCartEvent(RequestAddMeal(state.actualCartItem)) },
                        onEdit = {
                            onCartEvent(
                                UpdateMealInCart(
                                    state.actualCartItem ?: customizedMeal.toCartItem()
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
    }
    LaunchedEffect(Unit) {
        sheetState.show()
    }

    LaunchedEffect(Unit) {
        launch {
            effectFlow.collect { effect ->
                when (effect) {
                    is ShowRequiredModifiersDialog -> showRequiredModifiersDialog = true

                    is ShowMaxModifiersQuantity -> showMaxModifiersQuantity = true
                }
            }
        }
        launch {
            cartViewModel.effect.collect { effect ->
                if (effect is CartEffect.AskReplaceOrAdd) {
                    replaceOrAddData = ReplaceOrAddData(
                        message = effect.message,
                        onAddNew = { effect.onAddNew(); onClose() },
                        onReplace = { effect.onReplace(); onClose() }
                    )
                }
                if (effect is CartEffect.CloseMealDetails) {
                    onClose()
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

@Composable
private fun ReplaceOrAddDialog(
    message: String,
    onDismiss: () -> Unit,
    onAddNew: () -> Unit,
    onReplace: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Что делаем?") },
        text = { Text(message) },
        confirmButton = {
            TextButton(onClick = onReplace) { Text("Заменить ") }
        },
        dismissButton = {
            TextButton(onClick = onAddNew) { Text("Добавить ещё") }
        }
    )
}
