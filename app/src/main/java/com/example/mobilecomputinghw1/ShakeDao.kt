package com.example.mobilecomputinghw1

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ShakeDao {
    @Insert
    suspend fun insert(event: ShakeEvent)

    @Query("SELECT * FROM shake_events ORDER BY timestamp DESC")
    fun getAllEvents(): Flow<List<ShakeEvent>>
}
