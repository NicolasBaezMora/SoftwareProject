package com.example.softwareprojectapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.softwareprojectapp.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity(){

    private lateinit var mainActivityBinding: ActivityMainBinding
    private lateinit var appBarConfiguration: AppBarConfiguration

    private val navController by lazy { findNavController(R.id.fragmentNavigationLogIn) }

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_SoftwareProjectApp)
        super.onCreate(savedInstanceState)
        mainActivityBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mainActivityBinding.root)

        setupNavigation()
    }

    private fun setupNavigation() {
        setSupportActionBar(mainActivityBinding.toolBar)
        appBarConfiguration = AppBarConfiguration(
                setOf(
                        R.id.logInFragment
                )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration)
    }

}