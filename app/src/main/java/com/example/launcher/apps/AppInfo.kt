package com.example.launcher.apps

import android.graphics.Bitmap

data class AppInfo(
    val label: String,
    val packageName: String,
    val activityName: String,
    val icon: Bitmap? = null,
    val category: String = "Boshqa",
    val isFavorite: Boolean = false,
    val isHidden: Boolean = false,
    val launchCount: Int = 0
)
