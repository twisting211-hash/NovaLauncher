package com.example.ui.home

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.FlashlightOn
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Watch
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.utils.HapticFeedbackUtil
import com.example.utils.SystemControlUtil
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun IosWeatherWidget(
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .shadow(12.dp, RoundedCornerShape(24.dp), ambientColor = Color.Black.copy(alpha = 0.3f))
            .clip(RoundedCornerShape(24.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF2A5298),
                        Color(0xFF1E3C72)
                    )
                )
            )
            .border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(24.dp))
            .clickable(onClick = onClick)
            .padding(14.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    Text(
                        text = "Toshkent",
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    )
                    Text(
                        text = "24°",
                        style = MaterialTheme.typography.displayMedium.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Light,
                            fontSize = 38.sp
                        )
                    )
                }
                Icon(
                    imageVector = Icons.Default.WbSunny,
                    contentDescription = "Quyoshli",
                    tint = Color(0xFFFFD54F),
                    modifier = Modifier.size(32.dp)
                )
            }

            Column {
                Text(
                    text = "Quyoshli va iliq",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Color.White.copy(alpha = 0.9f),
                        fontWeight = FontWeight.Medium,
                        fontSize = 12.sp
                    )
                )
                Text(
                    text = "Yuqori: 28° • Past: 16°",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 11.sp
                    )
                )
            }
        }
    }
}

@Composable
fun IosCalendarWidget(
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val currentDate = remember { Date() }
    val dayOfWeek = when (SimpleDateFormat("u", Locale.getDefault()).format(currentDate)) {
        "1" -> "DUSHANBA"
        "2" -> "SESHANBA"
        "3" -> "CHORSHANBA"
        "4" -> "PAYSHANBA"
        "5" -> "JUMA"
        "6" -> "SHANBA"
        else -> "YAKSHANBA"
    }
    val dayOfMonth = SimpleDateFormat("d", Locale.getDefault()).format(currentDate)

    Box(
        modifier = modifier
            .shadow(12.dp, RoundedCornerShape(24.dp), ambientColor = Color.Black.copy(alpha = 0.3f))
            .clip(RoundedCornerShape(24.dp))
            .background(Color(0xFF1C1C1E).copy(alpha = 0.85f))
            .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(24.dp))
            .clickable(onClick = onClick)
            .padding(14.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = dayOfWeek,
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = Color(0xFFFF3B30),
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        letterSpacing = 0.5.sp
                    )
                )
                Text(
                    text = dayOfMonth,
                    style = MaterialTheme.typography.displayLarge.copy(
                        color = Color.White,
                        fontWeight = FontWeight.Light,
                        fontSize = 42.sp
                    )
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(width = 3.dp, height = 28.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(Color(0xFFFF3B30))
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "NovaHome iOS Rejimi",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp
                        ),
                        maxLines = 1
                    )
                    Text(
                        text = "Bugun to'liq faol",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Color.White.copy(alpha = 0.6f),
                            fontSize = 11.sp
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun IosBatteryWidget(
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .shadow(12.dp, RoundedCornerShape(24.dp), ambientColor = Color.Black.copy(alpha = 0.3f))
            .clip(RoundedCornerShape(24.dp))
            .background(Color(0xFF1C1C1E).copy(alpha = 0.85f))
            .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(24.dp))
            .clickable(onClick = onClick)
            .padding(12.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                BatteryRingItem(Icons.Default.Smartphone, 0.85f, "85%")
                BatteryRingItem(Icons.Default.Headphones, 1.0f, "100%")
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                BatteryRingItem(Icons.Default.Watch, 0.72f, "72%")
                BatteryRingItem(Icons.Default.MusicNote, 0.60f, "60%")
            }
        }
    }
}

@Composable
private fun BatteryRingItem(
    icon: ImageVector,
    progress: Float,
    percentText: String
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.size(54.dp)
    ) {
        CircularProgressIndicator(
            progress = { 1f },
            modifier = Modifier.fillMaxSize(),
            color = Color.White.copy(alpha = 0.12f),
            strokeWidth = 4.dp
        )
        CircularProgressIndicator(
            progress = { progress },
            modifier = Modifier.fillMaxSize(),
            color = Color(0xFF34C759),
            strokeWidth = 4.dp
        )
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = percentText,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = Color.White.copy(alpha = 0.85f),
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold
                )
            )
        }
    }
}

@Composable
fun IosQuickToolsWidget(
    hapticEnabled: Boolean = true,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var isTorchActive by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .shadow(12.dp, RoundedCornerShape(24.dp), ambientColor = Color.Black.copy(alpha = 0.3f))
            .clip(RoundedCornerShape(24.dp))
            .background(Color(0xFF1C1C1E).copy(alpha = 0.85f))
            .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(24.dp))
            .padding(12.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceAround
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                IosToolButton(
                    icon = Icons.Default.FlashlightOn,
                    label = "Fonar",
                    active = isTorchActive,
                    onClick = {
                        HapticFeedbackUtil.performHaptic(context, hapticEnabled)
                        isTorchActive = SystemControlUtil.toggleFlashlight(context)
                    }
                )
                IosToolButton(
                    icon = Icons.Default.CameraAlt,
                    label = "Kamera",
                    active = false,
                    onClick = {
                        HapticFeedbackUtil.performHaptic(context, hapticEnabled)
                        SystemControlUtil.openCamera(context)
                    }
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                IosToolButton(
                    icon = Icons.Default.Calculate,
                    label = "Kalkulyator",
                    active = false,
                    onClick = {
                        HapticFeedbackUtil.performHaptic(context, hapticEnabled)
                        SystemControlUtil.openCalculator(context)
                    }
                )
                IosToolButton(
                    icon = Icons.Default.Timer,
                    label = "Taymer",
                    active = false,
                    onClick = {
                        HapticFeedbackUtil.performHaptic(context, hapticEnabled)
                        SystemControlUtil.openTimer(context)
                    }
                )
            }
        }
    }
}

@Composable
private fun IosToolButton(
    icon: ImageVector,
    label: String,
    active: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(54.dp)
            .clip(CircleShape)
            .background(if (active) Color(0xFF007AFF) else Color.White.copy(alpha = 0.15f))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (active) Color.White else Color.White.copy(alpha = 0.9f),
            modifier = Modifier.size(24.dp)
        )
    }
}
