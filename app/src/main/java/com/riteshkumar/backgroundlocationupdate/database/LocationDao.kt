package com.riteshkumar.backgroundlocationupdate.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface LocationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(location: LocationEntity)

    @Update
    suspend fun update(location: LocationEntity)

    @Delete
    suspend fun delete(locations: List<LocationEntity>)

    @Query("SELECT * FROM location_table ORDER BY id DESC LIMIT 500")
    fun getLocations(): Flow<List<LocationEntity>>
}