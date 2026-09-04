package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Android
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Launch
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.launcher.apps.AppInfo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppContextMenu(
    app: AppInfo,
    onDismiss: () -> Unit,
    onOpen: () -> Unit,
    onAddToHome: () -> Unit,
    onToggleFavorite: () -> Unit,
    onAppInfo: () -> Unit,
    onUninstall: () -> Unit,
    onToggleHide: () -> Unit,
    isFavorite: Boolean,
    isLocked: Boolean = false,
    onToggleLock: (() -> Unit)? = null,
    onRenameApp: (() -> Unit)? = null
) {
    val sheetState = rememberModalBottomSheetState()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = Modifier.testTag("app_context_menu")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 10.dp)
        ) {
            // Header with App Icon and Name
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    if (app.icon != null) {
                        Image(
                            bitmap = app.icon.asImageBitmap(),
                            contentDescription = app.label,
                            modifier = Modifier.size(48.dp)
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Android,
                            contentDescription = app.label,
                            modifier = Modifier.size(36.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = app.label,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        )
                        if (isLocked) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Qulflangan",
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                    Text(
                        text = app.packageName,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

            Spacer(modifier = Modifier.height(10.dp))

            // Actions
            MenuItemRow(
                icon = Icons.Default.Launch,
                title = "Ochish",
                onClick = {
                    onOpen()
                    onDismiss()
                }
            )

            MenuItemRow(
                icon = Icons.Default.Add,
                title = "Bosh ekranga qo'shish",
                onClick = {
                    onAddToHome()
                    onDismiss()
                }
            )

            if (onRenameApp != null) {
                MenuItemRow(
                    icon = Icons.Default.Edit,
                    title = "Ilovani qayta nomlash",
                    onClick = {
                        onDismiss()
                        onRenameApp()
                    }
                )
            }

            if (onToggleLock != null) {
                MenuItemRow(
                    icon = if (isLocked) Icons.Default.LockOpen else Icons.Default.Lock,
                    title = if (isLocked) "Ilova qulfini yechish" else "Ilovani PIN bilan qulflash",
                    iconTint = if (isLocked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                    onClick = {
                        onToggleLock()
                        onDismiss()
                    }
                )
            }

            MenuItemRow(
                icon = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                title = if (isFavorite) "Sevimlilardan olish" else "Sevimlilarga qo'shish",
                iconTint = if (isFavorite) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                onClick = {
                    onToggleFavorite()
                    onDismiss()
                }
            )

            MenuItemRow(
                icon = Icons.Default.Info,
                title = "Ilova haqida",
                onClick = {
                    onAppInfo()
                    onDismiss()
                }
            )

            MenuItemRow(
                icon = Icons.Default.VisibilityOff,
                title = "Ilovani yashirish",
                onClick = {
                    onToggleHide()
                    onDismiss()
                }
            )

            MenuItemRow(
                icon = Icons.Default.Delete,
                title = "O'chirish (Uninstall)",
                iconTint = MaterialTheme.colorScheme.error,
                textColor = MaterialTheme.colorScheme.error,
                onClick = {
                    onUninstall()
                    onDismiss()
                }
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun MenuItemRow(
    icon: ImageVector,
    title: String,
    onClick: () -> Unit,
    iconTint: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    textColor: Color = MaterialTheme.colorScheme.onSurface
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 14.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = iconTint,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.Medium,
                color = textColor,
                fontSize = 15.sp
            )
        )
    }
}
