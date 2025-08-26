package se.umu.cs.phjo0015.mapapplication

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.appcompat.widget.Toolbar
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import se.umu.cs.phjo0015.mapapplication.utils.PermissionManager

/**
 * A simple [Fragment] subclass.
 * Use the [SettingsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SettingsFragment : Fragment() {

    // private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>
    private lateinit var permissionManager: PermissionManager
    // Gör det här till Compose state
    private var permissionState = mutableStateOf("NO")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        permissionManager = PermissionManager(requestPermissionLauncher)
    }

    // Register the permissions callback, which handles the user's response to the
    // system permissions dialog. Save the return value, an instance of
    // ActivityResultLauncher. You can use either a val, as shown in this snippet,
    // or a lateinit var in your onAttach() or onCreate() method.
    private val requestPermissionLauncher = registerForActivityResult(RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            println("LOCATION PERMISSION IS OK")
            permissionState.value = "JAG HAR PERMISSION"

            // Permission is granted. Continue the action or workflow in your
            // app.
        } else {
            println("NO LOCATION PERMISSION")
            permissionState.value = "INGEN PERMISSION"

            // Explain to the user that the feature is unavailable because the
            // feature requires a permission that the user has denied. At the
            // same time, respect the user's decision. Don't link to system
            // settings in an effort to convince the user to change their
            // decision.
            showPermissionRational() {
                permissionManager.requestLocationPermission()
            }
        }
    }

    fun showPermissionRational(positiveAction: () -> Unit) {
        AlertDialog.Builder(requireActivity())
            .setTitle("Location permission")
            .setMessage("Platsåtkomst behövs för appen att visa din position")
            .setPositiveButton(R.string.hamburgermenu) {_, _ -> positiveAction()}
            .setNegativeButton(R.string.hamburgermenu) {dialog,_ -> dialog.dismiss() }
            .create().show()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = ComposeView(requireContext())
        view.setContent {
            Column {
                Button(
                    onClick = {
                        requestPermissionLauncher.launch(ACCESS_FINE_LOCATION)
                    },
                    modifier = Modifier.wrapContentSize()
                ) {
                    Text("I want permission")
                }
                Text(permissionState.value)
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

    }
}