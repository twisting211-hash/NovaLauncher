package com.example.ui.theme

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.example.R

data class IosWallpaperItem(
    val id: String,
    val title: String,
    val isDrawable: Boolean,
    val drawableRes: Int? = null,
    val previewColors: List<Color> = emptyList()
)

object IosWallpapers {
    val PRESETS = listOf(
        IosWallpaperItem(
            id = "ios_18_dark",
            title = "iOS 18 Dark Nebula",
            isDrawable = true,
            drawableRes = R.drawable.ios_wallpaper_default,
            previewColors = listOf(Color(0xFF0D1B2A), Color(0xFF4A148C), Color(0xFF00E5FF))
        ),
        IosWallpaperItem(
            id = "ios_17_light",
            title = "iOS 17 Luminous Pastel",
            isDrawable = true,
            drawableRes = R.drawable.ios_wallpaper_light,
            previewColors = listOf(Color(0xFFFF8A80), Color(0xFF80D8FF), Color(0xFFEA80FC))
        ),
        IosWallpaperItem(
            id = "ios_cosmic_neon",
            title = "iOS Kosmik Neon",
            isDrawable = false,
            previewColors = listOf(Color(0xFF050515), Color(0xFF240046), Color(0xFF7B2CBF), Color(0xFFFF007F))
        ),
        IosWallpaperItem(
            id = "ios_sunset_glow",
            title = "iOS Quyosh Botishi",
            isDrawable = false,
            previewColors = listOf(Color(0xFF1F0C27), Color(0xFF6B114D), Color(0xFFE85D04), Color(0xFFFFBA08))
        ),
        IosWallpaperItem(
            id = "ios_emerald_aurora",
            title = "iOS Zumrad Aurora",
            isDrawable = false,
            previewColors = listOf(Color(0xFF03191E), Color(0xFF0A3A40), Color(0xFF119DA4), Color(0xFF04E762))
        ),
        IosWallpaperItem(
            id = "ios_dark_minimal",
            title = "iOS OLED Midnight",
            isDrawable = false,
            previewColors = listOf(Color(0xFF000000), Color(0xFF141414), Color(0xFF222226), Color(0xFF0F2027))
        )
    )
}

@Composable
fun IosWallpaperView(
    presetId: String,
    modifier: Modifier = Modifier
) {
    val currentPreset = IosWallpapers.PRESETS.find { it.id == presetId } ?: IosWallpapers.PRESETS.first()

    Box(modifier = modifier.fillMaxSize()) {
        if (currentPreset.isDrawable && currentPreset.drawableRes != null) {
            Image(
                painter = painterResource(id = currentPreset.drawableRes),
                contentDescription = currentPreset.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            // Procedural ultra-smooth Apple gradient
            when (presetId) {
                "ios_cosmic_neon" -> {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        drawRect(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    Color(0xFF050515),
                                    Color(0xFF1A0033),
                                    Color(0xFF3C096C),
                                    Color(0xFF090014)
                                )
                            )
                        )
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(Color(0xFFFF007F).copy(alpha = 0.55f), Color.Transparent),
                                center = Offset(size.width * 0.85f, size.height * 0.25f),
                                radius = size.width * 0.9f
                            )
                        )
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(Color(0xFF00F0FF).copy(alpha = 0.45f), Color.Transparent),
                                center = Offset(size.width * 0.2f, size.height * 0.75f),
                                radius = size.width * 0.85f
                            )
                        )
                    }
                }
                "ios_sunset_glow" -> {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        drawRect(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFF1A0A26),
                                    Color(0xFF491136),
                                    Color(0xFF9E2A2B),
                                    Color(0xFFD4483B)
                                )
                            )
                        )
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(Color(0xFFFFBA08).copy(alpha = 0.6f), Color.Transparent),
                                center = Offset(size.width * 0.5f, size.height * 0.6f),
                                radius = size.width * 0.8f
                            )
                        )
                    }
                }
                "ios_emerald_aurora" -> {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        drawRect(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    Color(0xFF021B1A),
                                    Color(0xFF063836),
                                    Color(0xFF0B525B),
                                    Color(0xFF021B1A)
                                )
                            )
                        )
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(Color(0xFF00FF87).copy(alpha = 0.4f), Color.Transparent),
                                center = Offset(size.width * 0.3f, size.height * 0.35f),
                                radius = size.width * 0.9f
                            )
                        )
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(Color(0xFF60EFFF).copy(alpha = 0.35f), Color.Transparent),
                                center = Offset(size.width * 0.75f, size.height * 0.7f),
                                radius = size.width * 0.8f
                            )
                        )
                    }
                }
                else -> {
                    // Dark minimal OLED
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        drawRect(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    Color(0xFF090A0F),
                                    Color(0xFF151821),
                                    Color(0xFF0D0E12),
                                    Color(0xFF000000)
                                )
                            )
                        )
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(Color(0xFF3A4B75).copy(alpha = 0.25f), Color.Transparent),
                                center = Offset(size.width * 0.5f, size.height * 0.3f),
                                radius = size.width * 0.7f
                            )
                        )
                    }
                }
            }
        }
    }
}
