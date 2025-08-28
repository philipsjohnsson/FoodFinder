package se.umu.cs.phjo0015.mapapplication

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.app.AlertDialog
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.location.Location
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.Toolbar
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.ComposeView
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
import se.umu.cs.phjo0015.mapapplication.pages.SettingsPage

/**
 * A simple [Fragment] subclass.
 * Use the [SettingsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SettingsFragment : Fragment() {

    private lateinit var permissionManager: PermissionManager
    private var permissionState: MutableState<Boolean> = mutableStateOf(false)
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var prefs: SharedPreferences
    private val settingsToggles: List<SettingsToggle> = listOf(
        SettingsToggle(
            title = "Visa min position",
            isEnabled = permissionState,
            onToggle = {
                if (!permissionState.value) {
                    requestPermissionLauncher.launch(ACCESS_FINE_LOCATION)
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

        permissionManager = PermissionManager(requestPermissionLauncher)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
    }

    fun initSettings() {
        permissionState.value = prefs.getBoolean(SHOW_USER_LOCATION, false)
    }

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
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        try {
            if (isGranted) {
                //permissionState.value = "JAG HAR PERMISSION"

                // Permission is granted. Continue the action or workflow in your
                // app.

                fusedLocationClient.getCurrentLocation(LocationRequest.PRIORITY_HIGH_ACCURACY, object : CancellationToken() {
                    override fun onCanceledRequested(p0: OnTokenCanceledListener) = CancellationTokenSource().token

                    override fun isCancellationRequested() = false
                })
                    .addOnSuccessListener { location : Location? ->
                        println("JAG HAR PERMISSION")
                        setPermissionState(true)
                        // Got last known location. In some rare situations this can be null.
                    }
            } else {
                println("NO LOCATION PERMISSION")
                //permissionState.value = "INGEN PERMISSION"

                // Explain to the user that the feature is unavailable because the
                // feature requires a permission that the user has denied. At the
                // same time, respect the user's decision. Don't link to system
                // settings in an effort to convince the user to change their
                // decision.
                showPermissionRational() {
                    permissionManager.requestLocationPermission()
                }
            }
        } catch (e: SecurityException) {
            println("ERROR HANDLING")
            println(e)
        }

    }

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

    private fun getPermissionState(): Boolean =
        prefs.getBoolean(SHOW_USER_LOCATION, false)

    private fun setPermissionState(enabled: Boolean) {
        prefs.edit().putBoolean(SHOW_USER_LOCATION, enabled).apply()
        permissionState.value = enabled
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupMenu()
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