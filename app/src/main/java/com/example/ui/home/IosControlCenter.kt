package com.example.ui.home

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AirplanemodeActive
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.FlashlightOn
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.NetworkCell
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.utils.HapticFeedbackUtil
import com.example.utils.SystemControlUtil

@Composable
fun IosControlCenter(
    onClose: () -> Unit,
    onOpenSettings: () -> Unit,
    onToggleTheme: () -> Unit,
    isDarkTheme: Boolean,
    hapticEnabled: Boolean = true,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var isTorchOn by remember { mutableStateOf(SystemControlUtil.isFlashlightOn()) }
    var volume by remember { mutableFloatStateOf(SystemControlUtil.getVolumePercent(context)) }
    var brightness by remember { mutableFloatStateOf(0.75f) }
    var isPlaying by remember { mutableStateOf(false) }

    var wifiActive by remember { mutableStateOf(true) }
    var bluetoothActive by remember { mutableStateOf(true) }
    var cellularActive by remember { mutableStateOf(true) }
    var airplaneActive by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.65f))
            .pointerInput(Unit) {
                detectVerticalDragGestures { _, dragAmount ->
                    if (dragAmount < -30) {
                        onClose()
                    }
                }
            }
            .statusBarsPadding()
            .navigationBarsPadding()
            .testTag("ios_control_center")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Boshqaruv Markazi",
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                )

                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.2f))
                        .clickable {
                            HapticFeedbackUtil.performHaptic(context, hapticEnabled)
                            onClose()
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Yopish",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // Middle Grid
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Row 1: Connectivity (2x2) and Media Player (2x2)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Connectivity Tile
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(156.dp)
                            .shadow(8.dp, RoundedCornerShape(26.dp))
                            .clip(RoundedCornerShape(26.dp))
                            .background(Color(0xFF1C1C1E).copy(alpha = 0.8f))
                            .border(1.dp, Color.White.copy(alpha = 0.12f), RoundedCornerShape(26.dp))
                            .padding(14.dp)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                ControlCenterRoundButton(
                                    icon = Icons.Default.AirplanemodeActive,
                                    active = airplaneActive,
                                    activeColor = Color(0xFFFF9500),
                                    onClick = {
                                        HapticFeedbackUtil.performHaptic(context, hapticEnabled)
                                        airplaneActive = !airplaneActive
                                        SystemControlUtil.openAirplaneModeSettings(context)
                                    }
                                )
                                ControlCenterRoundButton(
                                    icon = Icons.Default.NetworkCell,
                                    active = cellularActive,
                                    activeColor = Color(0xFF34C759),
                                    onClick = {
                                        HapticFeedbackUtil.performHaptic(context, hapticEnabled)
                                        cellularActive = !cellularActive
                                        SystemControlUtil.openDataUsageSettings(context)
                                    }
                                )
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                ControlCenterRoundButton(
                                    icon = Icons.Default.Wifi,
                                    active = wifiActive,
                                    activeColor = Color(0xFF007AFF),
                                    onClick = {
                                        HapticFeedbackUtil.performHaptic(context, hapticEnabled)
                                        wifiActive = !wifiActive
                                        SystemControlUtil.openWifiSettings(context)
                                    }
                                )
                                ControlCenterRoundButton(
                                    icon = Icons.Default.Bluetooth,
                                    active = bluetoothActive,
                                    activeColor = Color(0xFF007AFF),
                                    onClick = {
                                        HapticFeedbackUtil.performHaptic(context, hapticEnabled)
                                        bluetoothActive = !bluetoothActive
                                        SystemControlUtil.openBluetoothSettings(context)
                                    }
                                )
                            }
                        }
                    }

                    // Media Player Tile
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(156.dp)
                            .shadow(8.dp, RoundedCornerShape(26.dp))
                            .clip(RoundedCornerShape(26.dp))
                            .background(Color(0xFF1C1C1E).copy(alpha = 0.8f))
                            .border(1.dp, Color.White.copy(alpha = 0.12f), RoundedCornerShape(26.dp))
                            .padding(14.dp)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(34.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color(0xFFFF2D55)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.MusicNote,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = "Blinding Lights",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold
                                        ),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = "The Weeknd",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = Color.White.copy(alpha = 0.6f),
                                            fontSize = 10.sp
                                        ),
                                        maxLines = 1
                                    )
                                }
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                IconButton(
                                    onClick = {
                                        HapticFeedbackUtil.performHaptic(context, hapticEnabled)
                                        isPlaying = !isPlaying
                                    }
                                ) {
                                    Icon(
                                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                        contentDescription = "Ijro",
                                        tint = Color.White,
                                        modifier = Modifier.size(32.dp)
                                    )
                                }
                                IconButton(
                                    onClick = {
                                        HapticFeedbackUtil.performHaptic(context, hapticEnabled)
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.SkipNext,
                                        contentDescription = "Keyingisi",
                                        tint = Color.White,
                                        modifier = Modifier.size(28.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // Row 2: Sliders (Brightness & Volume)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Brightness vertical slider card
                    IosVerticalSliderCard(
                        icon = Icons.Default.LightMode,
                        label = "Yorug'lik",
                        value = brightness,
                        onValueChange = { brightness = it },
                        modifier = Modifier.weight(1f)
                    )

                    // Volume vertical slider card
                    IosVerticalSliderCard(
                        icon = Icons.Default.VolumeUp,
                        label = "Ovoz",
                        value = volume,
                        onValueChange = {
                            volume = it
                            SystemControlUtil.setVolumePercent(context, it)
                        },
                        modifier = Modifier.weight(1f)
                    )
                }

                // Row 3: 4 Action Buttons (Torch, Camera, Calculator, Dark Mode)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Flashlight
                    ControlCenterSquareTile(
                        icon = Icons.Default.FlashlightOn,
                        label = "Chiroq",
                        active = isTorchOn,
                        onClick = {
                            HapticFeedbackUtil.performHaptic(context, hapticEnabled)
                            isTorchOn = SystemControlUtil.toggleFlashlight(context)
                        }
                    )

                    // Camera
                    ControlCenterSquareTile(
                        icon = Icons.Default.CameraAlt,
                        label = "Kamera",
                        active = false,
                        onClick = {
                            HapticFeedbackUtil.performHaptic(context, hapticEnabled)
                            SystemControlUtil.openCamera(context)
                            onClose()
                        }
                    )

                    // Calculator
                    ControlCenterSquareTile(
                        icon = Icons.Default.Calculate,
                        label = "Kalkulyator",
                        active = false,
                        onClick = {
                            HapticFeedbackUtil.performHaptic(context, hapticEnabled)
                            SystemControlUtil.openCalculator(context)
                            onClose()
                        }
                    )

                    // Dark Mode Toggle
                    ControlCenterSquareTile(
                        icon = Icons.Default.DarkMode,
                        label = "Mavzu",
                        active = isDarkTheme,
                        onClick = {
                            HapticFeedbackUtil.performHaptic(context, hapticEnabled)
                            onToggleTheme()
                        }
                    )
                }
            }

            // Bottom Settings Action
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF1C1C1E).copy(alpha = 0.8f))
                    .clickable {
                        HapticFeedbackUtil.performHaptic(context, hapticEnabled)
                        onClose()
                        onOpenSettings()
                    }
                    .padding(vertical = 12.dp, horizontal = 16.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Barcha Launcher Sozlamalari",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold
                    )
                )
            }
        }
    }
}

@Composable
private fun ControlCenterRoundButton(
    icon: ImageVector,
    active: Boolean,
    activeColor: Color,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(54.dp)
            .clip(CircleShape)
            .background(if (active) activeColor else Color.White.copy(alpha = 0.15f))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(24.dp)
        )
    }
}

@Composable
private fun IosVerticalSliderCard(
    icon: ImageVector,
    label: String,
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(130.dp)
            .shadow(8.dp, RoundedCornerShape(24.dp))
            .clip(RoundedCornerShape(24.dp))
            .background(Color(0xFF1C1C1E).copy(alpha = 0.8f))
            .border(1.dp, Color.White.copy(alpha = 0.12f), RoundedCornerShape(24.dp))
            .padding(vertical = 12.dp, horizontal = 14.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "${(value * 100).toInt()}%",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = Color.White.copy(alpha = 0.8f),
                    fontWeight = FontWeight.Bold
                )
            )

            Slider(
                value = value,
                onValueChange = onValueChange,
                colors = SliderDefaults.colors(
                    thumbColor = Color.White,
                    activeTrackColor = Color.White,
                    inactiveTrackColor = Color.White.copy(alpha = 0.2f)
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = label,
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
private fun ControlCenterSquareTile(
    icon: ImageVector,
    label: String,
    active: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(72.dp)
            .shadow(8.dp, RoundedCornerShape(20.dp))
            .clip(RoundedCornerShape(20.dp))
            .background(if (active) Color(0xFF007AFF) else Color(0xFF1C1C1E).copy(alpha = 0.8f))
            .border(1.dp, Color.White.copy(alpha = 0.12f), RoundedCornerShape(20.dp))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = Color.White.copy(alpha = 0.9f),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium
                )
            )
        }
    }
}
