package com.riteshkumar.backgroundlocationupdate

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.location.Location
import android.location.LocationListener
import android.os.Binder
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.os.IBinder
import android.os.PowerManager
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.riteshkumar.backgroundlocationupdate.database.LocationEntity
import com.riteshkumar.backgroundlocationupdate.database.LocationRepository
import com.riteshkumar.backgroundlocationupdate.domain.repository.LocationUploadRepository
import com.riteshkumar.backgroundlocationupdate.network.ConnectivityObserver.Status
import com.riteshkumar.backgroundlocationupdate.network.NetworkConnectivityObserver
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class LocationUploadService : Service(), LocationListener {

    private var isFirstLaunch: Boolean = true
    private val scope = CoroutineScope(Dispatchers.IO)
    private val notificationManager: NotificationManager by lazy {
        applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    private var wakeLock: PowerManager.WakeLock? = null

    @Inject
    lateinit var repository: LocationUploadRepository

    @Inject
    lateinit var dbRepository: LocationRepository

    private val connectivityObserver by lazy {
        NetworkConnectivityObserver(applicationContext)
    }

    private val binder = LocalBinder()
    private val _locationLiveData = MutableLiveData<Location>()
    val locationLiveData: LiveData<Location>
        get() = _locationLiveData

    private val notificationBuilder by lazy {
        NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setAutoCancel(false)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Location Service is running...")
            .setOngoing(true)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        if (isFirstLaunch) {
            if (VERSION.SDK_INT >= VERSION_CODES.O) {
                createNotificationChannel()
            }
            if (VERSION.SDK_INT >= VERSION_CODES.Q) {
                startForeground(NOTIFICATION_ID, notificationBuilder.build(), ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION)
            } else {
                startForeground(NOTIFICATION_ID, notificationBuilder.build())
            }

            // we need this lock so our service gets not affected by Doze Mode
            wakeLock =
                (getSystemService(Context.POWER_SERVICE) as PowerManager).run {
                    newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "EndlessService::lock").apply {
                        acquire(1000*60*1000L /*1000 minutes*/)
                    }
                }
            isFirstLaunch = false
        }

        LocationHelper.startLocationUpdate(this, this)
        startService(Intent(applicationContext, DBDataUploadService::class.java))

        return START_STICKY
    }

    override fun onBind(intent: Intent): IBinder = binder

    @RequiresApi(VERSION_CODES.O)
    private fun createNotificationChannel() {
        if (notificationManager.getNotificationChannel(CHANNEL_ID) == null) {
            notificationManager.createNotificationChannel(
                NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_LOW
                )
            )
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("[LocationUploadService]", "sms service destroyed")
        scope.cancel()
        LocationHelper.stopLocationUpdate(this)
    }

    companion object {
        const val CHANNEL_ID = "channelId"
        const val NOTIFICATION_ID = 1001
        const val CHANNEL_NAME = "foregroundWorker"
    }

    override fun onLocationChanged(location: Location) {
        _locationLiveData.value = location
        Log.d("[LocationUploadService] location received", "${location.latitude}, ${location.longitude}")
        scope.launch(Dispatchers.IO) {
            connectivityObserver.observe().collectLatest {
                Log.d("[LocationUploadService] internet status", "$it")
                val data = LocationEntity(
                    latitude = location.latitude,
                    longitude = location.longitude
                )
                if (it == Status.Available) {
                    kotlin.runCatching {
                        repository.uploadLocation(data)
                    }.onFailure {
                        Log.d("[LocationUploadService] onFailure", "Failed: store in database")
                        dbRepository.insert(data)
                    }
                } else {
                    Log.d("[LocationUploadService] No Internet", "store in database")
                    dbRepository.insert(data)
                }
            }
        }
    }

    inner class LocalBinder : Binder() {
        fun getService(): LocationUploadService = this@LocationUploadService
    }
}