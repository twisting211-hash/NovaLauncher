package com.example.data.repository

import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.graphics.Bitmap
import android.os.Build
import android.util.LruCache
import com.example.launcher.apps.AppInfo
import com.example.launcher.apps.PackageEvents
import com.example.utils.BitmapUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AppRepository(private val context: Context) {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private val packageManager: PackageManager = context.packageManager
    private val iconCache = LruCache<String, Bitmap>(250)

    private val _appsFlow = MutableStateFlow<List<AppInfo>>(emptyList())
    val appsFlow: StateFlow<List<AppInfo>> = _appsFlow.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val launchCounts = mutableMapOf<String, Int>()

    init {
        loadApps()
        scope.launch {
            PackageEvents.events.collect {
                loadApps()
            }
        }
    }

    fun recordAppLaunch(packageName: String) {
        val current = launchCounts[packageName] ?: 0
        launchCounts[packageName] = current + 1
        // Refresh counts in list
        val updated = _appsFlow.value.map {
            if (it.packageName == packageName) it.copy(launchCount = current + 1) else it
        }
        _appsFlow.value = updated
    }

    fun loadApps() {
        scope.launch {
            _isLoading.value = true
            val apps = withContext(Dispatchers.IO) {
                queryInstalledLaunchableApps()
            }
            _appsFlow.value = apps
            _isLoading.value = false
        }
    }

    private fun queryInstalledLaunchableApps(): List<AppInfo> {
        val mainIntent = Intent(Intent.ACTION_MAIN, null).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }

        val resolvedList: List<ResolveInfo> = try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                packageManager.queryIntentActivities(
                    mainIntent,
                    PackageManager.ResolveInfoFlags.of(0L)
                )
            } else {
                packageManager.queryIntentActivities(mainIntent, 0)
            }
        } catch (e: Exception) {
            emptyList()
        }

        val ownPackage = context.packageName
        val appList = mutableListOf<AppInfo>()

        for (info in resolvedList) {
            val activityInfo = info.activityInfo ?: continue
            val pkg = activityInfo.packageName ?: continue
            // Exclude our own launcher from the drawer app list
            if (pkg == ownPackage) continue

            val label = try {
                info.loadLabel(packageManager).toString().trim().ifEmpty {
                    activityInfo.name
                }
            } catch (e: Exception) {
                activityInfo.name
            }

            val icon = getOrLoadIcon(info, pkg)
            val category = determineCategory(info, label, pkg)
            val count = launchCounts[pkg] ?: 0

            appList.add(
                AppInfo(
                    label = label,
                    packageName = pkg,
                    activityName = activityInfo.name,
                    icon = icon,
                    category = category,
                    launchCount = count
                )
            )
        }

        return appList.sortedBy { it.label.lowercase() }
    }

    private fun getOrLoadIcon(info: ResolveInfo, packageName: String): Bitmap? {
        val cached = iconCache.get(packageName)
        if (cached != null) return cached

        return try {
            val drawable = info.loadIcon(packageManager)
            val bitmap = BitmapUtils.drawableToBitmap(drawable, targetSize = 144)
            iconCache.put(packageName, bitmap)
            bitmap
        } catch (e: Exception) {
            null
        }
    }

    private fun determineCategory(info: ResolveInfo, label: String, packageName: String): String {
        val lowerLabel = label.lowercase()
        val lowerPkg = packageName.lowercase()

        if (lowerPkg.contains("dialer") || lowerPkg.contains("phone") ||
            lowerPkg.contains("message") || lowerPkg.contains("sms") ||
            lowerPkg.contains("telegram") || lowerPkg.contains("whatsapp") ||
            lowerPkg.contains("contacts") || lowerLabel.contains("telefon") ||
            lowerLabel.contains("kontakt") || lowerLabel.contains("xabar")
        ) {
            return "Muloqot"
        }

        if (lowerPkg.contains("camera") || lowerPkg.contains("gallery") ||
            lowerPkg.contains("photo") || lowerPkg.contains("music") ||
            lowerPkg.contains("audio") || lowerPkg.contains("video") ||
            lowerPkg.contains("youtube") || lowerLabel.contains("kamera") ||
            lowerLabel.contains("galereya") || lowerLabel.contains("musiqa")
        ) {
            return "Media"
        }

        if (lowerPkg.contains("instagram") || lowerPkg.contains("facebook") ||
            lowerPkg.contains("tiktok") || lowerPkg.contains("twitter") ||
            lowerPkg.contains("social")
        ) {
            return "Ijtimoiy"
        }

        if (lowerPkg.contains("calc") || lowerPkg.contains("clock") ||
            lowerPkg.contains("settings") || lowerPkg.contains("file") ||
            lowerPkg.contains("tool") || lowerLabel.contains("sozlama") ||
            lowerLabel.contains("kalkulyator") || lowerLabel.contains("soat")
        ) {
            return "Asboblar"
        }

        // Check Android ApplicationInfo category if available
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            try {
                val appCategory = info.activityInfo.applicationInfo.category
                when (appCategory) {
                    ApplicationInfo.CATEGORY_GAME -> return "O'yinlar"
                    ApplicationInfo.CATEGORY_AUDIO,
                    ApplicationInfo.CATEGORY_VIDEO,
                    ApplicationInfo.CATEGORY_IMAGE -> return "Media"
                    ApplicationInfo.CATEGORY_SOCIAL -> return "Ijtimoiy"
                    ApplicationInfo.CATEGORY_PRODUCTIVITY,
                    ApplicationInfo.CATEGORY_MAPS -> return "Ish unumi"
                }
            } catch (_: Exception) {}
        }

        return "Boshqa"
    }
}
