package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface HomeScreenDao {
    @Query("SELECT * FROM home_screen_items ORDER BY pageIndex ASC, cellY ASC, cellX ASC")
    fun getAllItems(): Flow<List<HomeScreenItemEntity>>

    @Query("SELECT * FROM home_screen_items WHERE pageIndex = :pageIndex ORDER BY cellY ASC, cellX ASC")
    fun getItemsForPage(pageIndex: Int): Flow<List<HomeScreenItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: HomeScreenItemEntity): Long

    @Update
    suspend fun updateItem(item: HomeScreenItemEntity)

    @Delete
    suspend fun deleteItem(item: HomeScreenItemEntity)

    @Query("DELETE FROM home_screen_items WHERE id = :id")
    suspend fun deleteItemById(id: Long)

    @Query("DELETE FROM home_screen_items WHERE packageName = :packageName")
    suspend fun deleteByPackage(packageName: String)

    @Query("DELETE FROM home_screen_items WHERE appWidgetId = :appWidgetId")
    suspend fun deleteByAppWidgetId(appWidgetId: Int)

    @Query("SELECT MAX(pageIndex) FROM home_screen_items")
    suspend fun getMaxPageIndex(): Int?

    @Query("DELETE FROM home_screen_items WHERE pageIndex = :pageIndex")
    suspend fun deletePage(pageIndex: Int)
}
