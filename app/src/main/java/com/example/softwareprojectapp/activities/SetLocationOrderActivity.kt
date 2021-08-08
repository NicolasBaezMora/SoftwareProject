package com.example.softwareprojectapp.activities

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.softwareprojectapp.R
import com.example.softwareprojectapp.databinding.ActivitySetUbicationOrderBinding
import com.example.softwareprojectapp.models.SubOrder
import com.example.softwareprojectapp.objects.Car
import com.example.softwareprojectapp.viewmodels.ViewModelSetLocationAndMakeOrder
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SetLocationOrderActivity :
    AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMarkerDragListener, View.OnClickListener, GoogleMap.OnMyLocationButtonClickListener {

    private lateinit var setUbicationOrderActivityBinding: ActivitySetUbicationOrderBinding
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var myMap: GoogleMap
    private lateinit var dataOrder: Bundle

    private var coordinates: LatLng? = null

    private val codeRequestPermissionLocation by lazy { 101 }
    private val vm by lazy { ViewModelProvider(this).get(ViewModelSetLocationAndMakeOrder::class.java) }
    private val emailId by lazy {
        this.getSharedPreferences(R.string.prefs_file.toString(), Context.MODE_PRIVATE)?.getString("email", null) ?: ""
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setUbicationOrderActivityBinding = ActivitySetUbicationOrderBinding.inflate(layoutInflater)
        setContentView(setUbicationOrderActivityBinding.root)

        dataOrder = intent.getBundleExtra("data")!!

        val mapFragment = supportFragmentManager.findFragmentById(R.id.fragmentMapOrder) as SupportMapFragment
        mapFragment.getMapAsync(this)

        setUbicationOrderActivityBinding.btnConfirmOrder.setOnClickListener(this)

    }

    override fun onClick(v: View?) {
        when(v?.id){
            setUbicationOrderActivityBinding.btnConfirmOrder.id -> addOrder()
        }
    }

    private fun addOrder(){
        val listOrders = dataOrder["listOrders"] as List<SubOrder>
        val address = setUbicationOrderActivityBinding.editTextAddress.text.toString()
        if (address.isNotEmpty() && coordinates != null){
            vm.addOrder(
                emailId,
                listOrders,
                coordinates?.latitude!!,
                coordinates?.longitude!!,
                address,
                "Pendiente"
            ).apply {
                Car.listOrder = mutableListOf()
                Toast.makeText(this@SetLocationOrderActivity, "Pedido realizado", Toast.LENGTH_SHORT).show()
                finish()
            }
        }else{
            Toast.makeText(
                this,
                "Debes especificar los datos de dirección correctamente",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun checkLocationPermission(): Boolean = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED


    override fun onMapReady(map: GoogleMap) {
        myMap = map
        myMap.uiSettings.isZoomControlsEnabled = true
        myMap.setOnMarkerDragListener(this)
        myMap.setOnMyLocationButtonClickListener(this)

        if (!checkLocationPermission()){
            if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)){
                Toast.makeText(this, "Configura los permisos de localización en los ajuste de la aplicación", Toast.LENGTH_LONG).show()
            }else{
                requestPermissions(
                        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                        codeRequestPermissionLocation
                )
            }
        }else{
            myMap.isMyLocationEnabled = true
            createMarker()
        }
    }

    @SuppressLint("MissingPermission")
    private fun createMarker(){
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        fusedLocationProviderClient.lastLocation.addOnCompleteListener {
            if (it.isSuccessful && it.result != null){
                coordinates = LatLng(it.result.latitude, it.result.longitude)
                myMap.addMarker(MarkerOptions().position(coordinates!!).title("Destino").draggable(true))
                myMap.moveCamera(CameraUpdateFactory.newLatLngZoom(coordinates!!, 18f))
            }
        }
    }

    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            codeRequestPermissionLocation -> if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                myMap.isMyLocationEnabled = true
                createMarker()
            }else{
                Toast.makeText(this, "Configura los permisos de localización en los ajuste de la aplicación", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onMarkerDragStart(marker: Marker){}

    override fun onMarkerDrag(marker: Marker){}

    override fun onMarkerDragEnd(marker: Marker) {
        coordinates = LatLng(marker.position.latitude, marker.position.longitude)
    }

    override fun onMyLocationButtonClick(): Boolean {
        return false
    }

}