package com.example.softwareprojectapp.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.softwareprojectapp.R
import com.example.softwareprojectapp.databinding.ActivityViewLocationOrderBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class ViewLocationOrderActivity : AppCompatActivity(), OnMapReadyCallback, View.OnClickListener {

    private lateinit var viewLocationOrderActivity: ActivityViewLocationOrderBinding
    private lateinit var myMap: GoogleMap
    private lateinit var dataLocation: List<Double>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewLocationOrderActivity = ActivityViewLocationOrderBinding.inflate(layoutInflater)
        setContentView(viewLocationOrderActivity.root)

        dataLocation = listOf(
            intent.getDoubleExtra("latitude", .0),
            intent.getDoubleExtra("longitude", .0)
        )

        val mapFragment = supportFragmentManager.findFragmentById(R.id.fragmentMap) as SupportMapFragment
        mapFragment.getMapAsync(this)

        viewLocationOrderActivity.btnBack.setOnClickListener(this)

    }

    override fun onClick(v: View?) {
        when(v?.id) {
            viewLocationOrderActivity.btnBack.id -> onBackPressed()
        }
    }

    override fun onMapReady(map: GoogleMap) {
        myMap = map
        myMap.uiSettings.isZoomControlsEnabled = true
        val latLngOrder = LatLng(dataLocation[0], dataLocation[1])
        myMap.addMarker(MarkerOptions().position(latLngOrder).title("Destino de la orden"))
        myMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngOrder, 18f))
    }
}