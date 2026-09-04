package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "home_screen_items")
data class HomeScreenItemEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val pageIndex: Int = 0,
    val cellX: Int = 0,
    val cellY: Int = 0,
    val spanX: Int = 1,
    val spanY: Int = 1,
    val itemType: String, // "app" or "widget"
    val packageName: String? = null,
    val activityName: String? = null,
    val appWidgetId: Int? = null
)
