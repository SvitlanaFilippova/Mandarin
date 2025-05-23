package com.mandarinkafe.mandarin.features.menu.ui.components

import android.content.Context
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.core.net.toUri
import androidx.navigation.NavHostController
import com.mandarinkafe.mandarin.features.menu.ui.view_model.MenuContract
import com.mandarinkafe.mandarin.navigation.navigateToSearchScreen
import com.mandarinkafe.mandarin.util.Constants.PHONE_NUMBER_DEFAULT
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
                is MenuContract.MenuEffect.OpenSearch -> {
                    navController.navigateToSearchScreen(effect.focusSearch)
                }

                is MenuContract.MenuEffect.CallPhone -> {
                    val intent = Intent(Intent.ACTION_DIAL).apply {
                        data = PHONE_NUMBER_DEFAULT.toUri()
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