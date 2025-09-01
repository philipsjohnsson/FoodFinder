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
import org.osmdroid.views.overlay.Marker
import se.umu.cs.phjo0015.mapapplication.overlays.BottomSheetWithDrag
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import androidx.appcompat.widget.Toolbar
import androidx.compose.runtime.Composable
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.CancellationToken
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.OnTokenCanceledListener
import se.umu.cs.phjo0015.mapapplication.SettingsFragment.Companion.SHOW_USER_LOCATION
import se.umu.cs.phjo0015.mapapplication.database.Destination
import se.umu.cs.phjo0015.mapapplication.database.DestinationViewModel
import se.umu.cs.phjo0015.mapapplication.model.UserLocation



/**
 * A simple [Fragment] subclass.
 * Use the [MapFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MapFragment : Fragment() {
    private var showDialog: MutableState<Boolean> = mutableStateOf(false)
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var userLocationState: MutableState<UserLocation?> = mutableStateOf(null)
    private lateinit var prefs: SharedPreferences
    private var permissionState: MutableState<Boolean> = mutableStateOf(false)
    private lateinit var destinations: LiveData<List<Destination>>

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
        destinations = viewModel.destinations



        viewModel.destinations.observe(viewLifecycleOwner) { destinationList ->
            println(destinationList)

            for(destination in destinationList) {
                println(destination.topic)
                println(destination.long)
                println(destination.lat)
                println(destination.description)
            }
        }

        // Inflate the layout for this fragment
        val view = ComposeView(requireContext())
        view.setContent {

            val destinations = viewModel.destinations.observeAsState(emptyList())

            Box {
                // A surface container using the 'background' color from the theme
                Surface(
                    color = MaterialTheme.colorScheme.background
                ) {
                    OsmdroidMapView(::onMarkerClick, destinations, userLocationState)
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

        val wantsLocation = getPermissionState()
        val hasPermission = userHasLocationPermission() == PackageManager.PERMISSION_GRANTED

        permissionState.value = wantsLocation && hasPermission

        Toast.makeText(context, "has permission: $hasPermission", Toast.LENGTH_SHORT).show()

        if(permissionState.value) {
            // Toast.makeText(context, "Permission to show userLocation", Toast.LENGTH_SHORT).show()
            setUserLocation()
        } else {
            // Toast.makeText(context, "No permission to show userLocation", Toast.LENGTH_SHORT).show()
            userLocationState.value = null
        }
    }

    private fun getPermissionState(): Boolean {
        return prefs.getBoolean(SHOW_USER_LOCATION, false)
    }

    private fun setPermissionState(enabled: Boolean) {
        prefs.edit().putBoolean(SHOW_USER_LOCATION, enabled).apply()
        permissionState.value = enabled
    }



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
                println("ERROR")
                println(e)
                userLocationState.value = null
            }
        }
    }

    private fun userHasLocationPermission() = ContextCompat.checkSelfPermission((requireContext()), ACCESS_FINE_LOCATION)

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