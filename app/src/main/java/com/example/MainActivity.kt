package com.example

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.launcher.apps.AppInfo
import com.example.launcher.intents.LauncherIntents
import com.example.launcher.widgets.WidgetManager
import com.example.ui.appdrawer.AppDrawerSheet
import com.example.ui.components.AppContextMenu
import com.example.ui.components.RenameAppDialog
import com.example.ui.home.AddAppPickerDialog
import com.example.ui.home.FirstLaunchDialog
import com.example.ui.home.HomeScreen
import com.example.ui.home.IosControlCenter
import com.example.ui.home.LockScreenOverlay
import com.example.ui.home.WidgetPickerDialog
import com.example.ui.search.SearchOverlay
import com.example.ui.settings.HiddenAppsDialog
import com.example.ui.settings.PinAuthDialog
import com.example.ui.settings.SettingsScreen
import com.example.ui.theme.NovaHomeTheme
import com.example.viewmodel.LauncherViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: LauncherViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val settings by viewModel.settings.collectAsState()
            val isDarkTheme = when (settings.themeMode) {
                "dark" -> true
                "light" -> false
                else -> isSystemInDarkTheme()
            }

            NovaHomeTheme(darkTheme = isDarkTheme) {
                val isDrawerOpen by viewModel.isAppDrawerOpen.collectAsState()
                val isSearchOpen by viewModel.isSearchOverlayOpen.collectAsState()
                val isSettingsOpen by viewModel.isSettingsOpen.collectAsState()
                val isCustomizing by viewModel.isCustomizing.collectAsState()
                val isWidgetPickerOpen by viewModel.isWidgetPickerOpen.collectAsState()
                val isAddAppPickerOpen by viewModel.isAddAppPickerOpen.collectAsState()
                val isHiddenAppsOpen by viewModel.isHiddenAppsManagerOpen.collectAsState()
                val isControlCenterOpen by viewModel.isControlCenterOpen.collectAsState()
                val selectedAppForMenu by viewModel.selectedAppForMenu.collectAsState()
                val isScreenLocked by viewModel.isScreenLocked.collectAsState()
                val appToRename by viewModel.appToRename.collectAsState()
                val lockedAppToAuthenticate by viewModel.lockedAppToAuthenticate.collectAsState()

                val allApps by viewModel.allApps.collectAsState()
                val drawerApps by viewModel.drawerApps.collectAsState()
                val recentApps by viewModel.recentApps.collectAsState()
                val favoriteApps by viewModel.favoriteApps.collectAsState()
                val isLoadingApps by viewModel.isLoadingApps.collectAsState()
                val searchQuery by viewModel.searchQuery.collectAsState()
                val selectedCategory by viewModel.selectedCategory.collectAsState()

                val handleAppLaunch: (AppInfo) -> Unit = { app ->
                    if (settings.lockedPackages.contains(app.packageName)) {
                        viewModel.lockedAppToAuthenticate.value = app
                    } else {
                        viewModel.onAppLaunched(app)
                        LauncherIntents.launchApp(this@MainActivity, app.packageName, app.activityName)
                    }
                }

                // Launcher Back Button Handling
                BackHandler(enabled = isScreenLocked || isSettingsOpen || isSearchOpen || isDrawerOpen || isCustomizing || isControlCenterOpen) {
                    when {
                        isScreenLocked -> { /* Remain on lock screen until unlocked */ }
                        isControlCenterOpen -> viewModel.isControlCenterOpen.value = false
                        isSettingsOpen -> viewModel.isSettingsOpen.value = false
                        isSearchOpen -> viewModel.isSearchOverlayOpen.value = false
                        isDrawerOpen -> {
                            viewModel.isAppDrawerOpen.value = false
                            viewModel.searchQuery.value = ""
                        }
                        isCustomizing -> viewModel.isCustomizing.value = false
                    }
                }

                Box(modifier = Modifier.fillMaxSize()) {
                    // Root: Home Screen
                    HomeScreen(viewModel = viewModel)

                    // App Drawer Sheet
                    AnimatedVisibility(
                        visible = isDrawerOpen,
                        enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                        exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
                    ) {
                        AppDrawerSheet(
                            apps = drawerApps,
                            isLoading = isLoadingApps,
                            searchQuery = searchQuery,
                            onSearchQueryChange = { viewModel.searchQuery.value = it },
                            selectedCategory = selectedCategory,
                            onCategorySelected = { viewModel.selectedCategory.value = it },
                            onAppClick = { app ->
                                handleAppLaunch(app)
                                viewModel.isAppDrawerOpen.value = false
                            },
                            onAppLongClick = { app ->
                                viewModel.selectedAppForMenu.value = app
                            },
                            onClose = {
                                viewModel.isAppDrawerOpen.value = false
                                viewModel.searchQuery.value = ""
                            },
                            iconSize = settings.iconSize,
                            iconShape = settings.iconShape,
                            showLabels = settings.showLabels,
                            hapticEnabled = settings.hapticEnabled
                        )
                    }

                    // Search Overlay
                    AnimatedVisibility(
                        visible = isSearchOpen,
                        enter = slideInVertically(initialOffsetY = { -it / 2 }) + fadeIn(),
                        exit = slideOutVertically(targetOffsetY = { -it / 2 }) + fadeOut()
                    ) {
                        SearchOverlay(
                            allApps = allApps,
                            recentApps = recentApps,
                            favoriteApps = favoriteApps,
                            onAppClick = { app ->
                                handleAppLaunch(app)
                                viewModel.isSearchOverlayOpen.value = false
                            },
                            onAppLongClick = { app ->
                                viewModel.selectedAppForMenu.value = app
                            },
                            onClose = { viewModel.isSearchOverlayOpen.value = false },
                            iconSize = settings.iconSize,
                            iconShape = settings.iconShape,
                            hapticEnabled = settings.hapticEnabled
                        )
                    }

                    // Settings Screen
                    AnimatedVisibility(
                        visible = isSettingsOpen,
                        enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                        exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
                    ) {
                        SettingsScreen(
                            viewModel = viewModel,
                            settings = settings,
                            onClose = { viewModel.isSettingsOpen.value = false }
                        )
                    }

                    // iOS Control Center
                    AnimatedVisibility(
                        visible = isControlCenterOpen,
                        enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
                        exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut()
                    ) {
                        IosControlCenter(
                            onClose = { viewModel.isControlCenterOpen.value = false },
                            onOpenSettings = {
                                viewModel.isControlCenterOpen.value = false
                                viewModel.isSettingsOpen.value = true
                            },
                            onToggleTheme = {
                                val nextTheme = if (isDarkTheme) "light" else "dark"
                                viewModel.setThemeMode(nextTheme)
                            },
                            isDarkTheme = isDarkTheme,
                            hapticEnabled = settings.hapticEnabled
                        )
                    }

                    // App Context Menu Sheet
                    selectedAppForMenu?.let { app ->
                        val isFavorite = settings.favoritePackages.contains(app.packageName)
                        val isLocked = settings.lockedPackages.contains(app.packageName)
                        AppContextMenu(
                            app = app,
                            isFavorite = isFavorite,
                            isLocked = isLocked,
                            onDismiss = { viewModel.selectedAppForMenu.value = null },
                            onOpen = {
                                handleAppLaunch(app)
                            },
                            onAddToHome = {
                                viewModel.addAppToHomeScreen(app)
                            },
                            onToggleFavorite = {
                                viewModel.toggleFavorite(app)
                            },
                            onAppInfo = {
                                LauncherIntents.openAppInfo(this@MainActivity, app.packageName)
                            },
                            onUninstall = {
                                LauncherIntents.uninstallApp(this@MainActivity, app.packageName)
                            },
                            onToggleHide = {
                                viewModel.toggleHideApp(app)
                            },
                            onToggleLock = {
                                viewModel.toggleLockPackage(app)
                            },
                            onRenameApp = {
                                viewModel.appToRename.value = app
                            }
                        )
                    }

                    // Rename App Dialog
                    appToRename?.let { app ->
                        RenameAppDialog(
                            app = app,
                            onConfirm = { newName ->
                                viewModel.setCustomAppLabel(app, newName)
                            },
                            onReset = {
                                viewModel.setCustomAppLabel(app, "")
                            },
                            onDismiss = {
                                viewModel.appToRename.value = null
                            }
                        )
                    }

                    // Locked App PIN Authentication Dialog
                    lockedAppToAuthenticate?.let { app ->
                        PinAuthDialog(
                            currentPin = settings.hiddenAppsPin,
                            onSuccess = {
                                val target = app
                                viewModel.lockedAppToAuthenticate.value = null
                                viewModel.onAppLaunched(target)
                                LauncherIntents.launchApp(this@MainActivity, target.packageName, target.activityName)
                            },
                            onDismiss = {
                                viewModel.lockedAppToAuthenticate.value = null
                            },
                            onSetNewPin = { newPin ->
                                viewModel.setHiddenAppsPin(newPin)
                            },
                            hapticEnabled = settings.hapticEnabled
                        )
                    }

                    // Built-in Launcher Lock Screen
                    LockScreenOverlay(
                        isLocked = isScreenLocked,
                        onUnlock = { viewModel.unlockScreen() },
                        pinCode = settings.hiddenAppsPin,
                        hapticEnabled = settings.hapticEnabled,
                        clockStyle = settings.iosClockStyle
                    )

                    // Widget Picker Dialog
                    if (isWidgetPickerOpen) {
                        WidgetPickerDialog(
                            widgetManager = viewModel.widgetManager,
                            onWidgetSelected = { widgetId ->
                                viewModel.addWidgetToHomeScreen(widgetId)
                            },
                            onDismiss = { viewModel.isWidgetPickerOpen.value = false }
                        )
                    }

                    // Add App Picker Dialog
                    if (isAddAppPickerOpen) {
                        AddAppPickerDialog(
                            apps = allApps,
                            onAppSelected = { app ->
                                viewModel.addAppToHomeScreen(app)
                            },
                            onDismiss = { viewModel.isAddAppPickerOpen.value = false }
                        )
                    }

                    // Hidden Apps Manager Dialog
                    if (isHiddenAppsOpen) {
                        HiddenAppsDialog(
                            allApps = allApps,
                            hiddenPackages = settings.hiddenPackages,
                            onToggleHide = { app ->
                                viewModel.toggleHideApp(app)
                            },
                            onDismiss = { viewModel.isHiddenAppsManagerOpen.value = false }
                        )
                    }

                    // First Launch Onboarding Dialog
                    if (settings.isFirstLaunch) {
                        FirstLaunchDialog(
                            onDismiss = {
                                viewModel.completeOnboarding()
                            }
                        )
                    }
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        viewModel.widgetManager.startListening()
    }

    override fun onStop() {
        super.onStop()
        viewModel.widgetManager.stopListening()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == WidgetManager.REQUEST_BIND_APPWIDGET) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                val appWidgetId = data.getIntExtra(android.appwidget.AppWidgetManager.EXTRA_APPWIDGET_ID, -1)
                if (appWidgetId != -1) {
                    viewModel.addWidgetToHomeScreen(appWidgetId)
                }
            }
        }
    }
}

