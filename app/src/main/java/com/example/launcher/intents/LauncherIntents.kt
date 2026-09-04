package com.example.launcher.intents

import android.app.Activity
import android.app.role.RoleManager
import android.content.ActivityNotFoundException
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.widget.Toast

object LauncherIntents {

    fun launchApp(context: Context, packageName: String, activityName: String? = null): Boolean {
        try {
            val intent = if (!activityName.isNullOrEmpty()) {
                Intent(Intent.ACTION_MAIN).apply {
                    addCategory(Intent.CATEGORY_LAUNCHER)
                    component = ComponentName(packageName, activityName)
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED
                }
            } else {
                context.packageManager.getLaunchIntentForPackage(packageName)?.apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED
                }
            }

            if (intent != null) {
                context.startActivity(intent)
                return true
            } else {
                Toast.makeText(context, "Ilovani ochib bo'lmadi", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Xatolik: ${e.localizedMessage ?: "Ilova topilmadi"}", Toast.LENGTH_SHORT).show()
        }
        return false
    }

    fun openAppInfo(context: Context, packageName: String) {
        try {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.fromParts("package", packageName, null)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "Sozlamalarni ochib bo'lmadi", Toast.LENGTH_SHORT).show()
        }
    }

    fun uninstallApp(context: Context, packageName: String) {
        try {
            val intent = Intent(Intent.ACTION_DELETE).apply {
                data = Uri.parse("package:$packageName")
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "Ilovani o'chirish oynasi ochilmadi", Toast.LENGTH_SHORT).show()
        }
    }

    fun requestDefaultLauncher(activity: Activity, requestCode: Int = 1001) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val roleManager = activity.getSystemService(RoleManager::class.java)
                if (roleManager != null && roleManager.isRoleAvailable(RoleManager.ROLE_HOME)) {
                    val intent = roleManager.createRequestRoleIntent(RoleManager.ROLE_HOME)
                    activity.startActivityForResult(intent, requestCode)
                    return
                }
            }

            // Fallback for Android 8 - 9 or if RoleManager is not available
            val intent = Intent(Settings.ACTION_HOME_SETTINGS).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            activity.startActivity(intent)
        } catch (e: Exception) {
            try {
                // Secondary fallback to generic application settings
                val fallbackIntent = Intent(Settings.ACTION_MANAGE_DEFAULT_APPS_SETTINGS).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                activity.startActivity(fallbackIntent)
            } catch (e2: Exception) {
                Toast.makeText(activity, "Bosh ekran sozlamalarini ochib bo'lmadi", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun openSystemRecents(context: Context) {
        // Safe attempt to trigger system recent apps or overview if accessible
        try {
            val intent = Intent("com.android.systemui.recent.action.TOGGLE_RECENTS").apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            // Android limits direct recent tasks intent for security, notify user gently
            Toast.makeText(context, "Tizim tugmasi yoki surish orqali so'nggi ilovalarga o'ting", Toast.LENGTH_SHORT).show()
        }
    }
}
