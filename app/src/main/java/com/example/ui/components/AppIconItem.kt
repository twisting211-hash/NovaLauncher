package com.example.ui.components

import android.graphics.Bitmap
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Android
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.launcher.apps.AppInfo
import com.example.utils.HapticFeedbackUtil

@OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)
@Composable
fun AppIconItem(
    app: AppInfo,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    modifier: Modifier = Modifier,
    iconSize: String = "medium", // "small", "medium", "large"
    iconShape: String = "squircle", // "circle", "rounded", "squircle", "square"
    showLabel: Boolean = true,
    textColor: Color = Color.White,
    hapticEnabled: Boolean = true,
    isJiggling: Boolean = false,
    onDeleteClick: (() -> Unit)? = null
) {
    val context = LocalContext.current
    val sizeDp = when (iconSize) {
        "small" -> 46.dp
        "large" -> 64.dp
        else -> 56.dp
    }

    val shape = when (iconShape) {
        "circle" -> CircleShape
        "square" -> RoundedCornerShape(8.dp)
        "squircle" -> RoundedCornerShape(22)
        else -> RoundedCornerShape(18.dp)
    }

    // iOS Jiggle Wiggle Animation
    val rotation = if (isJiggling) {
        val infiniteTransition = rememberInfiniteTransition(label = "jiggle_${app.packageName}")
        val angle by infiniteTransition.animateFloat(
            initialValue = -1.8f,
            targetValue = 1.8f,
            animationSpec = infiniteRepeatable(
                animation = tween(120),
                repeatMode = RepeatMode.Reverse
            ),
            label = "angle_${app.packageName}"
        )
        angle
    } else {
        0f
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
            .testTag("app_item_${app.packageName}")
            .rotate(rotation)
            .clip(RoundedCornerShape(14.dp))
            .combinedClickable(
                onClick = {
                    HapticFeedbackUtil.performHaptic(context, hapticEnabled)
                    onClick()
                },
                onLongClick = {
                    HapticFeedbackUtil.performHaptic(context, hapticEnabled)
                    onLongClick()
                }
            )
            .padding(vertical = 4.dp, horizontal = 2.dp)
    ) {
        Box(
            modifier = Modifier.size(sizeDp + 4.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(sizeDp)
                    .shadow(4.dp, shape, ambientColor = Color.Black.copy(alpha = 0.25f))
                    .clip(shape)
                    .border(0.5.dp, Color.White.copy(alpha = 0.12f), shape),
                contentAlignment = Alignment.Center
            ) {
                if (app.icon != null) {
                    Image(
                        bitmap = app.icon.asImageBitmap(),
                        contentDescription = app.label,
                        modifier = Modifier.size(sizeDp)
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Android,
                        contentDescription = app.label,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(sizeDp * 0.75f)
                    )
                }
            }

            // iOS style Minus Badge in Top-Left
            if (isJiggling && onDeleteClick != null) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .size(20.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF8E8E93).copy(alpha = 0.9f))
                        .border(1.dp, Color.White, CircleShape)
                        .clickable {
                            HapticFeedbackUtil.performHaptic(context, hapticEnabled)
                            onDeleteClick()
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Remove,
                        contentDescription = "O'chirish",
                        tint = Color.White,
                        modifier = Modifier.size(12.dp)
                    )
                }
            }
        }

        if (showLabel) {
            Text(
                text = app.label,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = textColor
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}
