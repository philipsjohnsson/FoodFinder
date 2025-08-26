package se.umu.cs.phjo0015.mapapplication.utils

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.app.AlertDialog
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import se.umu.cs.phjo0015.mapapplication.R

class PermissionManager(
    private val requestPermissionLauncher: ActivityResultLauncher<String>
) {

    fun handlePermissionResult() {

    }

    fun requestLocationPermission() {
        requestPermissionLauncher.launch(ACCESS_FINE_LOCATION)
    }
}