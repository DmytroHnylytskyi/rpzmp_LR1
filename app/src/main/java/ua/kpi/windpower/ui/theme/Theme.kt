package ua.kpi.windpower.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

/**
 * Конфігурація темної кольорової схеми (основна для диспетчерських пультів).
 */
private val DarkColorScheme = darkColorScheme(
    primary = EmeraldLight,
    onPrimary = Color(0xFF003822),
    primaryContainer = DarkContainer,
    onPrimaryContainer = EmeraldLight,
    secondary = CyanAccent,
    onSecondary = Color(0xFF00363F),
    tertiary = AmberWarning,
    background = DarkBackground,
    onBackground = DarkTextPrimary,
    surface = DarkSurface,
    onSurface = DarkTextPrimary,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkTextSecondary,
    error = StormAlert,
    onError = Color.White
)

/**
 * Конфігурація світлої кольорової схеми.
 */
private val LightColorScheme = lightColorScheme(
    primary = EmeraldDark,
    onPrimary = Color.White,
    primaryContainer = LightContainer,
    onPrimaryContainer = EmeraldDark,
    secondary = CyanAccent,
    onSecondary = Color.White,
    tertiary = AmberWarning,
    background = LightBackground,
    onBackground = LightTextPrimary,
    surface = LightSurface,
    onSurface = LightTextPrimary,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = LightTextSecondary,
    error = StormAlert,
    onError = Color.White
)

/**
 * Головна тема інтерфейсу застосунку AeroGrid.
 *
 * @param darkTheme Прапорець активації темної теми (за замовчуванням визначається системою Android).
 * @param dynamicColor Прапорець використання динамічних системних кольорів Material You (вимкнено для збереження брендингу).
 * @param content Вміст інтерфейсу користувача.
 */
@Composable
fun WindPowerPlantTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}