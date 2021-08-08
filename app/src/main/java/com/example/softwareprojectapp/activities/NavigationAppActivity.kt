package com.example.softwareprojectapp.activities

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.softwareprojectapp.R
import com.example.softwareprojectapp.databinding.ActivityNavigationAppBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NavigationAppActivity : AppCompatActivity(),
    NavController.OnDestinationChangedListener {

    private lateinit var navigationAppActivityBinding: ActivityNavigationAppBinding
    private lateinit var appBarConfiguration: AppBarConfiguration

    private val listMainFragments = mutableListOf<Int>()

    private val navController by lazy { findNavController(R.id.fragmentNavigationMainFlow) }

    private lateinit var _emailId: String
    val emailId get() = _emailId

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        navigationAppActivityBinding = ActivityNavigationAppBinding.inflate(layoutInflater)
        setContentView(navigationAppActivityBinding.root)

        listMainFragments.addAll(
            listOf(
                R.id.homeFragment,
                R.id.myProductsFragment
            )
        )
        setupNavigation()

        _emailId = intent.getStringExtra("email").toString()
        saveDataPreferences(_emailId)

    }

    private fun setupNavigation() {
        setSupportActionBar(navigationAppActivityBinding.toolBar)
        appBarConfiguration = AppBarConfiguration(
                setOf(
                    R.id.homeFragment,
                    R.id.myProductsFragment
                ),
                navigationAppActivityBinding.drawerLayout
        )
        setupActionBarWithNavController(
            navController,
            appBarConfiguration
        )
        navigationAppActivityBinding.navViewDrawerLayout.setupWithNavController(navController)
        navigationAppActivityBinding.bottomNavigationViewMainFlow.setupWithNavController(navController)
        navController.addOnDestinationChangedListener(this)
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration)
    }

    override fun onDestinationChanged(
        controller: NavController,
        destination: NavDestination,
        arguments: Bundle?
    ) {
        if (destination.id in listMainFragments) navigationAppActivityBinding.bottomNavigationViewMainFlow.visibility = View.VISIBLE
        else navigationAppActivityBinding.bottomNavigationViewMainFlow.visibility = View.GONE
    }


    private fun saveDataPreferences(email: String){
        val prefs = getSharedPreferences(R.string.prefs_file.toString(), Context.MODE_PRIVATE).edit()
        prefs.putString("email", email)
        prefs.apply()
    }

    override fun onResume() {
        super.onResume()
        val prefs = getSharedPreferences(R.string.prefs_file.toString(), Context.MODE_PRIVATE)
        if (prefs.getString("email", null) == null) finish()
    }



}