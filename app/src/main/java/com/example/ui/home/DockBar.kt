package com.example.ui.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.launcher.apps.AppInfo
import com.example.ui.components.AppIconItem

@Composable
fun DockBar(
    dockApps: List<AppInfo>,
    onAppClick: (AppInfo) -> Unit,
    onAppLongClick: (AppInfo) -> Unit,
    onOpenDrawer: () -> Unit,
    onOpenSearch: () -> Unit = {},
    isIosStyle: Boolean = true,
    showDock: Boolean = true,
    showLabels: Boolean = false,
    iconSize: String = "medium",
    iconShape: String = "squircle",
    hapticEnabled: Boolean = true,
    modifier: Modifier = Modifier
) {
    if (!showDock) return

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(bottom = 6.dp)
            .testTag("dock_bar")
    ) {
        if (isIosStyle) {
            // iOS 17/18 Spotlight Search Pill above Dock
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.22f))
                    .border(0.5.dp, Color.White.copy(alpha = 0.35f), CircleShape)
                    .clickable(onClick = onOpenSearch)
                    .padding(horizontal = 14.dp, vertical = 5.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Qidiruv",
                        tint = Color.White.copy(alpha = 0.95f),
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = "Qidiruv",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Color.White.copy(alpha = 0.95f),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }
            }
        } else {
            // Classic Android Swipe Up indicator
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.25f))
                    .clickable(onClick = onOpenDrawer)
                    .padding(horizontal = 14.dp, vertical = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowUp,
                        contentDescription = "Ilovalar",
                        tint = Color.White.copy(alpha = 0.85f),
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "Ilovalar",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Color.White.copy(alpha = 0.85f),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        ),
                        modifier = Modifier.padding(start = 2.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Frosted Glass Dock Container
        Box(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .shadow(
                    elevation = if (isIosStyle) 18.dp else 8.dp,
                    shape = RoundedCornerShape(34.dp),
                    ambientColor = Color.Black.copy(alpha = 0.35f)
                )
                .clip(RoundedCornerShape(34.dp))
                .background(
                    if (isIosStyle) Color.White.copy(alpha = 0.22f)
                    else Color.Black.copy(alpha = 0.35f)
                )
                .border(
                    width = 1.dp,
                    color = if (isIosStyle) Color.White.copy(alpha = 0.35f) else Color.White.copy(alpha = 0.12f),
                    shape = RoundedCornerShape(34.dp)
                )
                .padding(horizontal = 14.dp, vertical = 6.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                dockApps.forEach { app ->
                    AppIconItem(
                        app = app,
                        onClick = { onAppClick(app) },
                        onLongClick = { onAppLongClick(app) },
                        iconSize = iconSize,
                        iconShape = if (isIosStyle) "squircle" else iconShape,
                        showLabel = showLabels,
                        textColor = Color.White,
                        hapticEnabled = hapticEnabled
                    )
                }
            }
        }
    }
}
