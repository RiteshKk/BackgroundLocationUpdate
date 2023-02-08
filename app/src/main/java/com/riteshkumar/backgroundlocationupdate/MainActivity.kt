package com.riteshkumar.backgroundlocationupdate

import android.Manifest.permission
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.os.Bundle
import android.os.IBinder
import android.provider.Settings
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.riteshkumar.backgroundlocationupdate.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private var mService: LocationUploadService? = null
    private var isBounded = false

    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (
            permissions[permission.ACCESS_FINE_LOCATION] == true ||
            permissions[permission.ACCESS_COARSE_LOCATION] == true
        ) {
            if (VERSION.SDK_INT >= VERSION_CODES.P) {
                requestForegroundServicePermission()
            } else {
                startService()
            }
        }
    }

    @RequiresApi(VERSION_CODES.P)
    private fun requestForegroundServicePermission() {
        foregroundPermissionLauncher.launch(
            permission.FOREGROUND_SERVICE
        )
    }

    private val foregroundPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {
        if (it) {
            startService()
        }
    }

    override fun onStart() {
        super.onStart()
        Intent(this, LocationUploadService::class.java).also { intent ->
            bindService(intent, connection, Context.BIND_AUTO_CREATE)
        }
    }

    override fun onStop() {
        super.onStop()
        unbindService(connection)
        isBounded = false
    }

    private fun startService() {
        if (VERSION.SDK_INT >= VERSION_CODES.O)
            startForegroundService(Intent(this@MainActivity, LocationUploadService::class.java))
        else
            startService(Intent(this@MainActivity, LocationUploadService::class.java))
    }

    private val launcher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (LocationHelper.isLocationEnabled(this)) {
            requestLocationPermission()
        } else {
            Toast.makeText(this, "GPS permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    private fun requestLocationPermission() {
        permissionLauncher.launch(
            arrayOf(
                permission.ACCESS_FINE_LOCATION,
                permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.locationControlBtn.text = getString(R.string.start)
        binding.locationControlBtn.setOnClickListener {
            if (!LocationHelper.isLocationEnabled(this)) {
                showAlert()
            }else{
                requestLocationPermission()
            }
        }
    }

    private fun showAlert() {
        val dialog: AlertDialog.Builder = AlertDialog.Builder(this)
        dialog.setTitle("Enable Location")
            .setMessage(
                """Your Locations Settings is set to 'Off'.
            Please Enable Location to use this app""".trimIndent()
            )
            .setPositiveButton("Location Settings") { _, _ ->
                launcher.launch(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
            }
            .setNegativeButton("Cancel") { _, _ -> }
        dialog.show()
    }

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder) {
            val binder = service as LocationUploadService.LocalBinder
            mService = binder.getService()
            isBounded = true
            observeLocation()
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            isBounded = false
        }
    }

    private fun observeLocation() {
        mService?.locationLiveData?.observe(this) {
            binding.location.text = "Latitude : ${it.latitude}\nLongitude : ${it.longitude}"
        }
    }
}