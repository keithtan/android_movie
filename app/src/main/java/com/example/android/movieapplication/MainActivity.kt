package com.example.android.movieapplication

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.onNavDestinationSelected
import androidx.navigation.ui.setupWithNavController
import com.example.android.movieapplication.databinding.ActivityMainBinding
import com.google.android.material.navigation.NavigationView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)

        drawerLayout = binding.drawerLayout

        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        initNavController()
        initNavigationItemListener()
        initAppBarConfiguration()

        binding.toolbar.setupWithNavController(navController, appBarConfiguration)
    }

    private fun initAppBarConfiguration() {
        val topLevelDestinations = setOf(R.id.moviesViewPagerFragment, R.id.tvViewPagerFragment)
        appBarConfiguration = AppBarConfiguration.Builder(topLevelDestinations)
            .setOpenableLayout(drawerLayout)
            .build()
    }

    private fun initNavController() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
    }

    private fun initNavigationItemListener() {
        with(findViewById<NavigationView>(R.id.nav_view)) {
            setNavigationItemSelectedListener {
                drawerLayout.close()
                if (checkedItem != it)
                    it.onNavDestinationSelected(navController)
                else true
            }
        }
    }

}