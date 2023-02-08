package com.riteshkumar.backgroundlocationupdate.data.remote.model.requestmodel

data class LocationData(
    val latitude: Double,
    val longitude: Double,
    val batteryPercentage: Int,
    val batteryTemp: Float,
    val drivingMode: Int,
    val speed: Float
)
