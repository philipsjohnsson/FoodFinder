package se.umu.cs.phjo0015.mapapplication

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.annotation.SuppressLint
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.preference.PreferenceManager
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.livedata.observeAsState
import org.osmdroid.config.Configuration
import se.umu.cs.phjo0015.mapapplication.overlays.BottomSheetWithDrag
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.CancellationToken
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.OnTokenCanceledListener
import kotlinx.coroutines.launch
import org.osmdroid.util.GeoPoint
import se.umu.cs.phjo0015.mapapplication.SettingsFragment.Companion.SHOW_USER_LOCATION
import se.umu.cs.phjo0015.mapapplication.database.Destination
import se.umu.cs.phjo0015.mapapplication.database.DestinationViewModel
import se.umu.cs.phjo0015.mapapplication.model.MapState
import se.umu.cs.phjo0015.mapapplication.model.UserLocation
import se.umu.cs.phjo0015.mapapplication.pages.osmdroidMapView

/**
 * MapFragment is used to create a map view on the app.
 */
class MapFragment : Fragment() {
    private var showDialog: MutableState<Boolean> = mutableStateOf(false)
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var userLocationState: MutableState<UserLocation?> = mutableStateOf(null)
    private lateinit var prefs: SharedPreferences
    private var permissionState: MutableState<Boolean> = mutableStateOf(false)
    private var pickedDestinationState: MutableState<Destination?> = mutableStateOf(null)
    private var mapState = MapState(GeoPoint(63.189460, 14.607896), 6.0, false)


    /**
     * Initializes preferences, checks location permissions, and sets up
     * the location client when the fragment is created.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        prefs = requireContext().getSharedPreferences("Settings", MODE_PRIVATE)
        if(userHasLocationPermission() != PackageManager.PERMISSION_GRANTED) {
            setPermissionState(false)
        }

        permissionState.value = getPermissionState()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())

        if(permissionState.value) {
            setUserLocation()
        }
    }

    /**
     * Creates and returns the fragment's view. Initializes OSMDroid,
     * sets up the ViewModel, and builds the UI with Jetpack Compose,
     * including the map and an optional bottom sheet.
     */
    @SuppressLint("UnrememberedMutableState")
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Important: init OSMDroid
        Configuration.getInstance().load(requireContext(), PreferenceManager.getDefaultSharedPreferences(requireContext()))
        Configuration.getInstance().userAgentValue = "MapApp"

        val viewModel = ViewModelProvider(requireActivity())[DestinationViewModel::class.java]

        // Inflate the layout for this fragment
        val view = ComposeView(requireContext())
        view.setContent {

            val destinations = viewModel.destinations.observeAsState(emptyList())

            Box {
                // A surface container using the 'background' color from the theme
                Surface(
                    color = MaterialTheme.colorScheme.background
                ) {
                    osmdroidMapView(::onMarkerClick, destinations, userLocationState, mapState)
                }
            }

            if (showDialog.value) {

                // Inspired by:
                // https://developer.android.com/develop/ui/compose/components/bottom-sheets
                // https://developer.android.com/reference/kotlin/androidx/compose/material3/SheetState
                BottomSheetWithDrag(::setBottomSheetVisible, pickedDestinationState.value)
            }
        }

        return view
    }

    /**
     * Called after the fragment's view is created. Sets up the UI and restores
     * any previously saved state if available.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupMenu()

        // Restore data if available
        if (savedInstanceState != null) {
            setSavedData(savedInstanceState)
        }
    }

    /**
     * Called when the fragment becomes active again. Updates the location
     * permission state and sets or clears the user's location accordingly.
     */
    override fun onResume() {
        super.onResume()

        val wantsLocation = getPermissionState()
        val hasPermission = userHasLocationPermission() == PackageManager.PERMISSION_GRANTED

        permissionState.value = wantsLocation && hasPermission

        if(permissionState.value) {
            setUserLocation()
        } else {
            userLocationState.value = null
        }
    }

    /**
     * Gets the permission state
     */
    private fun getPermissionState(): Boolean {
        return prefs.getBoolean(SHOW_USER_LOCATION, false)
    }

    /**
     * Sets the permission state
     */
    private fun setPermissionState(enabled: Boolean) {
        prefs.edit().putBoolean(SHOW_USER_LOCATION, enabled).apply()
        permissionState.value = enabled
    }

    /**
     * Sets the user location if the user has allowed permission for location.
     */
    private fun setUserLocation() {
        if( userHasLocationPermission() == PackageManager.PERMISSION_GRANTED) {
            try {
                // You can use the API that requires the permission.
                fusedLocationClient.getCurrentLocation(LocationRequest.PRIORITY_HIGH_ACCURACY, object : CancellationToken() {
                    override fun onCanceledRequested(p0: OnTokenCanceledListener) = CancellationTokenSource().token

                    override fun isCancellationRequested() = false
                })
                    .addOnSuccessListener { location: Location? ->
                        location?.let {
                            userLocationState.value = UserLocation(it.longitude, it.latitude)
                        }
                    }

            }  catch(e: SecurityException) {
                userLocationState.value = null
            }
        }
    }

    /**
     * Checks if the user has Location permission
     */
    private fun userHasLocationPermission() = ContextCompat.checkSelfPermission((requireContext()), ACCESS_FINE_LOCATION)

    /**
     * Sets the navbar for this specific fragment
     */
    private fun setupMenu() {
        (requireActivity() as MenuHost).addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                val toolbar = requireActivity().findViewById<Toolbar>(R.id.my_toolbar)
                toolbar.title = "Upptäcktskartan"

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
     * Sets the visible of bottomSheet
     */
    fun setBottomSheetVisible(bol: Boolean) {
        showDialog.value = bol

        if(!bol) {
            setPickedDestinationState(null)
        }
    }

    /**
     * When a user clicks a marker, we will handle this click here.
     * Sets show bottomSheet
     * Sets the picked destination to show
     */
    fun onMarkerClick(destination: Destination): Boolean  {

        setPickedDestinationState(destination)
        setBottomSheetVisible(true)

        return true
    }

    /**
     *  sets the picked destination
     */
    fun setPickedDestinationState (destination: Destination?) {
        pickedDestinationState.value = destination
    }

    /**
     * Sets the saved data to the restored fragment
     */
    fun setSavedData(savedInstanceState: Bundle) {

        // Sets the saved map state
        val savedMapState = savedInstanceState.getParcelable<MapState>(KEY_MAP_STATE)
        if(savedMapState != null) {
            mapState = savedMapState
        }

        // Sets the saved showDialog
        val savedShowDialog = savedInstanceState.getBoolean(KEY_SHOW_DIALOG)
        showDialog.value = savedShowDialog

        // Sets the saved picked destination
        val savedPickedDestinationId = savedInstanceState.getInt(KEY_PICKED_DESTINATION_ID, -1)
        println(savedPickedDestinationId)
        if(savedPickedDestinationId != -1) {

            // We need to fetch data from the database using a synchronous (suspend) function.
            // Since suspend functions must be called from a coroutine, we use lifecycleScope.launch
            // to run this code asynchronously tied to the Fragment's lifecycle.
            lifecycleScope.launch {
                val viewModel = ViewModelProvider(requireActivity())[DestinationViewModel::class.java]

                val destination = viewModel.getDestinationSync(savedPickedDestinationId)
                if (destination != null) {
                    setPickedDestinationState(destination)
                }
            }
        }
    }

    /**
     * Saves data, so the data can be restored if the fragment is recreated.
     */
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        // Save the maps state
        outState.putParcelable(KEY_MAP_STATE, mapState)

        // Save the information about the picked destination, if it exists
        pickedDestinationState.value?.let {
            outState.putInt(KEY_PICKED_DESTINATION_ID, it.id)
            outState.putBoolean(KEY_SHOW_DIALOG, showDialog.value)
        }
    }

    companion object {
        fun newInstance(): MapFragment {
            return MapFragment()
        }

        private const val KEY_MAP_STATE = "mapState"
        private const val KEY_PICKED_DESTINATION_ID = "destinationId"
        private const val KEY_SHOW_DIALOG = "showDialog"
    }
}