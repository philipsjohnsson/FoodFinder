package se.umu.cs.phjo0015.mapapplication

import android.annotation.SuppressLint
import android.os.Bundle
import android.preference.PreferenceManager
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.navigation.fragment.NavHostFragment
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import org.osmdroid.config.Configuration
import org.osmdroid.views.overlay.Marker
import se.umu.cs.phjo0015.mapapplication.overlays.BottomSheetWithDrag
import android.hardware.Sensor
import android.hardware.SensorManager
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import androidx.appcompat.widget.Toolbar

/**
 * A simple [Fragment] subclass.
 * Use the [MapFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MapFragment : Fragment() {
    private var showDialog: MutableState<Boolean> = mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
                    OsmdroidMapView(::onMarkerClick)
                }
            }


            if (showDialog.value) {
                // Screen content

                // https://developer.android.com/develop/ui/compose/components/bottom-sheets
                // https://developer.android.com/reference/kotlin/androidx/compose/material3/SheetState
                BottomSheetWithDrag(::setBottomSheetVisible)

                // changeView()

            }
        }



        return view
    }

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