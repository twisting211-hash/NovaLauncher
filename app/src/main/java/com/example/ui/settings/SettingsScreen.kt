package com.example.ui.settings

import android.app.Activity
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Animation
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.SortByAlpha
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material.icons.filled.PhoneIphone
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import com.example.ui.settings.PinAuthDialog
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.preferences.LauncherSettings
import com.example.launcher.intents.LauncherIntents
import com.example.ui.theme.IosWallpapers
import com.example.viewmodel.LauncherViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: LauncherViewModel,
    settings: LauncherSettings,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val activity = context as? Activity
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    var isPinDialogOpen by remember { mutableStateOf(false) }
    var isResetDialogOpen by remember { mutableStateOf(false) }
    var pinDialogPurpose by remember { mutableStateOf("open_hidden") } // "open_hidden" or "set_pin"

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            scope.launch {
                viewModel.preferences.setWallpaperType("custom", uri.toString())
            }
        }
    }

    if (isPinDialogOpen) {
        PinAuthDialog(
            currentPin = if (pinDialogPurpose == "set_pin") "" else settings.hiddenAppsPin,
            onSuccess = {
                isPinDialogOpen = false
                if (pinDialogPurpose == "open_hidden") {
                    viewModel.isHiddenAppsManagerOpen.value = true
                }
            },
            onDismiss = { isPinDialogOpen = false },
            onSetNewPin = { newPin ->
                viewModel.setHiddenAppsPin(newPin)
                isPinDialogOpen = false
                if (pinDialogPurpose == "open_hidden") {
                    viewModel.isHiddenAppsManagerOpen.value = true
                }
            },
            hapticEnabled = settings.hapticEnabled
        )
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .navigationBarsPadding()
            .testTag("settings_screen")
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            TopAppBar(
                title = {
                    Text(
                        text = "NovaHome Sozlamalari",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onClose) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Yopish"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                // Default Launcher Banner
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Home,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Asosiy bosh ekran qilish",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            )
                            Text(
                                text = "NovaHome-ni doimiy launcher sifatida tanlang",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                                )
                            )
                        }
                        Button(
                            onClick = { activity?.let { LauncherIntents.requestDefaultLauncher(it) } },
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("O'rnatish")
                        }
                    }
                }

                // 0. SUPER-POWER FEATURES (Tizim boshqaruvi va kuchli qurollar)
                SectionTitle(title = "Kuchli Asboblar & Tizim Boshqaruvi", icon = Icons.Default.Bolt)
                SettingsCard {
                    // Assistive Touch Pad (Virtual Touchpad)
                    SettingToggleRow(
                        title = "Assistive Touch (Virtual Touchpad)",
                        subtitle = "Ekranda suzuvchi tezkor tugma (Bosh ekran, Qulflash, RAM, Chiroq, Spotlight va Sozlamalar)",
                        checked = settings.showAssistiveTouch,
                        onCheckedChange = { viewModel.setShowAssistiveTouch(it) }
                    )

                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                    // Launcher Lock Screen
                    SettingToggleRow(
                        title = "Launcher Qulf Ekrani",
                        subtitle = "Chiroyli raqamli soat, real chiroq, kamera va PIN kod himoyasi bilan qulf ekrani",
                        checked = settings.showLockScreen,
                        onCheckedChange = { viewModel.setShowLockScreen(it) }
                    )

                    if (settings.showLockScreen) {
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedButton(
                            onClick = {
                                onClose()
                                viewModel.lockScreen()
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 4.dp, vertical = 2.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Ekranni hozir qulflash", fontSize = 13.sp)
                        }
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                    // Clean & Minimalist Mode
                    SettingToggleRow(
                        title = "Toza Minimalist Rejim",
                        subtitle = "Barcha ortiqcha elementlarni yashirib, ekranni toza va bejirim qilish",
                        checked = settings.cleanMinimalistMode,
                        onCheckedChange = { viewModel.setCleanMinimalistMode(it) }
                    )

                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                    // Show Clock Widget
                    SettingToggleRow(
                        title = "Asosiy Soat Vidjetini ko'rsatish",
                        subtitle = "Bosh ekrandagi soat va sana vidjetini ko'rsatish yoki yashirish",
                        checked = settings.showClockWidget,
                        onCheckedChange = { viewModel.setShowClockWidget(it) }
                    )

                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                    // System Monitor Dashboard
                    SettingToggleRow(
                        title = "Tizim Tezlatgich & Holat Paneli (RAM & Xotira)",
                        subtitle = "Bosh ekranda jonli RAM, doimiy xotira va 1-bosishda tezlatgich moduli",
                        checked = settings.showSystemMonitor,
                        onCheckedChange = { viewModel.setShowSystemMonitor(it) }
                    )

                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                    // Zen Focus Mode
                    SettingToggleRow(
                        title = "Zen Fokus Rejimi (Chalg'imaslik)",
                        subtitle = "Barcha ijtimoiy tarmoqlarni vaqtinchalik yashirib, faqat tinch va zarur aloqa vositalarini qoldirish",
                        checked = settings.focusModeEnabled,
                        onCheckedChange = { viewModel.setFocusMode(it) }
                    )

                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                    // Double-tap action
                    SettingOptionRow(title = "Ekranni 2 marta bosish harakati") {
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            listOf(
                                "lock" to "Qulflash",
                                "search" to "Spotlight",
                                "booster" to "RAM Tozalash",
                                "drawer" to "Ilovalar",
                                "none" to "Yo'q"
                            ).forEach { (action, label) ->
                                FilterChip(
                                    selected = settings.doubleTapAction == action,
                                    onClick = { viewModel.setDoubleTapAction(action) },
                                    label = { Text(label, fontSize = 11.sp) },
                                    shape = RoundedCornerShape(12.dp)
                                )
                            }
                        }
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                    // Hidden Apps PIN Lock
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Maxfiy Ilovalar PIN Himoyasi",
                                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium)
                            )
                            Text(
                                text = if (settings.hiddenAppsPin.isEmpty()) "PIN kod o'rnatilmagan (Ochiq)" else "4 xonali PIN bilan himoyalangan",
                                style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                            )
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            if (settings.hiddenAppsPin.isNotEmpty()) {
                                OutlinedButton(
                                    onClick = { viewModel.setHiddenAppsPin("") },
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Text("O'chirish", fontSize = 11.sp)
                                }
                            }
                            Button(
                                onClick = {
                                    pinDialogPurpose = "set_pin"
                                    isPinDialogOpen = true
                                },
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text(if (settings.hiddenAppsPin.isEmpty()) "PIN o'rnatish" else "O'zgartirish", fontSize = 11.sp)
                            }
                        }
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                    // Spotlight Feature Highlight
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.12f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Calculate,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.secondary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Universal Spotlight Qidiruv",
                                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium)
                            )
                            Text(
                                text = "Ekranni pastga torting: ilovalar, matematik ifodalarni hisoblash (masalan: 1250*4), telefon raqam terish, va to'g'ridan-to'g'ri Google/YouTube qidiruv",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    lineHeight = 16.sp
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 1. LAUNCHER STYLE & HYBRID BALANCE
                SectionTitle(title = "Dizayn Uslubi (Gibrid / Android / iOS)", icon = Icons.Default.PhoneIphone)
                SettingsCard {
                    // Launcher Mode (Hybrid vs Classic vs iOS)
                    SettingOptionRow(title = "Launcher Uslubi") {
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            listOf(
                                "hybrid" to "Gibrid (Tavsiya)",
                                "classic" to "Toza Android",
                                "ios" to "iPhone (iOS 18)"
                            ).forEach { (style, label) ->
                                FilterChip(
                                    selected = settings.launcherStyle == style,
                                    onClick = {
                                        viewModel.setLauncherStyle(style)
                                        if (style == "ios") {
                                            viewModel.setIosWallpaper("ios_18_dark")
                                        }
                                    },
                                    label = { Text(label, fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                                    shape = RoundedCornerShape(12.dp)
                                )
                            }
                        }
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                    // Apple Wallpapers Collection
                    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                        Text(
                            text = "Apple Fon Rasmlari (iOS Wallpapers)",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            IosWallpapers.PRESETS.chunked(2).forEach { rowPresets ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    rowPresets.forEach { preset ->
                                        val isSelected = settings.iosWallpaper == preset.id && settings.wallpaperType == "ios_preset"
                                        Button(
                                            onClick = {
                                                viewModel.setIosWallpaper(preset.id)
                                            },
                                            shape = RoundedCornerShape(14.dp),
                                            colors = if (isSelected) ButtonDefaults.buttonColors() else ButtonDefaults.filledTonalButtonColors(),
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Text(
                                                text = preset.title,
                                                fontSize = 11.sp,
                                                maxLines = 1
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                    // Dynamic Island Toggle
                    SettingToggleRow(
                        title = "Dinamik Orolcha (Dynamic Island)",
                        subtitle = "Ekranning tepasida interaktiv qora orolcha va tezkor boshqaruv",
                        checked = settings.showDynamicIsland,
                        onCheckedChange = { viewModel.setShowDynamicIsland(it) }
                    )

                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                    // iOS Smart Widgets Toggle
                    SettingToggleRow(
                        title = "Apple Aqlli Vidjetlari",
                        subtitle = "Bosh ekranda chiroyli Ob-havo, Kalendar va Batareya kartalari",
                        checked = settings.showIosWidgets,
                        onCheckedChange = { viewModel.setShowIosWidgets(it) }
                    )

                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                    // iOS Clock Style
                    SettingOptionRow(title = "iOS Soat shrifti") {
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            listOf(
                                "ios_bold" to "iOS Qalin",
                                "ios_serif" to "iOS Serif",
                                "ios_thin" to "iOS Yupqa"
                            ).forEach { (fontStyle, label) ->
                                FilterChip(
                                    selected = settings.iosClockStyle == fontStyle,
                                    onClick = { viewModel.setIosClockStyle(fontStyle) },
                                    label = { Text(label, fontSize = 11.sp) },
                                    shape = RoundedCornerShape(12.dp)
                                )
                            }
                        }
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                    // Quick Open Control Center Button
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Boshqaruv Markazi (Control Center)",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                            )
                            Text(
                                text = "Ekran tepasidan pastga surish yoki orolchadan ochiladi",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                        }
                        Button(
                            onClick = {
                                onClose()
                                viewModel.isControlCenterOpen.value = true
                            },
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Tune, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Ochish", fontSize = 12.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 1. APPEARANCE
                SectionTitle(title = "Ko'rinish (Appearance)", icon = Icons.Default.Palette)

                SettingsCard {
                    // Theme
                    SettingOptionRow(title = "Mavzu rejimi") {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf("system" to "Tizim", "dark" to "Qorong'i", "light" to "Yorug'").forEach { (mode, label) ->
                                FilterChip(
                                    selected = settings.themeMode == mode,
                                    onClick = { scope.launch { viewModel.preferences.setThemeMode(mode) } },
                                    label = { Text(label, fontSize = 12.sp) },
                                    shape = RoundedCornerShape(12.dp)
                                )
                            }
                        }
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                    // Icon Shape
                    SettingOptionRow(title = "Belgilar shakli") {
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            listOf(
                                "rounded" to "Yumaloq",
                                "circle" to "Aylana",
                                "squircle" to "Skvinkl",
                                "square" to "To'rtburchak"
                            ).forEach { (shape, label) ->
                                FilterChip(
                                    selected = settings.iconShape == shape,
                                    onClick = { scope.launch { viewModel.preferences.setIconShape(shape) } },
                                    label = { Text(label, fontSize = 11.sp) },
                                    shape = RoundedCornerShape(12.dp)
                                )
                            }
                        }
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                    // Icon Size
                    SettingOptionRow(title = "Belgilar hajmi") {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf("small" to "Kichik", "medium" to "O'rtacha", "large" to "Katta").forEach { (size, label) ->
                                FilterChip(
                                    selected = settings.iconSize == size,
                                    onClick = { scope.launch { viewModel.preferences.setIconSize(size) } },
                                    label = { Text(label, fontSize = 12.sp) },
                                    shape = RoundedCornerShape(12.dp)
                                )
                            }
                        }
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                    // Grid Size
                    SettingOptionRow(title = "Bosh ekran to'ri") {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf("4x4", "4x5", "5x5").forEach { grid ->
                                FilterChip(
                                    selected = settings.gridSize == grid,
                                    onClick = { scope.launch { viewModel.preferences.setGridSize(grid) } },
                                    label = { Text(grid, fontSize = 12.sp) },
                                    shape = RoundedCornerShape(12.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 2. WALLPAPER
                SectionTitle(title = "Fon rasmi (Wallpaper)", icon = Icons.Default.Image)
                SettingsCard {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = { scope.launch { viewModel.preferences.setWallpaperType("system") } },
                            shape = RoundedCornerShape(12.dp),
                            colors = if (settings.wallpaperType == "system") ButtonDefaults.buttonColors() else ButtonDefaults.filledTonalButtonColors(),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Tizim fon rasmi", fontSize = 12.sp)
                        }

                        Button(
                            onClick = { galleryLauncher.launch("image/*") },
                            shape = RoundedCornerShape(12.dp),
                            colors = if (settings.wallpaperType == "custom") ButtonDefaults.buttonColors() else ButtonDefaults.filledTonalButtonColors(),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Galereyadan tanlash", fontSize = 12.sp)
                        }
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                    SettingToggleRow(
                        title = "Qoraytirilgan qatlam",
                        subtitle = "Yozuvlar yaxshi ko'rinishi uchun fonga qora tus berish",
                        checked = settings.wallpaperDarkOverlay > 0f,
                        onCheckedChange = { checked ->
                            scope.launch { viewModel.preferences.setWallpaperDarkOverlay(if (checked) 0.25f else 0f) }
                        }
                    )

                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                    SettingToggleRow(
                        title = "Xiralashtirish effekti (Blur)",
                        subtitle = "Bosh ekranda fon rasmini yumshoq xiralashtirish",
                        checked = settings.wallpaperBlur,
                        onCheckedChange = { checked ->
                            scope.launch { viewModel.preferences.setWallpaperBlur(checked) }
                        }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 3. HOME SCREEN & DOCK
                SectionTitle(title = "Bosh ekran va Dok", icon = Icons.Default.Widgets)
                SettingsCard {
                    SettingToggleRow(
                        title = "24 soatlik soat formati",
                        subtitle = "12 yoki 24 soatlik ko'rinish",
                        checked = settings.clockFormat24,
                        onCheckedChange = { scope.launch { viewModel.preferences.setClockFormat24(it) } }
                    )

                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                    SettingToggleRow(
                        title = "Soniyalarni ko'rsatish",
                        subtitle = "Soatda soniya hisoblagich",
                        checked = settings.showSeconds,
                        onCheckedChange = { scope.launch { viewModel.preferences.setShowSeconds(it) } }
                    )

                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                    SettingToggleRow(
                        title = "Sanani ko'rsatish",
                        subtitle = "Hafta kuni va oy",
                        checked = settings.showDate,
                        onCheckedChange = { scope.launch { viewModel.preferences.setShowDate(it) } }
                    )

                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                    SettingToggleRow(
                        title = "Ob-havo ko'rsatkichi",
                        subtitle = "Harorat va ob-havo holati",
                        checked = settings.showWeather,
                        onCheckedChange = { scope.launch { viewModel.preferences.setShowWeather(it) } }
                    )

                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                    SettingToggleRow(
                        title = "Pastki panel (Dok) ko'rinishi",
                        subtitle = "Tezkor ilovalar paneli",
                        checked = settings.showDock,
                        onCheckedChange = { scope.launch { viewModel.preferences.setShowDock(it) } }
                    )

                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                    SettingToggleRow(
                        title = "Ilova nomlarini ko'rsatish",
                        subtitle = "Ilova piktogrammasi ostidagi yozuv",
                        checked = settings.showLabels,
                        onCheckedChange = { scope.launch { viewModel.preferences.setShowLabels(it) } }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 4. APPS & SORTING
                SectionTitle(title = "Ilovalar va Saralash", icon = Icons.Default.SortByAlpha)
                SettingsCard {
                    SettingOptionRow(title = "Ilovalarni saralash tartibi") {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            FilterChip(
                                selected = settings.sortOrder == "alphabetical",
                                onClick = { scope.launch { viewModel.preferences.setSortOrder("alphabetical") } },
                                label = { Text("Alifbo bo'yicha (A-Z)", fontSize = 12.sp) },
                                shape = RoundedCornerShape(12.dp)
                            )
                            FilterChip(
                                selected = settings.sortOrder == "most_used",
                                onClick = { scope.launch { viewModel.preferences.setSortOrder("most_used") } },
                                label = { Text("Ko'p ishlatilgan", fontSize = 12.sp) },
                                shape = RoundedCornerShape(12.dp)
                            )
                        }
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .clickable {
                                if (settings.hiddenAppsPin.isNotEmpty()) {
                                    pinDialogPurpose = "open_hidden"
                                    isPinDialogOpen = true
                                } else {
                                    viewModel.isHiddenAppsManagerOpen.value = true
                                }
                            }
                            .padding(vertical = 10.dp, horizontal = 4.dp)
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Yashirilgan ilovalar menejeri",
                                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium)
                            )
                            Text(
                                text = "${settings.hiddenPackages.size} ta yashirilgan ilova",
                                style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                            )
                        }
                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 5. BEHAVIOR & PERFORMANCE
                SectionTitle(title = "Harakatlar va Unumdorlik", icon = Icons.Default.Speed)
                SettingsCard {
                    SettingToggleRow(
                        title = "Unumdorlik (Tezkor) rejimi",
                        subtitle = "Kam quvvatli qurilmalarda tezlikni oshirish uchun barcha og'ir effektlarni o'chiradi",
                        checked = settings.performanceMode,
                        onCheckedChange = { scope.launch { viewModel.preferences.setPerformanceMode(it) } }
                    )

                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                    SettingToggleRow(
                        title = "Animatsiyalarni kamaytirish",
                        subtitle = "Tezkor va keskin o'tishlar",
                        checked = settings.reduceAnimations,
                        onCheckedChange = { scope.launch { viewModel.preferences.setReduceAnimations(it) } }
                    )

                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                    SettingToggleRow(
                        title = "Bosishda tebranish (Haptic feedback)",
                        subtitle = "Belgi va vidjetlarni bosganda tebranish",
                        checked = settings.hapticEnabled,
                        onCheckedChange = { scope.launch { viewModel.preferences.setHapticEnabled(it) } }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 6. ABOUT
                SectionTitle(title = "NovaHome Haqida", icon = Icons.Default.Info)
                SettingsCard {
                    Text(
                        text = "NovaHome",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    )
                    Text(
                        text = "Versiya 1.0.0",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium)
                    )
                    Text(
                        text = "Kotlin va Jetpack Compose bilan yaratilgan zamonaviy Android Home Launcher.",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 18.sp
                        ),
                        modifier = Modifier.padding(vertical = 6.dp)
                    )

                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Maxfiylik kafolati: NovaHome hech qanday shaxsiy ma'lumotlarni to'plamaydi, reklamalarsiz to'liq oflayn ishlaydi.",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                lineHeight = 16.sp
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 7. RESET SETTINGS
                SectionTitle(title = "Boshlang'ich Holatga Qaytarish", icon = Icons.Default.RestartAlt)
                SettingsCard {
                    Text(
                        text = "Launcher Sozlamalarini Qayta O'rnatish",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = "Barcha kiritilgan o'zgartirishlar, maxsus nomlar va vidjet sozlamalari zavod holatiga qaytariladi.",
                        style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
                        modifier = Modifier.padding(vertical = 6.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = { isResetDialogOpen = true },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.RestartAlt, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Barcha sozlamalarni tiklash")
                    }
                }

                if (isResetDialogOpen) {
                    AlertDialog(
                        onDismissRequest = { isResetDialogOpen = false },
                        title = { Text("Sozlamalarni tiklash") },
                        text = { Text("Haqiqatan ham launcherning barcha sozlamalarini boshlang'ich holatiga qaytarmoqchimisiz?") },
                        confirmButton = {
                            Button(
                                onClick = {
                                    viewModel.resetAllSettings()
                                    isResetDialogOpen = false
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                            ) {
                                Text("Ha, tiklash")
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { isResetDialogOpen = false }) {
                                Text("Bekor qilish")
                            }
                        }
                    )
                }

                Spacer(modifier = Modifier.height(36.dp))
            }
        }
    }
}

@Composable
private fun SectionTitle(title: String, icon: ImageVector) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                fontSize = 15.sp
            )
        )
    }
}

@Composable
private fun SettingsCard(content: @Composable () -> Unit) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            content()
        }
    }
}

@Composable
private fun SettingToggleRow(
    title: String,
    subtitle: String? = null,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) }
            .padding(vertical = 4.dp)
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Medium,
                    fontSize = 15.sp
                )
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 12.sp
                    )
                )
            }
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}

@Composable
private fun SettingOptionRow(
    title: String,
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            ),
            modifier = Modifier.padding(bottom = 6.dp)
        )
        content()
    }
}
