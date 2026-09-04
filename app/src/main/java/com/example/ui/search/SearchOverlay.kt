package com.example.ui.search

import android.app.SearchManager
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shop
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.launcher.apps.AppInfo
import com.example.ui.components.AppIconItem
import com.example.utils.HapticFeedbackUtil
import java.util.Locale

@Composable
fun SearchOverlay(
    allApps: List<AppInfo>,
    recentApps: List<AppInfo>,
    favoriteApps: List<AppInfo>,
    onAppClick: (AppInfo) -> Unit,
    onAppLongClick: (AppInfo) -> Unit,
    onClose: () -> Unit,
    iconSize: String = "medium",
    iconShape: String = "squircle",
    hapticEnabled: Boolean = true,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var query by remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    // Live App Search Results
    val searchResults = remember(query, allApps) {
        if (query.isBlank()) {
            emptyList()
        } else {
            val q = query.trim().lowercase()
            allApps.filter {
                it.label.lowercase().contains(q) || it.packageName.lowercase().contains(q)
            }
        }
    }

    // Live Math Evaluation
    val mathResult = remember(query) {
        evaluateMathExpression(query.trim())
    }

    // Detect if query is a phone number
    val isPhoneNumber = remember(query) {
        val trimmed = query.trim()
        trimmed.length in 4..15 && trimmed.all { it.isDigit() || it == '+' || it == '-' || it == ' ' } && trimmed.any { it.isDigit() }
    }

    // System Settings Matching
    val systemShortcuts = remember(query) {
        if (query.length < 2) emptyList()
        else {
            val q = query.lowercase().trim()
            val list = mutableListOf<SystemShortcut>()
            if (q.contains("wi") || q.contains("int")) list.add(SystemShortcut("Wi-Fi Sozlamalari", Settings.ACTION_WIFI_SETTINGS))
            if (q.contains("blu") || q.contains("bt")) list.add(SystemShortcut("Bluetooth Sozlamalari", Settings.ACTION_BLUETOOTH_SETTINGS))
            if (q.contains("bat") || q.contains("quv")) list.add(SystemShortcut("Batareya Holati", Intent.ACTION_POWER_USAGE_SUMMARY))
            if (q.contains("xot") || q.contains("sto") || q.contains("xotira")) list.add(SystemShortcut("Xotira Sozlamalari", Settings.ACTION_INTERNAL_STORAGE_SETTINGS))
            if (q.contains("il") || q.contains("app")) list.add(SystemShortcut("Ilovalarni Boshqarish", Settings.ACTION_APPLICATION_SETTINGS))
            if (q.contains("ov") || q.contains("sou") || q.contains("vol")) list.add(SystemShortcut("Ovoz Sozlamalari", Settings.ACTION_SOUND_SETTINGS))
            if (q.contains("ek") || q.contains("dis")) list.add(SystemShortcut("Ekran Sozlamalari", Settings.ACTION_DISPLAY_SETTINGS))
            list
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.98f))
            .statusBarsPadding()
            .navigationBarsPadding()
            .testTag("search_overlay")
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Search Input Row
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                IconButton(onClick = onClose) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Orqaga"
                    )
                }

                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    placeholder = {
                        Text("Ilova, hisob-kitob yoki internetdan qidirish...", fontSize = 13.sp)
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    },
                    trailingIcon = {
                        if (query.isNotEmpty()) {
                            IconButton(onClick = { query = "" }) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Tozalash"
                                )
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(24.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = Color.Transparent
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .focusRequester(focusRequester)
                        .testTag("search_overlay_input")
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            if (query.isBlank()) {
                // Empty state: Show Recents, Favorites & Quick Tools
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    // System Shortcuts quick bar
                    item {
                        Text(
                            text = "Tezkor Tizim Asboblari",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            ),
                            modifier = Modifier.padding(vertical = 6.dp)
                        )
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            contentPadding = PaddingValues(vertical = 4.dp)
                        ) {
                            val defaultShortcuts = listOf(
                                SystemShortcut("Wi-Fi", Settings.ACTION_WIFI_SETTINGS),
                                SystemShortcut("Bluetooth", Settings.ACTION_BLUETOOTH_SETTINGS),
                                SystemShortcut("Batareya", Intent.ACTION_POWER_USAGE_SUMMARY),
                                SystemShortcut("Xotira", Settings.ACTION_INTERNAL_STORAGE_SETTINGS),
                                SystemShortcut("Ilovalar", Settings.ACTION_APPLICATION_SETTINGS),
                                SystemShortcut("Ekran", Settings.ACTION_DISPLAY_SETTINGS)
                            )
                            items(defaultShortcuts) { shortcut ->
                                FilterChip(
                                    selected = false,
                                    onClick = {
                                        try {
                                            context.startActivity(Intent(shortcut.action))
                                        } catch (e: Exception) {
                                            Toast.makeText(context, "Ochib bo'lmadi", Toast.LENGTH_SHORT).show()
                                        }
                                    },
                                    label = { Text(shortcut.title, fontSize = 12.sp) },
                                    leadingIcon = {
                                        Icon(imageVector = Icons.Default.Settings, contentDescription = null, modifier = Modifier.size(14.dp))
                                    },
                                    shape = RoundedCornerShape(12.dp)
                                )
                            }
                        }
                    }

                    if (favoriteApps.isNotEmpty()) {
                        item {
                            Spacer(modifier = Modifier.height(14.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.padding(end = 6.dp).size(18.dp)
                                )
                                Text(
                                    text = "Sevimli ilovalar",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp
                                    )
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                        }

                        item {
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                contentPadding = PaddingValues(vertical = 4.dp)
                            ) {
                                items(favoriteApps, key = { it.packageName }) { app ->
                                    AppIconItem(
                                        app = app,
                                        onClick = { onAppClick(app) },
                                        onLongClick = { onAppLongClick(app) },
                                        iconSize = iconSize,
                                        iconShape = iconShape,
                                        showLabel = true,
                                        textColor = MaterialTheme.colorScheme.onSurface,
                                        hapticEnabled = hapticEnabled
                                    )
                                }
                            }
                        }
                    }

                    if (recentApps.isNotEmpty()) {
                        item {
                            Spacer(modifier = Modifier.height(18.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.History,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.secondary,
                                    modifier = Modifier.padding(end = 6.dp).size(18.dp)
                                )
                                Text(
                                    text = "Tez-tez ishlatiladigan ilovalar",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp
                                    )
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                        }

                        item {
                            // 4 columns grid of recent apps
                            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                recentApps.chunked(4).forEach { rowApps ->
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceAround
                                    ) {
                                        rowApps.forEach { app ->
                                            AppIconItem(
                                                app = app,
                                                onClick = { onAppClick(app) },
                                                onLongClick = { onAppLongClick(app) },
                                                iconSize = iconSize,
                                                iconShape = iconShape,
                                                showLabel = true,
                                                textColor = MaterialTheme.colorScheme.onSurface,
                                                hapticEnabled = hapticEnabled
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                // Active Query Screen: Math, Phone, System Actions, Apps, Web Search
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    // 1. Math Calculation Result
                    mathResult?.let { result ->
                        item {
                            MathResultCard(
                                expression = query,
                                result = result,
                                onCopy = {
                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    clipboard.setPrimaryClip(ClipData.newPlainText("Natija", result))
                                    HapticFeedbackUtil.performHaptic(context, hapticEnabled)
                                    Toast.makeText(context, "Nusxa olindi: $result", Toast.LENGTH_SHORT).show()
                                },
                                onOpenCalculator = {
                                    val calcIntent = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_APP_CALCULATOR)
                                    try {
                                        context.startActivity(calcIntent)
                                    } catch (e: Exception) {
                                        Toast.makeText(context, "Kalkulyator ilovasi topilmadi", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                        }
                    }

                    // 2. Phone Dialer Shortcut
                    if (isPhoneNumber) {
                        item {
                            PhoneDialerCard(
                                phoneNumber = query.trim(),
                                onDial = {
                                    try {
                                        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${query.trim()}"))
                                        context.startActivity(intent)
                                    } catch (e: Exception) {
                                        Toast.makeText(context, "Qo'ng'iroq qilib bo'lmadi", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                        }
                    }

                    // 3. Matching System Settings
                    if (systemShortcuts.isNotEmpty()) {
                        item {
                            Text(
                                text = "Tizim Sozlamalari",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                systemShortcuts.forEach { shortcut ->
                                    Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                try {
                                                    context.startActivity(Intent(shortcut.action))
                                                } catch (e: Exception) {
                                                    Toast.makeText(context, "Ochib bo'lmadi", Toast.LENGTH_SHORT).show()
                                                }
                                            }
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(12.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Settings,
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.size(20.dp)
                                            )
                                            Spacer(modifier = Modifier.width(10.dp))
                                            Text(shortcut.title, fontWeight = FontWeight.Medium, fontSize = 14.sp)
                                        }
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                    }

                    // 4. App Results Header
                    if (searchResults.isNotEmpty()) {
                        item {
                            Text(
                                text = "Topilgan Ilovalar (${searchResults.size})",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }

                        // App Results as rich rows with direct quick launch & info
                        items(searchResults, key = { it.packageName }) { app ->
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .clickable { onAppClick(app) }
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        AppIconItem(
                                            app = app,
                                            onClick = { onAppClick(app) },
                                            onLongClick = { onAppLongClick(app) },
                                            iconSize = "small",
                                            iconShape = iconShape,
                                            showLabel = false,
                                            textColor = MaterialTheme.colorScheme.onSurface,
                                            hapticEnabled = hapticEnabled
                                        )
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column {
                                            Text(
                                                text = app.label,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 14.sp,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                            Text(
                                                text = app.packageName,
                                                fontSize = 11.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                maxLines = 1
                                            )
                                        }
                                    }

                                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                        IconButton(
                                            onClick = { onAppLongClick(app) },
                                            modifier = Modifier.size(32.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.OpenInNew,
                                                contentDescription = "Boshqarish",
                                                tint = MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // 5. Universal Web / Google / YouTube Search actions
                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Internetdan qidirish",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            // Google Web Search
                            WebSearchActionRow(
                                title = "Google'da qidirish",
                                query = query,
                                icon = Icons.Default.Language,
                                color = Color(0xFF4285F4),
                                onClick = {
                                    try {
                                        val intent = Intent(Intent.ACTION_WEB_SEARCH).apply {
                                            putExtra(SearchManager.QUERY, query)
                                        }
                                        context.startActivity(intent)
                                    } catch (e: Exception) {
                                        val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/search?q=${Uri.encode(query)}"))
                                        context.startActivity(webIntent)
                                    }
                                }
                            )

                            // YouTube Search
                            WebSearchActionRow(
                                title = "YouTube'da tomosha qilish",
                                query = query,
                                icon = Icons.Default.PlayArrow,
                                color = Color(0xFFFF0000),
                                onClick = {
                                    try {
                                        val ytIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/results?search_query=${Uri.encode(query)}"))
                                        context.startActivity(ytIntent)
                                    } catch (e: Exception) {
                                        Toast.makeText(context, "YouTube ochilmadi", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            )

                            // Play Store Search
                            WebSearchActionRow(
                                title = "Google Play'dan yangi ilova izlash",
                                query = query,
                                icon = Icons.Default.Shop,
                                color = Color(0xFF00C853),
                                onClick = {
                                    try {
                                        val storeIntent = Intent(Intent.ACTION_VIEW, Uri.parse("market://search?q=${Uri.encode(query)}"))
                                        context.startActivity(storeIntent)
                                    } catch (e: Exception) {
                                        val webStore = Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/search?q=${Uri.encode(query)}"))
                                        context.startActivity(webStore)
                                    }
                                }
                            )
                        }
                        Spacer(modifier = Modifier.height(30.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun MathResultCard(
    expression: String,
    result: String,
    onCopy: () -> Unit,
    onOpenCalculator: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF007AFF).copy(alpha = 0.12f)
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF007AFF)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = Icons.Default.Calculate, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Hisoblash Natijasi", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
                Text(text = expression, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "= $result",
                fontSize = 28.sp,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = onCopy,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    modifier = Modifier.height(34.dp)
                ) {
                    Icon(imageVector = Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Nusxa olish", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                }

                Button(
                    onClick = onOpenCalculator,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.filledTonalButtonColors(),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    modifier = Modifier.height(34.dp)
                ) {
                    Text("Kalkulyatorni ochish", fontSize = 11.sp)
                }
            }
        }
    }
}

@Composable
private fun PhoneDialerCard(
    phoneNumber: String,
    onDial: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF34C759).copy(alpha = 0.15f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF34C759)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = Icons.Default.Phone, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text("Telefon raqami", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(phoneNumber, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }

            Button(
                onClick = onDial,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF34C759))
            ) {
                Text("Qo'ng'iroq", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun WebSearchActionRow(
    title: String,
    query: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.18f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(18.dp))
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                Text("\"$query\"", fontSize = 11.sp, color = MaterialTheme.colorScheme.primary, maxLines = 1)
            }
            Icon(
                imageVector = Icons.Default.OpenInNew,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

private data class SystemShortcut(val title: String, val action: String)

/**
 * Safe, robust parser for arithmetic expressions like:
 * 25*4, 100/5, 45+15, 50-12.5, 3*10+5, etc.
 */
private fun evaluateMathExpression(input: String): String? {
    val clean = input.replace(" ", "").replace("x", "*").replace("X", "*")
    if (clean.length < 3) return null
    if (!clean.matches(Regex("^[0-9+*\\-/.%()]+$"))) return null
    if (!clean.any { it in "+-*/%" }) return null

    return try {
        var currentNumber = ""
        val tokens = mutableListOf<String>()
        var i = 0
        while (i < clean.length) {
            val c = clean[i]
            if (c in "+-*/%") {
                if (currentNumber.isNotEmpty()) {
                    tokens.add(currentNumber)
                    currentNumber = ""
                }
                tokens.add(c.toString())
            } else if (c.isDigit() || c == '.') {
                currentNumber += c
            } else {
                return null
            }
            i++
        }
        if (currentNumber.isNotEmpty()) tokens.add(currentNumber)
        if (tokens.size < 3) return null

        // Pass 1: *, /, %
        val pass1 = mutableListOf<String>()
        var idx = 0
        while (idx < tokens.size) {
            val token = tokens[idx]
            if (token == "*" || token == "/" || token == "%") {
                if (pass1.isEmpty()) return null
                val prev = pass1.removeAt(pass1.size - 1).toDoubleOrNull() ?: return null
                val next = tokens.getOrNull(idx + 1)?.toDoubleOrNull() ?: return null
                val res = when (token) {
                    "*" -> prev * next
                    "/" -> if (next != 0.0) prev / next else return null
                    "%" -> prev % next
                    else -> prev
                }
                pass1.add(res.toString())
                idx += 2
            } else {
                pass1.add(token)
                idx++
            }
        }

        // Pass 2: +, -
        var result = pass1[0].toDoubleOrNull() ?: return null
        var opIdx = 1
        while (opIdx < pass1.size) {
            val op = pass1[opIdx]
            val nextVal = pass1.getOrNull(opIdx + 1)?.toDoubleOrNull() ?: return null
            result = when (op) {
                "+" -> result + nextVal
                "-" -> result - nextVal
                else -> result
            }
            opIdx += 2
        }

        // Format nicely: no trailing .0 if integer
        if (result % 1.0 == 0.0 && !result.isInfinite()) {
            result.toLong().toString()
        } else {
            String.format(Locale.US, "%.3f", result).trimEnd('0').trimEnd('.')
        }
    } catch (e: Exception) {
        null
    }
}
