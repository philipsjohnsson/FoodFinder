package se.umu.cs.phjo0015.mapapplication

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.platform.ComposeView
import androidx.core.view.GravityCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import se.umu.cs.phjo0015.mapapplication.components.DrawerContent
import se.umu.cs.phjo0015.mapapplication.components.DrawerItem

class MainActivity : AppCompatActivity() {
    private lateinit var navController: NavController

    /**
     * Initializes the main activity. Sets up the layout, toolbar,
     * navigation controller, and drawer content with Jetpack Compose.
     */
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // enableEdgeToEdge()

        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.my_toolbar))

        val navHostFragment: NavHostFragment = supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment
        navController = navHostFragment.navController

        val drawerLayout = findViewById<androidx.drawerlayout.widget.DrawerLayout>(R.id.drawer_layout)
        drawerLayout.closeDrawer(GravityCompat.START, false) // false = no animation

        findViewById<ComposeView>(R.id.compose_drawer).setContent {
            DrawerContent(::onClickDrawerItem)
        }
    }

    /**
     * Toggles the drawer menu.
     */
    fun toggleDrawer() {
        val drawerLayout = findViewById<androidx.drawerlayout.widget.DrawerLayout>(R.id.drawer_layout)

        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            drawerLayout.openDrawer(GravityCompat.START)
        }
    }

    /**
     * Handles click on drawer item, navigates to the destination.
     */
    fun onClickDrawerItem(drawerItem: DrawerItem) {
        val destinationId = getDestinationId(drawerItem)

        toggleDrawer()
        navigateTo(destinationId)
    }

    /**
     * Gets the destination id
     */
    fun getDestinationId(drawerItem: DrawerItem): Int {
        if(drawerItem == DrawerItem.SETTINGS) {
            return R.id.settingsFragment
        } else if(drawerItem == DrawerItem.ABOUT) {
            return R.id.aboutFragment
        }

        // Default, come to the map view
        return R.id.mapFragment
    }

    /**
     * Navigates to the fragment that is provided.
     */
    fun navigateTo(fragmentId: Int) {
        navController.navigate(fragmentId)
    }
}