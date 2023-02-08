package com.riteshkumar.backgroundlocationupdate.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.riteshkumar.backgroundlocationupdate.common.DATABASE_VERSION

@Database(entities = [LocationEntity::class], version = DATABASE_VERSION, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun locationDao(): LocationDao
}