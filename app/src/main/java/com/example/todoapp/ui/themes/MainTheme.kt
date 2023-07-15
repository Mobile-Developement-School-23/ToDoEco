package com.example.todoapp.ui.themes

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
fun MainThemePreview() {
    MainTheme(darkTheme = false) {
        ColorPalettePreview()
    }
}

@Preview
@Composable
fun ColorPalettePreview() {
    MainTheme() {
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            item {
                Box(
                    modifier = Modifier
                        .height(100.dp)
                        .fillMaxWidth()
                        .background(MaterialTheme.colors.primary)
                )
            }
            item {
                Box(
                    modifier = Modifier
                        .height(100.dp)
                        .fillMaxWidth()
                        .background(MaterialTheme.colors.primaryVariant)
                )
            }
            item {
                Box(
                    modifier = Modifier
                        .height(100.dp)
                        .fillMaxWidth()
                        .background(MaterialTheme.colors.secondary)
                )
            }
            item {
                Box(
                    modifier = Modifier
                        .height(100.dp)
                        .fillMaxWidth()
                        .background(MaterialTheme.colors.secondaryVariant)
                )
            }
            item {
                Box(
                    modifier = Modifier
                        .height(100.dp)
                        .fillMaxWidth()
                        .background(MaterialTheme.colors.background)
                )
            }
            item {
                Box(
                    modifier = Modifier
                        .height(100.dp)
                        .fillMaxWidth()
                        .background(MaterialTheme.colors.surface)
                )
            }
            item {
                Box(
                    modifier = Modifier
                        .height(100.dp)
                        .fillMaxWidth()
                        .background(MaterialTheme.colors.error)
                )
            }
            item {
                Box(
                    modifier = Modifier
                        .height(100.dp)
                        .fillMaxWidth()
                        .background(MaterialTheme.colors.onPrimary)
                )
            }
            item {
                Box(
                    modifier = Modifier
                        .height(100.dp)
                        .fillMaxWidth()
                        .background(MaterialTheme.colors.onBackground)
                )
            }
            item {
                Box(
                    modifier = Modifier
                        .height(100.dp)
                        .fillMaxWidth()
                        .background(MaterialTheme.colors.onSurface)
                )
            }
        }
    }
}