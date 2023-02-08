package com.riteshkumar.backgroundlocationupdate

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log

class RebootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        intent?.let {
            if (it.action == Intent.ACTION_BOOT_COMPLETED) {
                Log.d("Location Service", "boot completed")
                context?.let { ctx ->
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                        ctx.startForegroundService(Intent(ctx, LocationUploadService::class.java))
                    else
                        ctx.startService(Intent(ctx, LocationUploadService::class.java))
                }
            }
        }
    }
}