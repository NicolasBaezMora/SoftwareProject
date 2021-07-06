package com.example.softwareprojectapp.activities

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.softwareprojectapp.R
import com.example.softwareprojectapp.databinding.ActivityNavigationAppBinding

class NavigationAppActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var navigationAppActivityBinding: ActivityNavigationAppBinding
    private val navController by lazy { findNavController(R.id.fragmentNavigationMainFlow) }

    private lateinit var _emailId: String
    val emailId get() = _emailId

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        navigationAppActivityBinding = ActivityNavigationAppBinding.inflate(layoutInflater)
        setContentView(navigationAppActivityBinding.root)

        navigationAppActivityBinding.bottomNavigationViewMainFlow.setupWithNavController(navController)
        changeVisibilityBottomNavigation()

        _emailId = intent.getStringExtra("email").toString()
        saveDataPreferences(_emailId)

        navigationAppActivityBinding.btnShoppingCar.setOnClickListener(this)
        navigationAppActivityBinding.btnProfile.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v?.id){
            navigationAppActivityBinding.btnShoppingCar.id -> {
                val intentShoppingCar = Intent(this, CarActivity::class.java)
                startActivity(intentShoppingCar)
            }
            navigationAppActivityBinding.btnProfile.id -> {
                val intentProfile = Intent(this, ProfileActivity::class.java)
                startActivity(intentProfile)
            }
        }
    }

    private fun changeVisibilityBottomNavigation(){
        navController.addOnDestinationChangedListener { controller, destination, arguments ->
            when(destination.id){
                R.id.productViewFragment -> hideLayoutFromActivityParent()
                R.id.addProductFragment -> hideLayoutFromActivityParent()
                R.id.editProductFragment -> hideLayoutFromActivityParent()
                else -> showLayoutFromActivityParent()
            }
        }
    }

    private fun hideLayoutFromActivityParent(){
        navigationAppActivityBinding.bottomNavigationViewMainFlow.visibility = View.GONE
        navigationAppActivityBinding.viewToolBar.visibility = View.GONE
    }

    private fun showLayoutFromActivityParent(){
        navigationAppActivityBinding.bottomNavigationViewMainFlow.visibility = View.VISIBLE
        navigationAppActivityBinding.viewToolBar.visibility = View.VISIBLE
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