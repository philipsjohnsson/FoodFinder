package se.umu.cs.phjo0015.mapapplication.utils

import android.Manifest.permission.ACCESS_FINE_LOCATION
import androidx.activity.result.ActivityResultLauncher

class PermissionManager(
    private val requestPermissionLauncher: ActivityResultLauncher<String>
) {

    fun handlePermissionResult() {

    }

    fun requestLocationPermission() {
        requestPermissionLauncher.launch(ACCESS_FINE_LOCATION)
    }
}