package com.riteshkumar.backgroundlocationupdate.database

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LocationRepository @Inject constructor(private val locationDao: LocationDao) {

    fun getLocations(): Flow<List<LocationEntity>> = locationDao.getLocations()

    suspend fun insert(location: LocationEntity) = locationDao.insert(location)

    suspend fun deleteLocations(dataList: List<LocationEntity>) = locationDao.delete(dataList)
}