package com.example.todoapp.ui.themes

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.colorResource
import com.example.todoapp.R

@Composable
fun MainTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val lightColorPalette = lightColors(
        primary = colorResource(id = R.color.night2_sneaky_Sesame),
        primaryVariant = colorResource(id = R.color.godzilla),
        secondary = colorResource(id = R.color.cave_Man),
        secondaryVariant = colorResource(id = R.color.godzilla),
        background = colorResource(id = R.color.light_cave_Man),
        surface = colorResource(id = R.color.light_rampart),
        error = colorResource(id = R.color.red),
        onPrimary = colorResource(id = R.color.lightBlack),
        onBackground = colorResource(id = R.color.dayDivider),
        onSurface = colorResource(id = R.color.godzilla),
    )

    val darkColorPalette = darkColors(
        primary = colorResource(id = R.color.night_sneaky_Sesame),
        primaryVariant = colorResource(id = R.color.dark_godzilla),
        secondary = colorResource(id = R.color.dark_cave_Man),
        secondaryVariant = colorResource(id = R.color.godzilla),
        background = colorResource(id = R.color.cave_Man),
        surface = colorResource(id = R.color.dark_rampart),
        error = colorResource(id = R.color.red),
        onPrimary = colorResource(id = R.color.light_rampart),
        onBackground = colorResource(id = R.color.night_background),
        onSurface = colorResource(id = R.color.godzilla),
    )

    val colors = when {
        darkTheme -> darkColorPalette
        else -> lightColorPalette
    }

    MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}