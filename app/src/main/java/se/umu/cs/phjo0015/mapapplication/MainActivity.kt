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
    private val drawerMenuItems = mutableListOf<String>("Inställningar")

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // enableEdgeToEdge()

        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.my_toolbar))

        val navHostFragment: NavHostFragment = supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment
        navController = navHostFragment.navController

        val drawerLayout = findViewById<androidx.drawerlayout.widget.DrawerLayout>(R.id.drawer_layout)
        drawerLayout.closeDrawer(GravityCompat.START, false) // false = utan animation

        findViewById<ComposeView>(R.id.compose_drawer).setContent {
            DrawerContent(::onClickDrawerItem)
        }
    }

    fun toggleDrawer() {
        val drawerLayout = findViewById<androidx.drawerlayout.widget.DrawerLayout>(R.id.drawer_layout)

        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            drawerLayout.openDrawer(GravityCompat.START)
        }
    }

    fun onClickDrawerItem(drawerItem: DrawerItem) {
        val destinationId = getDestinationId(drawerItem)

        toggleDrawer()
        navigateTo(destinationId)
    }

    fun getDestinationId(drawerItem: DrawerItem): Int {
        if(drawerItem == DrawerItem.SETTINGS) {
            return R.id.settingsFragment
        }

        return R.id.mapFragment
    }

    fun navigateTo(fragmentId: Int) {
        navController.navigate(fragmentId)
    }
}