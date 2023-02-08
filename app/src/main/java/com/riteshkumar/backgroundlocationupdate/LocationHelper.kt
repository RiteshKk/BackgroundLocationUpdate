package com.riteshkumar.backgroundlocationupdate

import android.annotation.SuppressLint
import android.content.Context
import android.location.Criteria
import android.location.LocationListener
import android.location.LocationManager
import android.location.LocationManager.GPS_PROVIDER
import android.location.LocationManager.NETWORK_PROVIDER
import android.widget.Toast

object LocationHelper {

    private lateinit var locationManager: LocationManager
    private fun initLocationManager(context: Context) {
        if (::locationManager.isInitialized.not()) {
            locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        }
    }

    fun isLocationEnabled(context: Context): Boolean {
        initLocationManager(context)
        return locationManager.isProviderEnabled(GPS_PROVIDER) ||
            locationManager.isProviderEnabled(NETWORK_PROVIDER)
    }

    @SuppressLint("MissingPermission")
    fun startLocationUpdate(context: Context, listener: LocationListener) {
        val criteria = Criteria()
        criteria.accuracy = Criteria.ACCURACY_FINE
        criteria.isAltitudeRequired = false
        criteria.isBearingRequired = false
        criteria.isCostAllowed = true
        criteria.powerRequirement = Criteria.POWER_LOW
        if(isLocationEnabled(context)) {
            val provider = locationManager.getBestProvider(criteria, true)
            if (provider != null) {
                locationManager.requestLocationUpdates(provider, 5000, 0f, listener)
            }
        }else{
            Toast.makeText(context, "GPS is off", Toast.LENGTH_SHORT).show()
        }
    }

    fun stopLocationUpdate(listener: LocationListener) {
        locationManager.removeUpdates(listener)
    }
}