package com.example.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.launcherDataStore: DataStore<Preferences> by preferencesDataStore(name = "novahome_preferences")

data class LauncherSettings(
    val isFirstLaunch: Boolean = true,
    val themeMode: String = "system", // "system", "dark", "light"
    val iconShape: String = "squircle", // "circle", "rounded", "squircle", "square"
    val iconSize: String = "medium", // "small", "medium", "large"
    val gridSize: String = "4x5", // "4x4", "4x5", "5x5"
    val clockFormat24: Boolean = true,
    val showSeconds: Boolean = false,
    val showDate: Boolean = true,
    val showWeather: Boolean = true,
    val showDock: Boolean = true,
    val dockIconCount: Int = 4,
    val showLabels: Boolean = true,
    val sortOrder: String = "alphabetical", // "alphabetical", "most_used"
    val wallpaperType: String = "ios_preset", // "system", "custom", "ios_preset"
    val wallpaperUri: String? = null,
    val wallpaperDarkOverlay: Float = 0.05f,
    val wallpaperBlur: Boolean = false,
    val performanceMode: Boolean = false,
    val reduceAnimations: Boolean = false,
    val hapticEnabled: Boolean = true,
    val hiddenPackages: Set<String> = emptySet(),
    val favoritePackages: Set<String> = emptySet(),
    val dockPackages: List<String> = emptyList(),
    val launcherStyle: String = "hybrid", // "hybrid", "classic", "ios"
    val iosWallpaper: String = "ios_18_dark",
    val showDynamicIsland: Boolean = true,
    val showIosWidgets: Boolean = false,
    val iosClockStyle: String = "ios_bold", // "ios_bold", "ios_thin", "ios_serif"
    val showSystemMonitor: Boolean = true,
    val focusModeEnabled: Boolean = false,
    val hiddenAppsPin: String = "",
    val doubleTapAction: String = "lock", // "lock", "search", "booster", "drawer", "none"
    val showAssistiveTouch: Boolean = false,
    val showLockScreen: Boolean = true,
    val showClockWidget: Boolean = true,
    val cleanMinimalistMode: Boolean = false,
    val lockedPackages: Set<String> = emptySet(),
    val customAppLabels: Map<String, String> = emptyMap()
)

class LauncherPreferences(private val context: Context) {

    private val dataStore = context.launcherDataStore

    companion object {
        val KEY_FIRST_LAUNCH = booleanPreferencesKey("is_first_launch")
        val KEY_THEME_MODE = stringPreferencesKey("theme_mode")
        val KEY_ICON_SHAPE = stringPreferencesKey("icon_shape")
        val KEY_ICON_SIZE = stringPreferencesKey("icon_size")
        val KEY_GRID_SIZE = stringPreferencesKey("grid_size")
        val KEY_CLOCK_24 = booleanPreferencesKey("clock_format_24")
        val KEY_SHOW_SECONDS = booleanPreferencesKey("show_seconds")
        val KEY_SHOW_DATE = booleanPreferencesKey("show_date")
        val KEY_SHOW_WEATHER = booleanPreferencesKey("show_weather")
        val KEY_SHOW_DOCK = booleanPreferencesKey("show_dock")
        val KEY_DOCK_ICON_COUNT = intPreferencesKey("dock_icon_count")
        val KEY_SHOW_LABELS = booleanPreferencesKey("show_labels")
        val KEY_SORT_ORDER = stringPreferencesKey("sort_order")
        val KEY_WALLPAPER_TYPE = stringPreferencesKey("wallpaper_type")
        val KEY_WALLPAPER_URI = stringPreferencesKey("wallpaper_uri")
        val KEY_DARK_OVERLAY = floatPreferencesKey("wallpaper_dark_overlay")
        val KEY_WALLPAPER_BLUR = booleanPreferencesKey("wallpaper_blur")
        val KEY_PERFORMANCE_MODE = booleanPreferencesKey("performance_mode")
        val KEY_REDUCE_ANIMATIONS = booleanPreferencesKey("reduce_animations")
        val KEY_HAPTIC = booleanPreferencesKey("haptic_enabled")
        val KEY_HIDDEN_PACKAGES = stringSetPreferencesKey("hidden_packages")
        val KEY_FAVORITE_PACKAGES = stringSetPreferencesKey("favorite_packages")
        val KEY_DOCK_PACKAGES = stringPreferencesKey("dock_packages_csv")
        val KEY_LAUNCHER_STYLE = stringPreferencesKey("launcher_style")
        val KEY_IOS_WALLPAPER = stringPreferencesKey("ios_wallpaper")
        val KEY_SHOW_DYNAMIC_ISLAND = booleanPreferencesKey("show_dynamic_island")
        val KEY_SHOW_IOS_WIDGETS = booleanPreferencesKey("show_ios_widgets")
        val KEY_IOS_CLOCK_STYLE = stringPreferencesKey("ios_clock_style")
        val KEY_SHOW_SYSTEM_MONITOR = booleanPreferencesKey("show_system_monitor")
        val KEY_FOCUS_MODE = booleanPreferencesKey("focus_mode_enabled")
        val KEY_HIDDEN_APPS_PIN = stringPreferencesKey("hidden_apps_pin")
        val KEY_DOUBLE_TAP_ACTION = stringPreferencesKey("double_tap_action")
        val KEY_SHOW_ASSISTIVE_TOUCH = booleanPreferencesKey("show_assistive_touch")
        val KEY_SHOW_LOCK_SCREEN = booleanPreferencesKey("show_lock_screen")
        val KEY_SHOW_CLOCK_WIDGET = booleanPreferencesKey("show_clock_widget")
        val KEY_CLEAN_MINIMALIST = booleanPreferencesKey("clean_minimalist_mode")
        val KEY_LOCKED_PACKAGES = stringSetPreferencesKey("locked_packages")
        val KEY_CUSTOM_APP_LABELS = stringPreferencesKey("custom_app_labels")
    }

    val settingsFlow: Flow<LauncherSettings> = dataStore.data.map { prefs ->
        val dockCsv = prefs[KEY_DOCK_PACKAGES] ?: ""
        val dockList = if (dockCsv.isNotEmpty()) dockCsv.split(",").filter { it.isNotBlank() } else emptyList()

        val labelsRaw = prefs[KEY_CUSTOM_APP_LABELS] ?: ""
        val labelsMap = if (labelsRaw.isNotBlank()) {
            labelsRaw.split(";;;").mapNotNull { entry ->
                val parts = entry.split(":::")
                if (parts.size == 2) parts[0] to parts[1] else null
            }.toMap()
        } else emptyMap()

        LauncherSettings(
            isFirstLaunch = prefs[KEY_FIRST_LAUNCH] ?: true,
            themeMode = prefs[KEY_THEME_MODE] ?: "system",
            iconShape = prefs[KEY_ICON_SHAPE] ?: "squircle",
            iconSize = prefs[KEY_ICON_SIZE] ?: "medium",
            gridSize = prefs[KEY_GRID_SIZE] ?: "4x5",
            clockFormat24 = prefs[KEY_CLOCK_24] ?: true,
            showSeconds = prefs[KEY_SHOW_SECONDS] ?: false,
            showDate = prefs[KEY_SHOW_DATE] ?: true,
            showWeather = prefs[KEY_SHOW_WEATHER] ?: true,
            showDock = prefs[KEY_SHOW_DOCK] ?: true,
            dockIconCount = prefs[KEY_DOCK_ICON_COUNT] ?: 4,
            showLabels = prefs[KEY_SHOW_LABELS] ?: true,
            sortOrder = prefs[KEY_SORT_ORDER] ?: "alphabetical",
            wallpaperType = prefs[KEY_WALLPAPER_TYPE] ?: "ios_preset",
            wallpaperUri = prefs[KEY_WALLPAPER_URI],
            wallpaperDarkOverlay = prefs[KEY_DARK_OVERLAY] ?: 0.05f,
            wallpaperBlur = prefs[KEY_WALLPAPER_BLUR] ?: false,
            performanceMode = prefs[KEY_PERFORMANCE_MODE] ?: false,
            reduceAnimations = prefs[KEY_REDUCE_ANIMATIONS] ?: false,
            hapticEnabled = prefs[KEY_HAPTIC] ?: true,
            hiddenPackages = prefs[KEY_HIDDEN_PACKAGES] ?: emptySet(),
            favoritePackages = prefs[KEY_FAVORITE_PACKAGES] ?: emptySet(),
            dockPackages = dockList,
            launcherStyle = prefs[KEY_LAUNCHER_STYLE] ?: "hybrid",
            iosWallpaper = prefs[KEY_IOS_WALLPAPER] ?: "ios_18_dark",
            showDynamicIsland = prefs[KEY_SHOW_DYNAMIC_ISLAND] ?: true,
            showIosWidgets = prefs[KEY_SHOW_IOS_WIDGETS] ?: false,
            iosClockStyle = prefs[KEY_IOS_CLOCK_STYLE] ?: "ios_bold",
            showSystemMonitor = prefs[KEY_SHOW_SYSTEM_MONITOR] ?: true,
            focusModeEnabled = prefs[KEY_FOCUS_MODE] ?: false,
            hiddenAppsPin = prefs[KEY_HIDDEN_APPS_PIN] ?: "",
            doubleTapAction = prefs[KEY_DOUBLE_TAP_ACTION] ?: "lock",
            showAssistiveTouch = prefs[KEY_SHOW_ASSISTIVE_TOUCH] ?: false,
            showLockScreen = prefs[KEY_SHOW_LOCK_SCREEN] ?: true,
            showClockWidget = prefs[KEY_SHOW_CLOCK_WIDGET] ?: true,
            cleanMinimalistMode = prefs[KEY_CLEAN_MINIMALIST] ?: false,
            lockedPackages = prefs[KEY_LOCKED_PACKAGES] ?: emptySet(),
            customAppLabels = labelsMap
        )
    }

    suspend fun setFirstLaunchCompleted() {
        dataStore.edit { it[KEY_FIRST_LAUNCH] = false }
    }

    suspend fun setThemeMode(mode: String) {
        dataStore.edit { it[KEY_THEME_MODE] = mode }
    }

    suspend fun setIconShape(shape: String) {
        dataStore.edit { it[KEY_ICON_SHAPE] = shape }
    }

    suspend fun setIconSize(size: String) {
        dataStore.edit { it[KEY_ICON_SIZE] = size }
    }

    suspend fun setGridSize(grid: String) {
        dataStore.edit { it[KEY_GRID_SIZE] = grid }
    }

    suspend fun setClockFormat24(is24: Boolean) {
        dataStore.edit { it[KEY_CLOCK_24] = is24 }
    }

    suspend fun setShowSeconds(show: Boolean) {
        dataStore.edit { it[KEY_SHOW_SECONDS] = show }
    }

    suspend fun setShowDate(show: Boolean) {
        dataStore.edit { it[KEY_SHOW_DATE] = show }
    }

    suspend fun setShowWeather(show: Boolean) {
        dataStore.edit { it[KEY_SHOW_WEATHER] = show }
    }

    suspend fun setShowDock(show: Boolean) {
        dataStore.edit { it[KEY_SHOW_DOCK] = show }
    }

    suspend fun setDockIconCount(count: Int) {
        dataStore.edit { it[KEY_DOCK_ICON_COUNT] = count }
    }

    suspend fun setShowLabels(show: Boolean) {
        dataStore.edit { it[KEY_SHOW_LABELS] = show }
    }

    suspend fun setSortOrder(order: String) {
        dataStore.edit { it[KEY_SORT_ORDER] = order }
    }

    suspend fun setWallpaperType(type: String, uri: String? = null) {
        dataStore.edit {
            it[KEY_WALLPAPER_TYPE] = type
            if (uri != null) {
                it[KEY_WALLPAPER_URI] = uri
            } else if (type == "system") {
                it.remove(KEY_WALLPAPER_URI)
            }
        }
    }

    suspend fun setWallpaperDarkOverlay(overlay: Float) {
        dataStore.edit { it[KEY_DARK_OVERLAY] = overlay }
    }

    suspend fun setWallpaperBlur(blur: Boolean) {
        dataStore.edit { it[KEY_WALLPAPER_BLUR] = blur }
    }

    suspend fun setPerformanceMode(enabled: Boolean) {
        dataStore.edit {
            it[KEY_PERFORMANCE_MODE] = enabled
            if (enabled) {
                it[KEY_REDUCE_ANIMATIONS] = true
                it[KEY_WALLPAPER_BLUR] = false
            }
        }
    }

    suspend fun setReduceAnimations(reduce: Boolean) {
        dataStore.edit { it[KEY_REDUCE_ANIMATIONS] = reduce }
    }

    suspend fun setHapticEnabled(enabled: Boolean) {
        dataStore.edit { it[KEY_HAPTIC] = enabled }
    }

    suspend fun toggleHidePackage(packageName: String) {
        dataStore.edit { prefs ->
            val current = prefs[KEY_HIDDEN_PACKAGES] ?: emptySet()
            if (current.contains(packageName)) {
                prefs[KEY_HIDDEN_PACKAGES] = current - packageName
            } else {
                prefs[KEY_HIDDEN_PACKAGES] = current + packageName
            }
        }
    }

    suspend fun toggleFavorite(packageName: String) {
        dataStore.edit { prefs ->
            val current = prefs[KEY_FAVORITE_PACKAGES] ?: emptySet()
            if (current.contains(packageName)) {
                prefs[KEY_FAVORITE_PACKAGES] = current - packageName
            } else {
                prefs[KEY_FAVORITE_PACKAGES] = current + packageName
            }
        }
    }

    suspend fun setDockPackages(packages: List<String>) {
        dataStore.edit { prefs ->
            prefs[KEY_DOCK_PACKAGES] = packages.joinToString(",")
        }
    }

    suspend fun setLauncherStyle(style: String) {
        dataStore.edit { it[KEY_LAUNCHER_STYLE] = style }
    }

    suspend fun setIosWallpaper(wallpaperId: String) {
        dataStore.edit {
            it[KEY_IOS_WALLPAPER] = wallpaperId
            it[KEY_WALLPAPER_TYPE] = "ios_preset"
        }
    }

    suspend fun setShowDynamicIsland(show: Boolean) {
        dataStore.edit { it[KEY_SHOW_DYNAMIC_ISLAND] = show }
    }

    suspend fun setShowIosWidgets(show: Boolean) {
        dataStore.edit { it[KEY_SHOW_IOS_WIDGETS] = show }
    }

    suspend fun setIosClockStyle(clockStyle: String) {
        dataStore.edit { it[KEY_IOS_CLOCK_STYLE] = clockStyle }
    }

    suspend fun setShowSystemMonitor(show: Boolean) {
        dataStore.edit { it[KEY_SHOW_SYSTEM_MONITOR] = show }
    }

    suspend fun setFocusMode(enabled: Boolean) {
        dataStore.edit { it[KEY_FOCUS_MODE] = enabled }
    }

    suspend fun setHiddenAppsPin(pin: String) {
        dataStore.edit { it[KEY_HIDDEN_APPS_PIN] = pin }
    }

    suspend fun setDoubleTapAction(action: String) {
        dataStore.edit { it[KEY_DOUBLE_TAP_ACTION] = action }
    }

    suspend fun setShowAssistiveTouch(show: Boolean) {
        dataStore.edit { it[KEY_SHOW_ASSISTIVE_TOUCH] = show }
    }

    suspend fun setShowLockScreen(show: Boolean) {
        dataStore.edit { it[KEY_SHOW_LOCK_SCREEN] = show }
    }

    suspend fun setShowClockWidget(show: Boolean) {
        dataStore.edit { it[KEY_SHOW_CLOCK_WIDGET] = show }
    }

    suspend fun setCleanMinimalistMode(clean: Boolean) {
        dataStore.edit {
            it[KEY_CLEAN_MINIMALIST] = clean
            if (clean) {
                it[KEY_SHOW_SYSTEM_MONITOR] = false
                it[KEY_SHOW_LABELS] = false
            }
        }
    }

    suspend fun toggleLockPackage(packageName: String) {
        dataStore.edit { prefs ->
            val current = prefs[KEY_LOCKED_PACKAGES] ?: emptySet()
            if (current.contains(packageName)) {
                prefs[KEY_LOCKED_PACKAGES] = current - packageName
            } else {
                prefs[KEY_LOCKED_PACKAGES] = current + packageName
            }
        }
    }

    suspend fun setCustomAppLabel(packageName: String, label: String) {
        dataStore.edit { prefs ->
            val raw = prefs[KEY_CUSTOM_APP_LABELS] ?: ""
            val currentMap = if (raw.isNotBlank()) {
                raw.split(";;;").mapNotNull { entry ->
                    val parts = entry.split(":::")
                    if (parts.size == 2) parts[0] to parts[1] else null
                }.toMap().toMutableMap()
            } else mutableMapOf()

            if (label.isBlank()) {
                currentMap.remove(packageName)
            } else {
                currentMap[packageName] = label.trim()
            }

            prefs[KEY_CUSTOM_APP_LABELS] = currentMap.entries.joinToString(";;;") { "${it.key}:::${it.value}" }
        }
    }

    suspend fun resetAllSettings() {
        dataStore.edit { it.clear() }
    }
}
