package com.coffeeshop.designsystem.common

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val CoffeeColorScheme = lightColorScheme(
    primary = DarkBrown,
    onPrimary = White,
    primaryContainer = Beige,
    onPrimaryContainer = DarkBrown,
    secondary = Secondary,
    onSecondary = White,
    secondaryContainer = Beige,
    onSecondaryContainer = DarkBrown,
    background = White,
    onBackground = DarkBrown,
    surface = White,
    onSurface = DarkBrown,
    surfaceVariant = Beige,
    onSurfaceVariant = Secondary,
)

@Composable
fun CoffeeTheme(
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = CoffeeColorScheme,
        typography = CoffeeTypography,
        content = content,
    )
}
