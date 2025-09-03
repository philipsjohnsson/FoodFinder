package se.umu.cs.phjo0015.mapapplication

import android.Manifest
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.app.Activity
import android.app.AlertDialog
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresPermission
import androidx.appcompat.widget.Toolbar
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.ComposeView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import se.umu.cs.phjo0015.mapapplication.utils.PermissionManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.CancellationToken
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.OnTokenCanceledListener
import se.umu.cs.phjo0015.mapapplication.model.SettingsToggle
import se.umu.cs.phjo0015.mapapplication.model.UserLocation
import se.umu.cs.phjo0015.mapapplication.pages.SettingsPage

/**
 * SettingsFragment is used to show all of the settings in the application.
 */
class SettingsFragment : Fragment() {

    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>
    private var permissionState: MutableState<Boolean> = mutableStateOf(false)
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var prefs: SharedPreferences
    private val settingsToggles: List<SettingsToggle> = listOf(
        SettingsToggle(
            title = "Visa min position",
            isEnabled = permissionState,
            onToggle = {
                if (!permissionState.value) {
                    when {
                        // Case 1: Permission granted
                        userHasLocationPermission() == PackageManager.PERMISSION_GRANTED -> {
                            setPermissionState(true)
                        }

                        // Case 2: Ask for permission
                        shouldShowPermissionRationale(ACCESS_FINE_LOCATION) -> {
                            showPermissionRational {
                                requestPermissionLauncher.launch(ACCESS_FINE_LOCATION)
                            }
                        }

                        // Case 3:
                        else -> {
                            // Directly ask for the permission.
                            requestPermissionLauncher.launch(ACCESS_FINE_LOCATION)
                        }
                    }
                } else {
                    setPermissionState(false)
                }
            }
        )
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        prefs = requireContext().getSharedPreferences("Settings", MODE_PRIVATE)

        initSettings()
        setRequestPermissionLauncher()

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
    }

    fun initSettings() {
        permissionState.value = getPermissionState()
    }

    private fun userHasLocationPermission() = ContextCompat.checkSelfPermission((requireContext()), ACCESS_FINE_LOCATION)
    private fun shouldShowPermissionRationale(ACCESS_REQUEST: String) =
        ActivityCompat.shouldShowRequestPermissionRationale(requireContext() as Activity, ACCESS_REQUEST)

    fun showPermissionRational(positiveAction: () -> Unit) {
        AlertDialog.Builder(requireActivity())
            .setTitle("Platsåtkomst")
            .setMessage("Platsåtkomst behövs för att visa din position")
            .setPositiveButton(R.string.confirm) {_, _ -> positiveAction()}
            .setNegativeButton(R.string.decline) {dialog,_ -> dialog.dismiss() }
            .create().show()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = ComposeView(requireContext())
        view.setContent {
            SettingsPage(settingsToggles)
        }

        return view
    }

    private fun getPermissionState(): Boolean {
        return prefs.getBoolean(SHOW_USER_LOCATION, false)
    }

    private fun setPermissionState(enabled: Boolean) {
        prefs.edit().putBoolean(SHOW_USER_LOCATION, enabled).apply()
        permissionState.value = enabled
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupMenu()
    }

    private fun setRequestPermissionLauncher() {
        /**
         *
         * https://developer.android.com/training/permissions/requesting#principles
         * https://www.youtube.com/watch?v=7RDKN0WFEVc
         *
         * Register the permissions callback, which handles the user's response to the
         * system permissions dialog. Save the return value, an instance of
         * ActivityResultLauncher. You can use either a val, as shown in this snippet,
         * or a lateinit var in your onAttach() or onCreate() method.
         */
        requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
                isGranted ->
            if (isGranted) {
                // Permission is granted. Continue the action or workflow in your app.
                setPermissionState(true)
            } else {
                // Permission denied
                if (shouldShowPermissionRationale(ACCESS_FINE_LOCATION)) {
                    showPermissionRational {
                        requestPermissionLauncher.launch(ACCESS_FINE_LOCATION)
                    }
                } else {
                    // Explain to the user that the feature is unavailable because the
                    // feature requires a permission that the user has denied. At the
                    // same time, respect the user's decision. Don't link to system
                    // settings in an effort to convince the user to change their
                    // decision.
                    setPermissionState(false)
                    Toast.makeText(requireContext(), "Platsåtkomst nekad permanent, ändra i inställningar för att få tillgång.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun setupMenu() {
        (requireActivity() as MenuHost).addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {


                val toolbar = requireActivity().findViewById<Toolbar>(R.id.my_toolbar)

                toolbar.setNavigationIcon(R.drawable.back)
                toolbar.setNavigationOnClickListener {
                    findNavController().popBackStack()
                }
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {

                if (menuItem.itemId == R.id.hamburger_menu) {
                    (requireActivity() as MainActivity).toggleDrawer()

                    return true
                }

                return false
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    companion object {
        fun newInstance(): SettingsFragment {
            return SettingsFragment()
        }

        const val SHOW_USER_LOCATION = "show_user_location"
    }
}