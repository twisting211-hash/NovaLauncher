package com.example.ui.components

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
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
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FlashlightOn
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.utils.HapticFeedbackUtil
import com.example.utils.SystemControlUtil
import kotlin.math.roundToInt

@Composable
fun AssistiveTouchPad(
    onHomeClick: () -> Unit,
    onLockScreen: () -> Unit,
    onOpenSearch: () -> Unit,
    onOpenAppDrawer: () -> Unit,
    onOpenControlCenter: () -> Unit,
    onOpenSettings: () -> Unit,
    hapticEnabled: Boolean,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val density = LocalDensity.current

    var isMenuOpen by remember { mutableStateOf(false) }

    // Floating Button Position
    var offsetX by remember { mutableFloatStateOf(20f) }
    var offsetY by remember { mutableFloatStateOf(650f) }

    var isFlashlightOn by remember { mutableStateOf(SystemControlUtil.isFlashlightOn()) }

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val maxX = with(density) { (maxWidth - 60.dp).toPx() }
        val maxY = with(density) { (maxHeight - 120.dp).toPx() }

        // 1. Draggable Floating Assistive Touch Button
        val buttonAlpha by animateFloatAsState(
            targetValue = if (isMenuOpen) 0f else 0.85f,
            animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
            label = "touch_alpha"
        )

        if (!isMenuOpen) {
            Box(
                modifier = Modifier
                    .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
                    .size(54.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.95f),
                                Color(0xFF1E293B).copy(alpha = 0.85f)
                            )
                        )
                    )
                    .border(2.dp, Color.White.copy(alpha = 0.6f), CircleShape)
                    .shadow(elevation = 10.dp, shape = CircleShape)
                    .pointerInput(Unit) {
                        detectDragGestures { change, dragAmount ->
                            change.consume()
                            offsetX = (offsetX + dragAmount.x).coerceIn(10f, maxX)
                            offsetY = (offsetY + dragAmount.y).coerceIn(60f, maxY)
                        }
                    }
                    .clickable {
                        HapticFeedbackUtil.performHaptic(context, hapticEnabled)
                        isMenuOpen = true
                    }
                    .testTag("assistive_touch_pad_button"),
                contentAlignment = Alignment.Center
            ) {
                // Outer ring
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.35f))
                        .border(1.5.dp, Color.White.copy(alpha = 0.7f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    // Center inner dot
                    Box(
                        modifier = Modifier
                            .size(18.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.9f))
                    )
                }
            }
        }

        // 2. Assistive Touch Expanded Quick Actions Menu
        AnimatedVisibility(
            visible = isMenuOpen,
            enter = scaleIn(initialScale = 0.7f) + fadeIn(),
            exit = scaleOut(targetScale = 0.7f) + fadeOut(),
            modifier = Modifier.align(Alignment.Center)
        ) {
            // Backdrop dismissal
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable { isMenuOpen = false }
                    .testTag("assistive_menu_backdrop"),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    shape = RoundedCornerShape(28.dp),
                    color = Color(0xEB161C24),
                    shadowElevation = 18.dp,
                    modifier = Modifier
                        .padding(24.dp)
                        .width(310.dp)
                        .border(1.dp, Color.White.copy(alpha = 0.18f), RoundedCornerShape(28.dp))
                        .clickable(enabled = false) {}
                        .testTag("assistive_touch_menu")
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(18.dp)
                    ) {
                        // Header
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "Assistive Touchpad",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.12f))
                                    .clickable {
                                        HapticFeedbackUtil.performHaptic(context, hapticEnabled)
                                        isMenuOpen = false
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Yopish",
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Grid of 6-8 Action Buttons
                        val actions = listOf(
                            TouchAction(
                                icon = Icons.Default.Home,
                                label = "Bosh Ekran",
                                color = Color(0xFF38BDF8),
                                onClick = {
                                    isMenuOpen = false
                                    onHomeClick()
                                }
                            ),
                            TouchAction(
                                icon = Icons.Default.Lock,
                                label = "Qulflash",
                                color = Color(0xFFF43F5E),
                                onClick = {
                                    isMenuOpen = false
                                    onLockScreen()
                                }
                            ),
                            TouchAction(
                                icon = Icons.Default.Bolt,
                                label = "RAM Tezlatish",
                                color = Color(0xFFFACC15),
                                onClick = {
                                    System.gc()
                                    Toast.makeText(context, "Operativ xotira (RAM) tozalandi!", Toast.LENGTH_SHORT).show()
                                    isMenuOpen = false
                                }
                            ),
                            TouchAction(
                                icon = Icons.Default.Search,
                                label = "Spotlight",
                                color = Color(0xFF818CF8),
                                onClick = {
                                    isMenuOpen = false
                                    onOpenSearch()
                                }
                            ),
                            TouchAction(
                                icon = Icons.Default.FlashlightOn,
                                label = if (isFlashlightOn) "Chiroq: Yoqiq" else "Chiroq",
                                color = if (isFlashlightOn) Color(0xFFFFD60A) else Color(0xFF94A3B8),
                                onClick = {
                                    isFlashlightOn = SystemControlUtil.toggleFlashlight(context)
                                }
                            ),
                            TouchAction(
                                icon = Icons.Default.Tune,
                                label = "Boshqaruv",
                                color = Color(0xFF34D399),
                                onClick = {
                                    isMenuOpen = false
                                    onOpenControlCenter()
                                }
                            ),
                            TouchAction(
                                icon = Icons.Default.Apps,
                                label = "Ilovalar",
                                color = Color(0xFFA78BFA),
                                onClick = {
                                    isMenuOpen = false
                                    onOpenAppDrawer()
                                }
                            ),
                            TouchAction(
                                icon = Icons.Default.Settings,
                                label = "Sozlamalar",
                                color = Color(0xFFCBD5E1),
                                onClick = {
                                    isMenuOpen = false
                                    onOpenSettings()
                                }
                            )
                        )

                        // 4x2 Grid layout
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            actions.chunked(4).forEach { rowItems ->
                                Row(
                                    horizontalArrangement = Arrangement.SpaceEvenly,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    rowItems.forEach { action ->
                                        TouchActionButton(
                                            action = action,
                                            onClicked = {
                                                HapticFeedbackUtil.performHaptic(context, hapticEnabled)
                                                action.onClick()
                                            }
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}

private data class TouchAction(
    val icon: ImageVector,
    val label: String,
    val color: Color,
    val onClick: () -> Unit
)

@Composable
private fun TouchActionButton(
    action: TouchAction,
    onClicked: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(62.dp)
            .clip(RoundedCornerShape(14.dp))
            .clickable(onClick = onClicked)
            .padding(vertical = 6.dp)
    ) {
        Box(
            modifier = Modifier
                .size(46.dp)
                .clip(CircleShape)
                .background(action.color.copy(alpha = 0.18f))
                .border(1.dp, action.color.copy(alpha = 0.4f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = action.icon,
                contentDescription = action.label,
                tint = action.color,
                modifier = Modifier.size(22.dp)
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = action.label,
            fontSize = 10.sp,
            color = Color.White.copy(alpha = 0.9f),
            fontWeight = FontWeight.Medium,
            maxLines = 1
        )
    }
}
