package com.example.mobilecomputinghw1

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "shake_events")
data class ShakeEvent(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val timestamp: Long = System.currentTimeMillis()
)
