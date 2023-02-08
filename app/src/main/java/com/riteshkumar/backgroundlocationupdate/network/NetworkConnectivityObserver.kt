package com.riteshkumar.backgroundlocationupdate.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import com.riteshkumar.backgroundlocationupdate.network.ConnectivityObserver.Status
import com.riteshkumar.backgroundlocationupdate.network.ConnectivityObserver.Status.Available
import com.riteshkumar.backgroundlocationupdate.network.ConnectivityObserver.Status.Losing
import com.riteshkumar.backgroundlocationupdate.network.ConnectivityObserver.Status.Lost
import com.riteshkumar.backgroundlocationupdate.network.ConnectivityObserver.Status.Unavailable
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

class NetworkConnectivityObserver(
    context: Context
): ConnectivityObserver {

    private val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    override fun observe(): Flow<Status> {
        return callbackFlow {
            val callback  = object : ConnectivityManager.NetworkCallback() {

                override fun onAvailable(network: Network) {
                    super.onAvailable(network)
                    launch { send(Available) }
                }

                override fun onUnavailable() {
                    super.onUnavailable()
                    launch { send(Unavailable) }
                }

                override fun onLosing(network: Network, maxMsToLive: Int) {
                    super.onLosing(network, maxMsToLive)
                    launch { send(Losing) }
                }

                override fun onLost(network: Network) {
                    super.onLost(network)
                    launch { send(Lost) }
                }
            }
            connectivityManager.registerDefaultNetworkCallback(callback)
            awaitClose {
                connectivityManager.unregisterNetworkCallback(callback)
            }
        }.distinctUntilChanged()
    }
}