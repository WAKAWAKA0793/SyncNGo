package com.example.tripshare.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val LightColorScheme = lightColorScheme(
    primary = Blue700,
    onPrimary = White,
    primaryContainer = Blue100,
    onPrimaryContainer = Blue900,

    secondary = LightBlue700,
    onSecondary = White,
    secondaryContainer = LightBlue100,
    onSecondaryContainer = LightBlue900,

    tertiary = Cyan700,
    onTertiary = White,
    tertiaryContainer = Cyan100,
    onTertiaryContainer = Cyan900,

    background = Grey50,
    onBackground = Grey900,

    surface = White,
    onSurface = Grey900,
    surfaceVariant = BlueGrey50,
    onSurfaceVariant = BlueGrey800,

    outline = Grey300,
    outlineVariant = Grey200,

    error = ErrorMain,
    onError = White,
    errorContainer = ErrorLight,
    onErrorContainer = ErrorDark
)

private val DarkColorScheme = darkColorScheme(
    primary = Blue300,
    onPrimary = Blue900,
    primaryContainer = Blue800,
    onPrimaryContainer = Blue50,

    secondary = LightBlue300,
    onSecondary = LightBlue900,
    secondaryContainer = LightBlue800,
    onSecondaryContainer = LightBlue50,

    tertiary = Cyan300,
    onTertiary = Cyan900,
    tertiaryContainer = Cyan800,
    onTertiaryContainer = Cyan50,

    background = Grey900,
    onBackground = Grey50,

    surface = Grey900,
    onSurface = Grey50,
    surfaceVariant = BlueGrey800,
    onSurfaceVariant = BlueGrey100,

    outline = Grey700,
    outlineVariant = Grey800,

    error = ErrorLight,
    onError = Grey900,
    errorContainer = ErrorDark,
    onErrorContainer = ErrorLight
)

@Composable
fun TripShareTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // If you don't want dynamic color, set this to false.
    dynamicColor: Boolean = true,
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
