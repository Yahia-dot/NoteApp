package com.example.noteapp.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Light theme colors
private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF4CAF50),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFA5D6A7),
    onPrimaryContainer = Color(0xFF003300),
    secondary = Color(0xFF81C784),
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFFC8E6C9),
    onSecondaryContainer = Color(0xFF002200),
    tertiary = Color(0xFF66BB6A),
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFE8F5E9),
    onTertiaryContainer = Color(0xFF003300),
    error = Color(0xFFB00020),
    onError = Color.White,
    background = Color(0xFFF1F8E9),
    onBackground = Color(0xFF1B5E20),
    surface = Color(0xFFE8F5E9),
    onSurface = Color(0xFF1B5E20),
    surfaceVariant = Color(0xFFC8E6C9),
    onSurfaceVariant = Color(0xFF2E7D32),
    outline = Color(0xFF66BB6A)
)

// Dark theme colors
private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF81C784),
    onPrimary = Color.Black,
    primaryContainer = Color(0xFF388E3C),
    onPrimaryContainer = Color.White,
    secondary = Color(0xFF66BB6A),
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFF2E7D32),
    onSecondaryContainer = Color.White,
    tertiary = Color(0xFF4CAF50),
    onTertiary = Color.Black,
    tertiaryContainer = Color(0xFF1B5E20),
    onTertiaryContainer = Color.White,
    error = Color(0xFFCF6679),
    onError = Color.Black,
    background = Color(0xFF1B5E20),
    onBackground = Color(0xFFC8E6C9),
    surface = Color(0xFF2E7D32),
    onSurface = Color(0xFFC8E6C9),
    surfaceVariant = Color(0xFF388E3C),
    onSurfaceVariant = Color(0xFFA5D6A7),
    outline = Color(0xFF66BB6A)
)

// App typography
private val AppTypography = Typography(
    displayLarge = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 57.sp,
        lineHeight = 64.sp,
        letterSpacing = -0.25.sp
    ),
    headlineMedium = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 28.sp,
        lineHeight = 36.sp
    ),
    bodyLarge = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    labelSmall = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
)

@Composable
fun NoteAppTheme(
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
        typography = AppTypography,
        content = content
    )
}
