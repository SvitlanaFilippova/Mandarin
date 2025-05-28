package com.mandarinkafe.mandarin.util.ui.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.mandarinkafe.mandarin.core.ui.models.UiError
import com.mandarinkafe.mandarin.shared.ui.view_model.SharedContract.SharedEffect
import com.mandarinkafe.mandarin.shared.ui.view_model.SharedViewModel
import com.mandarinkafe.mandarin.util.Resource
import com.mandarinkafe.mandarin.util.ui.components.LoadingScreen
import kotlinx.coroutines.flow.StateFlow

@Composable
fun <T> ResourceScreen(
    resourceFlow: StateFlow<Resource<List<T>>>,
    onRetry: () -> Unit = { },
    onCallClick: () -> Unit = { },
    errorMapper: (Resource.ErrorOther<List<T>>) -> UiError,
    sharedViewModel: SharedViewModel = hiltViewModel(),
    content: @Composable (List<T>) -> Unit
) {
    // подписка на эффекты (например, открытие BottomSheet)
    LaunchedEffect(Unit) {
        sharedViewModel.effect.collect { effect ->
            when (effect) {
                is SharedEffect.OpenMealDetailsBS -> {
                    // здесь любой код открытия BS
                }

                is SharedEffect.OnPhoneClick -> {
                    // здесь любой код открытия BS
                }
            }
        }
    }
    // собираем StateFlow
    val resourceState by resourceFlow.collectAsState(initial = Resource.Loading<List<T>>())
    // «закрепляем» в локальную переменную, чтобы была стабильная ссылка
    val resource = resourceState

    when (resource) {
        is Resource.Loading<*> -> {
            LoadingScreen()
        }

        is Resource.ErrorOther -> PlaceholderScreen(
            error = errorMapper(resource),
            onRetryClick = onRetry,
            onCallClick = onCallClick
        )

        is Resource.Success -> {
            val data: List<T> = resource.data.orEmpty()
            content(data)
        }

        else -> {}
    }
}
