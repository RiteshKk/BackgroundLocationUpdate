package com.riteshkumar.backgroundlocationupdate.domain.repository

import com.riteshkumar.backgroundlocationupdate.database.LocationEntity
import com.riteshkumar.backgroundlocationupdate.data.remote.model.response.Result

interface LocationUploadRepository {
    suspend fun uploadLocation(data: LocationEntity): Result
    suspend fun uploadLocations(list: List<LocationEntity>): Result
}