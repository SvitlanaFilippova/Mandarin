package com.mandarinkafe.mandarin.features.mealdetails.presentation.ui.screen


import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import com.mandarinkafe.mandarin.MR
import com.mandarinkafe.mandarin.core.domain.models.CartItem
import com.mandarinkafe.mandarin.core.domain.models.CustomizedMeal
import com.mandarinkafe.mandarin.core.domain.models.isCustomized
import com.mandarinkafe.mandarin.core.domain.models.isFavorite
import com.mandarinkafe.mandarin.features.cart.domain.Mapper.toCartItem
import com.mandarinkafe.mandarin.features.cart.presentation.components.FavoriteVariantChoiceDialog
import com.mandarinkafe.mandarin.features.mealdetails.presentation.models.ReplaceOrAddData
import com.mandarinkafe.mandarin.features.mealdetails.presentation.viewmodel.MealDetailsContract.MealDetailsEffect.AskReplaceOrAdd
import com.mandarinkafe.mandarin.features.mealdetails.presentation.viewmodel.MealDetailsContract.MealDetailsEffect.CloseAndShowMessage
import com.mandarinkafe.mandarin.features.mealdetails.presentation.viewmodel.MealDetailsContract.MealDetailsEffect.ShowMaxModifiersQuantity
import com.mandarinkafe.mandarin.features.mealdetails.presentation.viewmodel.MealDetailsContract.MealDetailsEffect.ShowRequiredModifiersDialog
import com.mandarinkafe.mandarin.features.mealdetails.presentation.viewmodel.MealDetailsContract.MealDetailsEvent
import com.mandarinkafe.mandarin.features.mealdetails.presentation.viewmodel.MealDetailsContract.MealDetailsEvent.EditMealInCart
import com.mandarinkafe.mandarin.features.mealdetails.presentation.viewmodel.MealDetailsContract.MealDetailsEvent.TryAddMeal
import com.mandarinkafe.mandarin.shared.presentation.viewmodel.SharedContract
import com.mandarinkafe.mandarin.shared.presentation.viewmodel.SharedViewModel
import com.mandarinkafe.mandarin.shared.presentation.viewmodel.rememberMealDetailsViewModel
import com.mandarinkafe.mandarin.util.presentation.ui.SimpleBottomSheet
import com.mandarinkafe.mandarin.util.presentation.ui.components.InformationDialog
import com.mandarinkafe.mandarin.util.presentation.ui.components.LoadingScreen
import com.mandarinkafe.mandarin.util.presentation.ui.screen.PlaceholderScreen
import dev.icerock.moko.resources.compose.stringResource
import kotlinx.coroutines.launch

@Composable
fun MealDetailsBottomSheet(
    sharedViewModel: SharedViewModel,
    mealId: String?,
    initItem: CartItem?,
    isEditMode: Boolean,
    onClose: () -> Unit,
) {
    val viewModel = rememberMealDetailsViewModel()

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
    val favorites by sharedViewModel.favoritesItemsFlow.collectAsState()

    val onSharedEvent = sharedViewModel::onEvent
    val onEvent = viewModel::onEvent
    val effectFlow = viewModel.effect

    val onToggleFavorite = { item: CustomizedMeal ->
        onSharedEvent(SharedContract.SharedEvent.ToggleFavorite(item = item))
    }

    var showFavoriteVariantChoiceDialog by remember { mutableStateOf(false) }
    var showRequiredModifiersDialog by remember { mutableStateOf(false) }
    var showMaxModifiersQuantity by remember { mutableStateOf(false) }
    var replaceOrAddData by remember { mutableStateOf<ReplaceOrAddData?>(null) }

    val error = state.error
    val customizedMeal = state.customizedMeal ?: initItem?.customizedMeal

    var showSheet by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        showSheet = true
    }

    customizedMeal?.let {
        val isFavorite by remember(customizedMeal, favorites) {
            derivedStateOf { customizedMeal.isFavorite(favorites) }
        }

        // Диалоги
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
                onSharedEvent(SharedContract.SharedEvent.ToggleFavorite(meal = customizedMeal.meal))
            },
            onCustomSelected = {
                onToggleFavorite(customizedMeal)
            },
            onDismiss = { showFavoriteVariantChoiceDialog = false }
        )

        replaceOrAddData?.let { data ->
            ReplaceOrAddDialog(
                message = stringResource(data.messageRes),
                onDismiss = { replaceOrAddData = null },
                onAddNew = data.onAddNew,
                onReplace = data.onReplace
            )
        }

        when {
            state.isLoading -> LoadingScreen()
            error != null -> PlaceholderScreen(
                error = error,
                onCallClick = { onSharedEvent(SharedContract.SharedEvent.OnPhoneClick) },
            )
            else -> SimpleBottomSheet(
                visible = showSheet,
                onDismiss = {
                    showSheet = false
                    coroutineScope.launch {
                        // ждём завершения анимации, если нужно
                        onClose()
                    }
                }
            ) {
                MealDetailsContentScreen(
                    customizedMeal = customizedMeal,
                    onEvent = viewModel::onEvent,
                    selectedTabIndex = state.selectedTabIndex,
                    addons = state.addons,
                    isFavorite = isFavorite,
                    isEditMode = isEditMode,
                    onClose = onClose,
                    onRequestAddMeal = { onEvent(TryAddMeal(state.actualCartItem)) },
                    onEdit = {
                        onEvent(
                            EditMealInCart(
                                state.actualCartItem ?: customizedMeal.toCartItem()
                            )
                        )
                    },
                    onToggleFavorite = {
                        if (!isFavorite && customizedMeal.isCustomized) {
                            onSharedEvent(SharedContract.SharedEvent.ShowFavoriteDialog(customizedMeal))
                        } else {
                            onToggleFavorite(customizedMeal)
                        }
                    },
                    comment = state.comment
                )
            }
        }
    }

    // Эффекты
    LaunchedEffect(Unit) {
        effectFlow.collect { effect ->
            when (effect) {
                is ShowRequiredModifiersDialog -> showRequiredModifiersDialog = true
                is ShowMaxModifiersQuantity -> showMaxModifiersQuantity = true
                is AskReplaceOrAdd -> {
                    replaceOrAddData = ReplaceOrAddData(
                        messageRes = effect.message,
                        onAddNew = { effect.onAddNew() },
                        onReplace = { effect.onReplace() }
                    )
                }

                is CloseAndShowMessage -> {
                    effect.message?.let {
                        onSharedEvent(
                            SharedContract.SharedEvent.ShowSnackbar(
                                messageRes = effect.message,
                                showToCartButton = !state.isEditMode
                            )
                        )
                    }
                    onClose()
                }
            }
        }
    }
}

/* ---------- Вспомогательные диалоги ---------- */

@Composable
private fun RequiredModifiersDialog(show: Boolean, onDismiss: () -> Unit) {
    if (show) {
        InformationDialog(
            textRes = MR.strings.make_mandatory_choice_before_cart,
            onDismiss = onDismiss
        )
    }
}

@Composable
private fun MaxModifiersDialog(show: Boolean, onDismiss: () -> Unit) {
    if (show) {
        InformationDialog(
            textRes = MR.strings.maximum_modifier,
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
        title = { Text(stringResource(MR.strings.replace_or_add_title)) },
        text = { Text(message) },
        confirmButton = {
            TextButton(onClick = onReplace) { Text(stringResource(MR.strings.replace_button)) }
        },
        dismissButton = {
            TextButton(onClick = onAddNew) { Text(stringResource(MR.strings.add_one_more_button)) }
        }
    )
}

