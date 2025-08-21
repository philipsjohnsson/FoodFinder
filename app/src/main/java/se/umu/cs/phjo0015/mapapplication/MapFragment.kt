package se.umu.cs.phjo0015.mapapplication

import android.annotation.SuppressLint
import android.os.Bundle
import android.preference.PreferenceManager
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.ui.platform.ComposeView
import androidx.navigation.fragment.NavHostFragment
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Marker
import se.umu.cs.phjo0015.mapapplication.overlays.BtmSheetWithDrag
import se.umu.cs.phjo0015.mapapplication.overlays.InfoDialogExample

// TODO: Rename parameter arguments, choose names that match

/**
 * A simple [Fragment] subclass.
 * Use the [MapFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MapFragment : Fragment(), MapEventsReceiver {
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
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    OsmdroidMapView(::onMarkerClick)
                }
            }

            // Start for bottom sheet

            if (showDialog.value) {
                // Screen content

                BtmSheetWithDrag(::setBottomSheetVisible)

                // https://developer.android.com/develop/ui/compose/components/bottom-sheets
                // https://developer.android.com/reference/kotlin/androidx/compose/material3/SheetState


                // BtmSheet()

            }

            /**
             *
            if(showDialog.value) {
                InfoDialogExample(
                    ::onDismissRequest,
                    ::onConfirmation,
                    "Test",
                    "Text content",
                    Icons.Default.KeyboardArrowUp
                )
            }
            */
        }

        return view
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

    fun onConfirmation() {

    }

    override fun singleTapConfirmedHelper(p: GeoPoint?): Boolean {
        TODO("Not yet implemented")
    }

    override fun longPressHelper(p: GeoPoint?): Boolean {
        TODO("Not yet implemented")
    }

    companion object {
        fun newInstance(): MapFragment {
            val fragment = MapFragment()
            return fragment
        }

    }
}