package com.example.softwareprojectapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.softwareprojectapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(){


    override fun onCreate(savedInstanceState: Bundle?) {

        setTheme(R.style.Theme_SoftwareProjectApp)
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }




}