package com.riteshkumar.backgroundlocationupdate.data.remote

import com.riteshkumar.backgroundlocationupdate.data.remote.model.response.Result
import com.riteshkumar.backgroundlocationupdate.database.LocationEntity
import retrofit2.http.Body
import retrofit2.http.POST

interface LocationUploadApi {

    @POST("location")
    suspend fun uploadLocation(@Body data: LocationEntity): Result

    @POST("locations")
    suspend fun uploadLocations(@Body list: List<LocationEntity>): Result
}