package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.HomeScreenItemEntity
import com.example.data.preferences.LauncherPreferences
import com.example.data.preferences.LauncherSettings
import com.example.data.repository.AppRepository
import com.example.launcher.apps.AppInfo
import com.example.launcher.widgets.WidgetManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class LauncherViewModel(application: Application) : AndroidViewModel(application) {

    val appRepository = AppRepository(application)
    val preferences = LauncherPreferences(application)
    private val database = AppDatabase.getDatabase(application)
    private val homeScreenDao = database.homeScreenDao()
    val widgetManager = WidgetManager(application)

    // Settings
    val settings: StateFlow<LauncherSettings> = preferences.settingsFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = LauncherSettings()
    )

    // All installed apps
    val allApps: StateFlow<List<AppInfo>> = appRepository.appsFlow
    val isLoadingApps: StateFlow<Boolean> = appRepository.isLoading

    // Search query & category in App Drawer
    val searchQuery = MutableStateFlow("")
    val selectedCategory = MutableStateFlow("Barchasi")

    // UI overlays & sheets
    val isAppDrawerOpen = MutableStateFlow(false)
    val isSearchOverlayOpen = MutableStateFlow(false)
    val isSettingsOpen = MutableStateFlow(false)
    val isCustomizing = MutableStateFlow(false)
    val isWidgetPickerOpen = MutableStateFlow(false)
    val isAddAppPickerOpen = MutableStateFlow(false)
    val isHiddenAppsManagerOpen = MutableStateFlow(false)
    val isControlCenterOpen = MutableStateFlow(false)
    val isPinAuthOpen = MutableStateFlow(false)
    val isScreenLocked = MutableStateFlow(false)
    val appToRename = MutableStateFlow<AppInfo?>(null)
    val lockedAppToAuthenticate = MutableStateFlow<AppInfo?>(null)
    val selectedAppForMenu = MutableStateFlow<AppInfo?>(null)
    val selectedHomeItemForMenu = MutableStateFlow<HomeScreenItemEntity?>(null)

    // Home Screen Items from Room
    val homeItems: StateFlow<List<HomeScreenItemEntity>> = homeScreenDao.getAllItems().stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = emptyList()
    )

    // Active Home Page count
    val pageCount = MutableStateFlow(1)
    val currentPageIndex = MutableStateFlow(0)

    // Filtered apps for App Drawer (handles search, category, hidden apps, custom labels, and sorting)
    val drawerApps: StateFlow<List<AppInfo>> = combine(
        allApps,
        searchQuery,
        selectedCategory,
        settings
    ) { apps, query, category, userSettings ->
        val customLabels = userSettings.customAppLabels
        val visibleApps = apps.filter { app ->
            !userSettings.hiddenPackages.contains(app.packageName)
        }.map { app ->
            val custom = customLabels[app.packageName]
            if (custom != null) app.copy(label = custom) else app
        }

        val categoryFiltered = if (category == "Barchasi") {
            visibleApps
        } else {
            visibleApps.filter { it.category == category }
        }

        val searchFiltered = if (query.isBlank()) {
            categoryFiltered
        } else {
            val q = query.trim().lowercase()
            categoryFiltered.filter {
                it.label.lowercase().contains(q) || it.packageName.lowercase().contains(q)
            }
        }

        when (userSettings.sortOrder) {
            "most_used" -> searchFiltered.sortedByDescending { it.launchCount }
            else -> searchFiltered.sortedBy { it.label.lowercase() }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = emptyList()
    )

    // Favorite apps list
    val favoriteApps: StateFlow<List<AppInfo>> = combine(
        allApps,
        settings
    ) { apps, userSettings ->
        val customLabels = userSettings.customAppLabels
        apps.filter { userSettings.favoritePackages.contains(it.packageName) }.map { app ->
            val custom = customLabels[app.packageName]
            if (custom != null) app.copy(label = custom) else app
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = emptyList()
    )

    // Recent / Frequently used apps (top 6 by launch count or first 6 apps)
    val recentApps: StateFlow<List<AppInfo>> = allApps.combine(settings) { apps, userSettings ->
        val customLabels = userSettings.customAppLabels
        val nonHidden = apps.filter { !userSettings.hiddenPackages.contains(it.packageName) }.map { app ->
            val custom = customLabels[app.packageName]
            if (custom != null) app.copy(label = custom) else app
        }
        val withLaunches = nonHidden.filter { it.launchCount > 0 }.sortedByDescending { it.launchCount }
        if (withLaunches.isNotEmpty()) {
            withLaunches.take(6)
        } else {
            nonHidden.take(6)
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = emptyList()
    )

    // Dock apps (filtered according to settings or default fallback)
    val dockApps: StateFlow<List<AppInfo>> = combine(
        allApps,
        settings
    ) { apps, userSettings ->
        val configured = userSettings.dockPackages
        val capacity = userSettings.dockIconCount
        val customLabels = userSettings.customAppLabels

        val processedApps = apps.map { app ->
            val custom = customLabels[app.packageName]
            if (custom != null) app.copy(label = custom) else app
        }

        if (configured.isNotEmpty()) {
            val matched = configured.mapNotNull { pkg -> processedApps.find { it.packageName == pkg } }
            matched.take(capacity)
        } else {
            // Find sensible defaults: Dialer/Phone, Messages, Browser/Chrome, Camera, Settings
            val defaults = mutableListOf<AppInfo>()
            fun pickFirstMatch(predicate: (AppInfo) -> Boolean) {
                if (defaults.size >= capacity) return
                val match = processedApps.firstOrNull { predicate(it) && !defaults.contains(it) }
                if (match != null) defaults.add(match)
            }

            pickFirstMatch { it.packageName.contains("dialer") || it.packageName.contains("phone") }
            pickFirstMatch { it.packageName.contains("message") || it.packageName.contains("sms") }
            pickFirstMatch { it.packageName.contains("chrome") || it.packageName.contains("browser") }
            pickFirstMatch { it.packageName.contains("camera") }
            pickFirstMatch { it.packageName.contains("settings") }

            // If still fewer than capacity, fill with first apps
            for (app in processedApps) {
                if (defaults.size >= capacity) break
                if (!defaults.contains(app)) defaults.add(app)
            }
            defaults
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = emptyList()
    )

    init {
        viewModelScope.launch {
            // Calculate total pages based on home items
            homeItems.collect { items ->
                val maxPage = items.maxOfOrNull { it.pageIndex } ?: 0
                pageCount.value = maxOf(1, maxPage + 1)
            }
        }

        // Initialize default shortcuts if first launch and DB is empty
        viewModelScope.launch {
            val userSettings = settings.first()
            val currentItems = homeItems.first()
            if (currentItems.isEmpty()) {
                val apps = allApps.first { it.isNotEmpty() }
                // Put 4 initial shortcuts on Page 0
                val initialShortcuts = apps.take(4)
                initialShortcuts.forEachIndexed { index, app ->
                    homeScreenDao.insertItem(
                        HomeScreenItemEntity(
                            pageIndex = 0,
                            cellX = index % 4,
                            cellY = 0,
                            itemType = "app",
                            packageName = app.packageName,
                            activityName = app.activityName
                        )
                    )
                }
            }
        }
    }

    fun completeOnboarding() {
        viewModelScope.launch {
            preferences.setFirstLaunchCompleted()
        }
    }

    fun addAppToHomeScreen(app: AppInfo, pageIndex: Int = currentPageIndex.value) {
        viewModelScope.launch {
            val itemsOnPage = homeItems.value.filter { it.pageIndex == pageIndex }
            // Find first available cell (grid 4x4 or 4x5)
            val occupied = itemsOnPage.map { it.cellX to it.cellY }.toSet()
            var targetX = 0
            var targetY = 0
            var found = false
            for (y in 0 until 5) {
                for (x in 0 until 4) {
                    if (!occupied.contains(x to y)) {
                        targetX = x
                        targetY = y
                        found = true
                        break
                    }
                }
                if (found) break
            }

            homeScreenDao.insertItem(
                HomeScreenItemEntity(
                    pageIndex = pageIndex,
                    cellX = targetX,
                    cellY = targetY,
                    itemType = "app",
                    packageName = app.packageName,
                    activityName = app.activityName
                )
            )
        }
    }

    fun addWidgetToHomeScreen(appWidgetId: Int, pageIndex: Int = currentPageIndex.value) {
        viewModelScope.launch {
            val itemsOnPage = homeItems.value.filter { it.pageIndex == pageIndex }
            val maxY = itemsOnPage.maxOfOrNull { it.cellY + it.spanY } ?: 0
            val targetY = if (maxY + 2 <= 5) maxY else 0

            homeScreenDao.insertItem(
                HomeScreenItemEntity(
                    pageIndex = pageIndex,
                    cellX = 0,
                    cellY = targetY,
                    spanX = 4,
                    spanY = 2,
                    itemType = "widget",
                    appWidgetId = appWidgetId
                )
            )
        }
    }

    fun removeHomeScreenItem(item: HomeScreenItemEntity) {
        viewModelScope.launch {
            if (item.itemType == "widget" && item.appWidgetId != null) {
                widgetManager.deleteWidgetId(item.appWidgetId)
            }
            homeScreenDao.deleteItem(item)
        }
    }

    fun addNewPage() {
        val newPage = pageCount.value
        pageCount.value = newPage + 1
        currentPageIndex.value = newPage
    }

    fun removeCurrentPage() {
        val current = currentPageIndex.value
        if (pageCount.value > 1) {
            viewModelScope.launch {
                homeScreenDao.deletePage(current)
                pageCount.value = pageCount.value - 1
                if (currentPageIndex.value >= pageCount.value) {
                    currentPageIndex.value = pageCount.value - 1
                }
            }
        }
    }

    fun toggleFavorite(app: AppInfo) {
        viewModelScope.launch {
            preferences.toggleFavorite(app.packageName)
        }
    }

    fun toggleHideApp(app: AppInfo) {
        viewModelScope.launch {
            preferences.toggleHidePackage(app.packageName)
        }
    }

    fun onAppLaunched(app: AppInfo) {
        appRepository.recordAppLaunch(app.packageName)
    }

    fun setThemeMode(mode: String) {
        viewModelScope.launch {
            preferences.setThemeMode(mode)
        }
    }

    fun setLauncherStyle(style: String) {
        viewModelScope.launch {
            preferences.setLauncherStyle(style)
        }
    }

    fun setIosWallpaper(preset: String) {
        viewModelScope.launch {
            preferences.setIosWallpaper(preset)
        }
    }

    fun setShowDynamicIsland(show: Boolean) {
        viewModelScope.launch {
            preferences.setShowDynamicIsland(show)
        }
    }

    fun setShowIosWidgets(show: Boolean) {
        viewModelScope.launch {
            preferences.setShowIosWidgets(show)
        }
    }

    fun setIosClockStyle(clockStyle: String) {
        viewModelScope.launch {
            preferences.setIosClockStyle(clockStyle)
        }
    }

    fun setShowSystemMonitor(show: Boolean) {
        viewModelScope.launch {
            preferences.setShowSystemMonitor(show)
        }
    }

    fun setFocusMode(enabled: Boolean) {
        viewModelScope.launch {
            preferences.setFocusMode(enabled)
        }
    }

    fun setHiddenAppsPin(pin: String) {
        viewModelScope.launch {
            preferences.setHiddenAppsPin(pin)
        }
    }

    fun setDoubleTapAction(action: String) {
        viewModelScope.launch {
            preferences.setDoubleTapAction(action)
        }
    }

    fun lockScreen() {
        isScreenLocked.value = true
    }

    fun unlockScreen() {
        isScreenLocked.value = false
    }

    fun setShowAssistiveTouch(show: Boolean) {
        viewModelScope.launch {
            preferences.setShowAssistiveTouch(show)
        }
    }

    fun setShowLockScreen(show: Boolean) {
        viewModelScope.launch {
            preferences.setShowLockScreen(show)
        }
    }

    fun setShowClockWidget(show: Boolean) {
        viewModelScope.launch {
            preferences.setShowClockWidget(show)
        }
    }

    fun setCleanMinimalistMode(clean: Boolean) {
        viewModelScope.launch {
            preferences.setCleanMinimalistMode(clean)
        }
    }

    fun toggleLockPackage(app: AppInfo) {
        viewModelScope.launch {
            preferences.toggleLockPackage(app.packageName)
        }
    }

    fun setCustomAppLabel(app: AppInfo, newLabel: String) {
        viewModelScope.launch {
            preferences.setCustomAppLabel(app.packageName, newLabel)
        }
    }

    fun resetAllSettings() {
        viewModelScope.launch {
            preferences.resetAllSettings()
        }
    }
}
