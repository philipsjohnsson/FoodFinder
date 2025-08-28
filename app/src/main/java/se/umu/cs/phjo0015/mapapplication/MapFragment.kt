package se.umu.cs.phjo0015.mapapplication

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.preference.PreferenceManager
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.navigation.fragment.NavHostFragment
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import org.osmdroid.config.Configuration
import org.osmdroid.views.overlay.Marker
import se.umu.cs.phjo0015.mapapplication.overlays.BottomSheetWithDrag
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.CancellationToken
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.OnTokenCanceledListener
import se.umu.cs.phjo0015.mapapplication.model.UserLocation
import se.umu.cs.phjo0015.mapapplication.utils.PermissionManager

/**
 * A simple [Fragment] subclass.
 * Use the [MapFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MapFragment : Fragment() {
    private lateinit var permissionManager: PermissionManager
    private var showDialog: MutableState<Boolean> = mutableStateOf(false)
    // private var pref: SharedPreferences = getSharedPreferences("mypref", MODE_PRIVATE)
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var userLocationState: MutableState<UserLocation?> = mutableStateOf(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        permissionManager = PermissionManager(requestPermissionLauncher)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())

        val showUserLocation: Boolean = true
        println(showUserLocation)
        if(showUserLocation) {
            setUserLocation()
        }
    }

    @SuppressLint("UnrememberedMutableState")
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Important: init OSMDroid
        Configuration.getInstance().load(requireContext(), PreferenceManager.getDefaultSharedPreferences(requireContext()))
        Configuration.getInstance().userAgentValue = "MapApp"

        // Inflate the layout for this fragment
        val view = ComposeView(requireContext())
        
        view.setContent {

            Box {
                // A surface container using the 'background' color from the theme
                Surface(
                    color = MaterialTheme.colorScheme.background
                ) {
                    OsmdroidMapView(::onMarkerClick, userLocationState)
                }
            }


            if (showDialog.value) {
                // Screen content

                val showUserPosition: Boolean = true

                // https://developer.android.com/develop/ui/compose/components/bottom-sheets
                // https://developer.android.com/reference/kotlin/androidx/compose/material3/SheetState
                BottomSheetWithDrag(::setBottomSheetVisible)
            }
        }



        return view
    }

    override fun onResume() {
        super.onResume()

        val showUserLocation: Boolean = true // Change to value you get from sharing the data..
        if(showUserLocation) {
            setUserLocation()
        }
    }

    private fun setUserLocation() {
        when {
            // Case 1: Permission granted
            userHasLocationPermission() == PackageManager.PERMISSION_GRANTED -> {
                try {
                    // You can use the API that requires the permission.
                    //performAction(...)
                    fusedLocationClient.getCurrentLocation(LocationRequest.PRIORITY_HIGH_ACCURACY, object : CancellationToken() {
                        override fun onCanceledRequested(p0: OnTokenCanceledListener) = CancellationTokenSource().token

                        override fun isCancellationRequested() = false
                    })
                        .addOnSuccessListener { location: Location? ->
                            location?.let {
                                userLocationState.value = UserLocation(it.latitude, it.longitude)
                            }

                            // Got last known location. In some rare situations this can be null.
                        }

                }  catch(e: SecurityException) {
                    println("ERROR")
                    println(e)
                }
            }

            // Case 2:
            shouldShowPermissionRationale(ACCESS_FINE_LOCATION) -> {
                showPermissionRational {
                    requestPermissionLauncher.launch(ACCESS_FINE_LOCATION)
                }
            }

            else -> {
                // You can directly ask for the permission.
                requestPermissionLauncher.launch(ACCESS_FINE_LOCATION)
            }
        }
    }

    private fun userHasLocationPermission() = ContextCompat.checkSelfPermission((requireContext()), ACCESS_FINE_LOCATION)
    private fun shouldShowPermissionRationale(ACCESS_REQUEST: String) =
        ActivityCompat.shouldShowRequestPermissionRationale(requireContext() as Activity, ACCESS_REQUEST)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupMenu()
    }

    private fun setupMenu() {
        (requireActivity() as MenuHost).addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                val toolbar = requireActivity().findViewById<Toolbar>(R.id.my_toolbar)

                toolbar.setNavigationIcon(R.drawable.hamburgermenu)
                toolbar.setNavigationOnClickListener {
                    (requireActivity() as MainActivity).toggleDrawer()
                }
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return true
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    /**
     *
     * https://developer.android.com/training/permissions/requesting#principles
     *
     * Register the permissions callback, which handles the user's response to the
     * system permissions dialog. Save the return value, an instance of
     * ActivityResultLauncher. You can use either a val, as shown in this snippet,
     * or a lateinit var in your onAttach() or onCreate() method.
     */
    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        try {
            if (isGranted) {
                // Permission is granted. Continue the action or workflow in your app.

                fusedLocationClient.lastLocation
                    .addOnSuccessListener { location : Location? ->
                        println("LOCATION UNDER HERE:")
                        println(location?.latitude)
                        println(location?.longitude)
                        // Got last known location. In some rare situations this can be null.
                    }
            } else {
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
            .setPositiveButton(R.string.confirm) {_, _ -> positiveAction() }
            .setNegativeButton(R.string.decline) {dialog,_ -> dialog.dismiss() }
            .create().show()
    }

    fun changeView() {
        NavHostFragment.findNavController(this@MapFragment)
            .navigate(R.id.action_mapFragment_to_testFragment)
    }

    fun setBottomSheetVisible(bol: Boolean) {
        showDialog.value = bol
    }

    fun onMarkerClick(marker: Marker): Boolean  {
        setBottomSheetVisible(true)

        return true
    }

    fun onDismissRequest() {
        showDialog.value = false
    }

    companion object {
        fun newInstance(): MapFragment {
            return MapFragment()
        }
    }
}