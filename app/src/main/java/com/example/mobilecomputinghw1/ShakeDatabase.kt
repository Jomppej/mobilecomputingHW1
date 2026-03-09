package com.example.mobilecomputinghw1

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [ShakeEvent::class], version = 1)
abstract class ShakeDatabase : RoomDatabase() {
    abstract fun shakeDao(): ShakeDao

    companion object {
        @Volatile private var INSTANCE: ShakeDatabase? = null

        fun getDatabase(context: Context): ShakeDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    ShakeDatabase::class.java,
                    "shake_database"
                ).build().also { INSTANCE = it }
            }
        }
    }
}
