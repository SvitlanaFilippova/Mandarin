import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.mandarinkafe.mandarin.core.presentation.theme.Colors

@Composable
fun MandarinTheme(
    content: @Composable () -> Unit
) {
    val colorScheme =
        darkColorScheme(
            primary = Colors.Orange,
            background = Colors.AppBlack,
            onPrimary = Colors.White
        )

    val systemUiController = rememberSystemUiController()
    SideEffect {
        systemUiController.setStatusBarColor(
            color = Colors.AppBlack,
            darkIcons = false
        )
        systemUiController.setNavigationBarColor(
            color = Colors.AppBlack,
            darkIcons = false
        )
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = MaterialTheme.typography,
        content = content
    )
}
