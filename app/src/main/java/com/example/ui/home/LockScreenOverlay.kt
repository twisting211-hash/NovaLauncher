package com.example.ui.home

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.BatteryFull
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.FlashlightOn
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.settings.PinAuthDialog
import com.example.utils.HapticFeedbackUtil
import com.example.utils.SystemControlUtil
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt

@Composable
fun LockScreenOverlay(
    isLocked: Boolean,
    onUnlock: () -> Unit,
    pinCode: String,
    hapticEnabled: Boolean,
    clockStyle: String = "ios_bold"
) {
    if (!isLocked) return

    val context = LocalContext.current

    // Real-time time and date
    var currentTime by remember { mutableStateOf("") }
    var currentDate by remember { mutableStateOf("") }
    var batteryLevel by remember { mutableIntStateOf(100) }
    var isCharging by remember { mutableStateOf(false) }
    var isFlashlightOn by remember { mutableStateOf(SystemControlUtil.isFlashlightOn()) }
    var isPinAuthOpen by remember { mutableStateOf(false) }

    // Swipe offset for interactive drag to unlock
    var dragOffsetY by remember { mutableFloatStateOf(0f) }

    // Time ticker
    LaunchedEffect(Unit) {
        while (true) {
            val now = Date()
            currentTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(now)
            currentDate = SimpleDateFormat("EEEE, d-MMMM", Locale("uz")).format(now).replaceFirstChar { it.uppercase() }
            delay(1000)
        }
    }

    // Battery receiver
    DisposableEffect(Unit) {
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(c: Context?, intent: Intent?) {
                intent?.let {
                    val level = it.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
                    val scale = it.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
                    val status = it.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
                    if (level != -1 && scale != -1) {
                        batteryLevel = (level * 100 / scale.toFloat()).toInt()
                    }
                    isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                            status == BatteryManager.BATTERY_STATUS_FULL
                }
            }
        }
        val filter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        context.registerReceiver(receiver, filter)
        onDispose {
            try {
                context.unregisterReceiver(receiver)
            } catch (_: Exception) {}
        }
    }

    fun triggerUnlockFlow() {
        if (pinCode.isNotEmpty()) {
            isPinAuthOpen = true
        } else {
            HapticFeedbackUtil.performHaptic(context, hapticEnabled)
            onUnlock()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .offset { IntOffset(0, dragOffsetY.coerceAtMost(0f).roundToInt()) }
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xF00F172A),
                        Color(0xF5050811),
                        Color(0xFF000000)
                    )
                )
            )
            .draggable(
                orientation = Orientation.Vertical,
                state = rememberDraggableState { delta ->
                    if (delta < 0 || dragOffsetY < 0) {
                        dragOffsetY += delta
                        if (dragOffsetY < -260f) {
                            dragOffsetY = 0f
                            triggerUnlockFlow()
                        }
                    }
                },
                onDragStopped = {
                    if (dragOffsetY < -180f) {
                        dragOffsetY = 0f
                        triggerUnlockFlow()
                    } else {
                        dragOffsetY = 0f
                    }
                }
            )
            .testTag("launcher_lock_screen")
    ) {
        // Top status indicators
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 28.dp, vertical = 46.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "Qulflangan",
                    tint = Color.White.copy(alpha = 0.85f),
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Qulflangan",
                    fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.8f),
                    fontWeight = FontWeight.Medium
                )
            }

            // Battery
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = if (isCharging) Icons.Default.BatteryChargingFull else Icons.Default.BatteryFull,
                    contentDescription = null,
                    tint = if (batteryLevel <= 20) Color(0xFFEF4444) else Color(0xFF22C55E),
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "$batteryLevel%",
                    fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.85f),
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        // Center Clock & Date
        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 110.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = currentDate,
                fontSize = 17.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White.copy(alpha = 0.9f)
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = currentTime.ifEmpty { "12:00" },
                fontSize = 82.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = (-2).sp,
                color = Color.White,
                lineHeight = 88.sp
            )
        }

        // Bottom Actions Bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(horizontal = 36.dp, vertical = 44.dp)
        ) {
            // Flashlight Quick Button (Bottom Left)
            Box(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .size(54.dp)
                    .clip(CircleShape)
                    .background(
                        if (isFlashlightOn) Color(0xFFFFD60A).copy(alpha = 0.85f)
                        else Color.White.copy(alpha = 0.18f)
                    )
                    .border(
                        1.dp,
                        if (isFlashlightOn) Color(0xFFFFD60A) else Color.White.copy(alpha = 0.35f),
                        CircleShape
                    )
                    .clickable {
                        HapticFeedbackUtil.performHaptic(context, hapticEnabled)
                        isFlashlightOn = SystemControlUtil.toggleFlashlight(context)
                    }
                    .testTag("lockscreen_flashlight_btn"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.FlashlightOn,
                    contentDescription = "Chiroq",
                    tint = if (isFlashlightOn) Color.Black else Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }

            // Swipe Up handle in center
            Column(
                modifier = Modifier
                    .align(Alignment.Center)
                    .clickable { triggerUnlockFlow() },
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowUp,
                    contentDescription = "Yuqoriga suring",
                    tint = Color.White.copy(alpha = 0.75f),
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = if (pinCode.isNotEmpty()) "PIN terish uchun bosing" else "Ochish uchun yuqoriga suring",
                    fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.75f),
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(6.dp))
                // iOS Home indicator bar
                Box(
                    modifier = Modifier
                        .width(135.dp)
                        .height(4.5.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(Color.White.copy(alpha = 0.8f))
                )
            }

            // Camera Quick Button (Bottom Right)
            Box(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .size(54.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.18f))
                    .border(1.dp, Color.White.copy(alpha = 0.35f), CircleShape)
                    .clickable {
                        HapticFeedbackUtil.performHaptic(context, hapticEnabled)
                        SystemControlUtil.openCamera(context)
                    }
                    .testTag("lockscreen_camera_btn"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.CameraAlt,
                    contentDescription = "Kamera",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }

    // PIN Authentication Dialog for unlocking screen
    if (isPinAuthOpen) {
        PinAuthDialog(
            currentPin = pinCode,
            onSuccess = {
                isPinAuthOpen = false
                HapticFeedbackUtil.performHaptic(context, hapticEnabled)
                onUnlock()
            },
            onDismiss = {
                isPinAuthOpen = false
            },
            onSetNewPin = null,
            hapticEnabled = hapticEnabled
        )
    }
}
