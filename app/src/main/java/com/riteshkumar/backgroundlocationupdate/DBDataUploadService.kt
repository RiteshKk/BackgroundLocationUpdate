package com.riteshkumar.backgroundlocationupdate

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.riteshkumar.backgroundlocationupdate.database.LocationRepository
import com.riteshkumar.backgroundlocationupdate.domain.repository.LocationUploadRepository
import com.riteshkumar.backgroundlocationupdate.network.ConnectivityObserver.Status.Available
import com.riteshkumar.backgroundlocationupdate.network.NetworkConnectivityObserver
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class DBDataUploadService : Service() {
    @Inject
    lateinit var dbRepository: LocationRepository

    @Inject
    lateinit var repository: LocationUploadRepository

    private val connectivityObserver by lazy {
        NetworkConnectivityObserver(applicationContext)
    }

    private val scope = CoroutineScope(Dispatchers.IO)

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        Log.d("[DBDataUploadService]","started")
        scope.launch(Dispatchers.IO) {
            connectivityObserver.observe().collectLatest {
                if(it == Available){
                    dbRepository.getLocations().collectLatest { dataList ->
                        Log.d("[DBDataUploadService] location list","$dataList")
                        kotlin.runCatching {
                            val result = repository.uploadLocations(dataList)
                            if (result.statusCode == 200) {
                                Log.d("[DBDataUploadService]", "deleting data from database")
                                dbRepository.deleteLocations(dataList)
                            }
                        }.onFailure {
                            Log.e("[DBDataUploadService] onFailure","${it.message}")
                        }
                    }
                }
            }
        }

        return START_STICKY
    }
}