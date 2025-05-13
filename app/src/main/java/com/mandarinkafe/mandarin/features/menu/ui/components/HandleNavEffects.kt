package com.mandarinkafe.mandarin.features.menu.ui.components

import android.content.Context
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.core.net.toUri
import androidx.navigation.NavHostController
import com.mandarinkafe.mandarin.features.menu.ui.view_model.MenuContract
import com.mandarinkafe.mandarin.navigation.navigateToFavoritesScreen
import com.mandarinkafe.mandarin.navigation.navigateToSearchScreen
import com.mandarinkafe.mandarin.util.Constants.PHONE_NUMBER
import kotlinx.coroutines.flow.Flow

@Composable
fun HandleNavEffects(
    effectFlow: Flow<MenuContract.MenuEffect>,
    navController: NavHostController,
    context: Context
) {
    LaunchedEffect(effectFlow) {
        effectFlow.collect { effect ->
            when (effect) {
                is MenuContract.MenuEffect.ShowSnackbar -> {
                    // Пока оставляем пустым — сюда можно добавить вызов Snackbar через ScaffoldState
                }

                is MenuContract.MenuEffect.OpenSearch -> {
                    navController.navigateToSearchScreen(effect.focusSearch)
                }

                is MenuContract.MenuEffect.OpenFavorites -> {
                    navController.navigateToFavoritesScreen()
                }

                is MenuContract.MenuEffect.CallPhone -> {
                    val intent = Intent(Intent.ACTION_DIAL).apply {
                        data = PHONE_NUMBER.toUri()
                    }
                    context.startActivity(intent)
                }

                is MenuContract.MenuEffect.OpenMealDetailsBS -> {
                    // Игнорируем здесь, обработаем в другом месте
                }
            }
        }
    }
}