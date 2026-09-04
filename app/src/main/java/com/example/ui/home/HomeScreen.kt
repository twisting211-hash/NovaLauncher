package com.example.ui.home

import android.net.Uri
import android.os.Build
import android.widget.FrameLayout
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import coil.compose.AsyncImage
import com.example.data.local.HomeScreenItemEntity
import com.example.launcher.apps.AppInfo
import com.example.launcher.intents.LauncherIntents
import com.example.ui.components.AppIconItem
import com.example.ui.components.AssistiveTouchPad
import com.example.ui.theme.IosWallpaperView
import com.example.utils.HapticFeedbackUtil
import com.example.viewmodel.LauncherViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    viewModel: LauncherViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val settings by viewModel.settings.collectAsState()
    val allApps by viewModel.allApps.collectAsState()
    val dockApps by viewModel.dockApps.collectAsState()
    val homeItems by viewModel.homeItems.collectAsState()
    val userPageCount by viewModel.pageCount.collectAsState()
    val isCustomizing by viewModel.isCustomizing.collectAsState()

    val isIosStyle = settings.launcherStyle == "ios"
    val isHybridStyle = settings.launcherStyle == "hybrid"
    // In iOS mode, we add an extra page for the Apple "App Library" (Ilovalar Kutubxonasi)
    val totalPages = userPageCount + (if (isIosStyle) 1 else 0)
    val pagerState = rememberPagerState(pageCount = { totalPages })

    // Sync currentPageIndex
    LaunchedEffect(pagerState.currentPage) {
        if (pagerState.currentPage < userPageCount) {
            viewModel.currentPageIndex.value = pagerState.currentPage
        }
    }

    LaunchedEffect(viewModel.currentPageIndex.value) {
        if (pagerState.currentPage != viewModel.currentPageIndex.value && viewModel.currentPageIndex.value < totalPages) {
            pagerState.animateScrollToPage(viewModel.currentPageIndex.value)
        }
    }

    // Zen Focus Mode takes over screen when enabled
    if (settings.focusModeEnabled) {
        FocusModeScreen(
            allApps = allApps,
            clockFormat24 = settings.clockFormat24,
            onExitFocus = { viewModel.setFocusMode(false) },
            onAppClick = { app ->
                viewModel.onAppLaunched(app)
                LauncherIntents.launchApp(context, app.packageName, app.activityName)
            },
            hapticEnabled = settings.hapticEnabled,
            modifier = modifier
        )
        return
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .testTag("home_screen")
    ) {
        // 1. Wallpaper Layer
        WallpaperBackground(
            wallpaperType = settings.wallpaperType,
            wallpaperUri = settings.wallpaperUri,
            iosWallpaper = settings.iosWallpaper,
            darkOverlay = settings.wallpaperDarkOverlay,
            blur = settings.wallpaperBlur && !settings.performanceMode
        )

        // 2. Gesture Container for Home Screen
        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(isCustomizing) {
                    if (!isCustomizing) {
                        detectVerticalDragGestures(
                            onVerticalDrag = { change, dragAmount ->
                                val x = change.position.x
                                val screenWidth = size.width
                                if (dragAmount > 30) {
                                    // Drag down
                                    if (x > screenWidth * 0.65f && isIosStyle) {
                                        // Swipe down from top right -> Open Control Center (iOS mode)
                                        viewModel.isControlCenterOpen.value = true
                                    } else {
                                        // Swipe down -> Open Universal Power Search (Spotlight)
                                        viewModel.isSearchOverlayOpen.value = true
                                    }
                                } else if (dragAmount < -30) {
                                    // Drag up -> Open Drawer
                                    viewModel.isAppDrawerOpen.value = true
                                }
                            }
                        )
                    }
                }
                .pointerInput(isCustomizing) {
                    detectTapGestures(
                        onLongPress = {
                            HapticFeedbackUtil.performHaptic(context, settings.hapticEnabled)
                            viewModel.isCustomizing.value = true
                        },
                        onDoubleTap = {
                            HapticFeedbackUtil.performHaptic(context, settings.hapticEnabled)
                            when (settings.doubleTapAction) {
                                "lock" -> viewModel.lockScreen()
                                "search" -> viewModel.isSearchOverlayOpen.value = true
                                "booster" -> {
                                    System.gc()
                                    Toast.makeText(context, "Operativ xotira (RAM) tozalandi!", Toast.LENGTH_SHORT).show()
                                }
                                "drawer" -> viewModel.isAppDrawerOpen.value = true
                                "focus" -> viewModel.setFocusMode(true)
                                else -> {}
                            }
                        }
                    )
                }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
            ) {
                // Top Status Bar: Dynamic Island
                if (settings.showDynamicIsland) {
                    DynamicIsland(
                        onOpenControlCenter = {
                            viewModel.isControlCenterOpen.value = true
                        },
                        hapticEnabled = settings.hapticEnabled
                    )
                }

                // Top Clock & Date Widget (Respect clean screen toggle)
                if (settings.showClockWidget && !settings.cleanMinimalistMode) {
                    ClockWidget(
                        format24 = settings.clockFormat24,
                        showSeconds = settings.showSeconds,
                        showDate = settings.showDate,
                        showWeather = settings.showWeather,
                        iosClockStyle = if (isIosStyle || isHybridStyle) settings.iosClockStyle else "classic",
                        onClockClick = {
                            val clockApp = allApps.find { it.packageName.contains("clock") }
                            if (clockApp != null) {
                                LauncherIntents.launchApp(context, clockApp.packageName, clockApp.activityName)
                            }
                        }
                    )
                }

                // Multi-Page Pager
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) { pageIndex ->
                    if (isIosStyle && pageIndex == userPageCount) {
                        // The iOS App Library page
                        IosAppLibrary(
                            allApps = allApps,
                            onAppClick = { app ->
                                viewModel.onAppLaunched(app)
                                LauncherIntents.launchApp(context, app.packageName, app.activityName)
                            },
                            onAppLongClick = { app ->
                                viewModel.selectedAppForMenu.value = app
                            },
                            onOpenSearch = {
                                viewModel.isSearchOverlayOpen.value = true
                            },
                            hapticEnabled = settings.hapticEnabled
                        )
                    } else {
                        // Standard Page: contains widgets and apps
                        val pageItems = homeItems.filter { it.pageIndex == pageIndex }

                        val gridColumns = when (settings.gridSize) {
                            "4x4" -> 4
                            "5x5" -> 5
                            else -> 4
                        }

                        Column(modifier = Modifier.fillMaxSize()) {
                            // On Page 0, if System Monitor is active, display Device Performance Dashboard
                            if (settings.showSystemMonitor && pageIndex == 0) {
                                SystemDashboardCard(
                                    onDismiss = { viewModel.setShowSystemMonitor(false) },
                                    hapticEnabled = settings.hapticEnabled
                                )
                            }

                            // On Page 0, if iOS mode and iOS widgets enabled, show 2x2 Weather + Calendar widgets
                            if (isIosStyle && settings.showIosWidgets && pageIndex == 0) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp, vertical = 6.dp),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    IosWeatherWidget(
                                        onClick = {
                                            viewModel.isSearchOverlayOpen.value = true
                                        },
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(134.dp)
                                    )
                                    IosCalendarWidget(
                                        onClick = {
                                            val calApp = allApps.find { it.packageName.contains("calendar") }
                                            if (calApp != null) {
                                                LauncherIntents.launchApp(context, calApp.packageName, calApp.activityName)
                                            }
                                        },
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(134.dp)
                                    )
                                }
                            }

                            LazyVerticalGrid(
                                columns = GridCells.Fixed(gridColumns),
                                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
                                modifier = Modifier.fillMaxSize()
                            ) {
                                items(pageItems, key = { it.id }) { item ->
                                    if (item.itemType == "app" && item.packageName != null) {
                                        val app = allApps.find { it.packageName == item.packageName }
                                        if (app != null) {
                                            val customLabel = settings.customAppLabels[app.packageName]
                                            val displayedApp = if (customLabel != null) app.copy(label = customLabel) else app

                                            Box(contentAlignment = Alignment.TopEnd) {
                                                AppIconItem(
                                                    app = displayedApp,
                                                    onClick = {
                                                        if (isCustomizing) {
                                                            viewModel.selectedHomeItemForMenu.value = item
                                                        } else {
                                                            if (settings.lockedPackages.contains(app.packageName)) {
                                                                viewModel.lockedAppToAuthenticate.value = app
                                                            } else {
                                                                viewModel.onAppLaunched(app)
                                                                LauncherIntents.launchApp(context, app.packageName, app.activityName)
                                                            }
                                                        }
                                                    },
                                                    onLongClick = {
                                                        HapticFeedbackUtil.performHaptic(context, settings.hapticEnabled)
                                                        if (!isCustomizing) {
                                                            viewModel.selectedAppForMenu.value = displayedApp
                                                        } else {
                                                            viewModel.removeHomeScreenItem(item)
                                                        }
                                                    },
                                                    iconSize = settings.iconSize,
                                                    iconShape = if (isIosStyle || isHybridStyle) "squircle" else settings.iconShape,
                                                    showLabel = settings.showLabels && !settings.cleanMinimalistMode,
                                                    textColor = Color.White,
                                                    hapticEnabled = settings.hapticEnabled,
                                                    isJiggling = isCustomizing,
                                                    onDeleteClick = {
                                                        viewModel.removeHomeScreenItem(item)
                                                    }
                                                )
                                            }
                                        }
                                    } else if (item.itemType == "widget" && item.appWidgetId != null) {
                                        // Hosted Android AppWidget
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(130.dp)
                                                .padding(8.dp)
                                                .clip(RoundedCornerShape(20.dp))
                                                .background(Color.Black.copy(alpha = 0.25f))
                                        ) {
                                            AndroidView(
                                                factory = { ctx ->
                                                    val hostView = viewModel.widgetManager.createWidgetView(ctx, item.appWidgetId)
                                                    hostView ?: FrameLayout(ctx)
                                                },
                                                modifier = Modifier.fillMaxSize()
                                            )

                                            if (isCustomizing) {
                                                IconButton(
                                                    onClick = { viewModel.removeHomeScreenItem(item) },
                                                    modifier = Modifier
                                                        .align(Alignment.TopEnd)
                                                        .size(26.dp)
                                                        .clip(CircleShape)
                                                        .background(MaterialTheme.colorScheme.error)
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.Close,
                                                        contentDescription = "Vidjetni o'chirish",
                                                        tint = Color.White,
                                                        modifier = Modifier.size(16.dp)
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Page Indicator Dots
                if (totalPages > 1) {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        repeat(totalPages) { index ->
                            val isSelected = pagerState.currentPage == index
                            Box(
                                modifier = Modifier
                                    .padding(horizontal = 3.dp)
                                    .size(if (isSelected) 7.dp else 5.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (isSelected) Color.White else Color.White.copy(alpha = 0.4f)
                                    )
                            )
                        }
                    }
                }

                // Dock Bar
                AnimatedVisibility(
                    visible = !isCustomizing,
                    enter = fadeIn() + slideInVertically { it },
                    exit = fadeOut() + slideOutVertically { it }
                ) {
                    DockBar(
                        dockApps = dockApps,
                        onAppClick = { app ->
                            if (settings.lockedPackages.contains(app.packageName)) {
                                viewModel.lockedAppToAuthenticate.value = app
                            } else {
                                viewModel.onAppLaunched(app)
                                LauncherIntents.launchApp(context, app.packageName, app.activityName)
                            }
                        },
                        onAppLongClick = { app ->
                            viewModel.selectedAppForMenu.value = app
                        },
                        onOpenDrawer = {
                            viewModel.isAppDrawerOpen.value = true
                        },
                        onOpenSearch = {
                            viewModel.isSearchOverlayOpen.value = true
                        },
                        isIosStyle = isIosStyle || isHybridStyle,
                        showDock = settings.showDock,
                        showLabels = false,
                        iconSize = settings.iconSize,
                        iconShape = if (isIosStyle || isHybridStyle) "squircle" else settings.iconShape,
                        hapticEnabled = settings.hapticEnabled
                    )
                }

                // Customization Control Bar
                AnimatedVisibility(
                    visible = isCustomizing,
                    enter = fadeIn() + slideInVertically { it },
                    exit = fadeOut() + slideOutVertically { it }
                ) {
                    CustomizationBar(
                        onWallpaperClick = {
                            viewModel.isSettingsOpen.value = true
                        },
                        onWidgetsClick = {
                            viewModel.isWidgetPickerOpen.value = true
                        },
                        onSettingsClick = {
                            viewModel.isSettingsOpen.value = true
                        },
                        onAddAppClick = {
                            viewModel.isAddAppPickerOpen.value = true
                        },
                        onAddPageClick = {
                            viewModel.addNewPage()
                        },
                        onRemovePageClick = {
                            viewModel.removeCurrentPage()
                        },
                        onDoneClick = {
                            viewModel.isCustomizing.value = false
                        },
                        canRemovePage = userPageCount > 1
                    )
                }
            }
        }

        // Floating Assistive Touchpad (Virtual Touchpad)
        if (settings.showAssistiveTouch && !isCustomizing) {
            AssistiveTouchPad(
                onHomeClick = {
                    scope.launch {
                        pagerState.animateScrollToPage(0)
                    }
                },
                onLockScreen = {
                    viewModel.lockScreen()
                },
                onOpenSearch = {
                    viewModel.isSearchOverlayOpen.value = true
                },
                onOpenAppDrawer = {
                    viewModel.isAppDrawerOpen.value = true
                },
                onOpenControlCenter = {
                    viewModel.isControlCenterOpen.value = true
                },
                onOpenSettings = {
                    viewModel.isSettingsOpen.value = true
                },
                hapticEnabled = settings.hapticEnabled
            )
        }
    }
}

@Composable
private fun WallpaperBackground(
    wallpaperType: String,
    wallpaperUri: String?,
    iosWallpaper: String,
    darkOverlay: Float,
    blur: Boolean
) {
    Box(modifier = Modifier.fillMaxSize()) {
        if (wallpaperType == "ios_preset") {
            IosWallpaperView(presetId = iosWallpaper)
        } else if (wallpaperType == "custom" && !wallpaperUri.isNullOrEmpty()) {
            val blurModifier = if (blur && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                Modifier.blur(20.dp)
            } else {
                Modifier
            }

            AsyncImage(
                model = Uri.parse(wallpaperUri),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .then(blurModifier)
            )
        } else {
            // Default system / fallback to iOS 18 wallpaper
            IosWallpaperView(presetId = "ios_18_dark")
        }

        // Dark Overlay for readability
        if (darkOverlay > 0f) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = darkOverlay))
            )
        }
    }
}
