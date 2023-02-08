package com.riteshkumar.backgroundlocationupdate.data

import com.riteshkumar.backgroundlocationupdate.data.remote.LocationUploadApi
import com.riteshkumar.backgroundlocationupdate.database.LocationEntity
import com.riteshkumar.backgroundlocationupdate.domain.repository.LocationUploadRepository
import javax.inject.Inject

class LocationUploadRepositoryImpl @Inject constructor(
    private val api: LocationUploadApi
) : LocationUploadRepository {

    override suspend fun uploadLocation(data: LocationEntity) = api.uploadLocation(data)
    override suspend fun uploadLocations(list: List<LocationEntity>) = api.uploadLocations(list)
}