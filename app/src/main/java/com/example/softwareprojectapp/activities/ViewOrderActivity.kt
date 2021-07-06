package com.example.softwareprojectapp.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProviders
import com.example.softwareprojectapp.R
import com.example.softwareprojectapp.databinding.ActivityViewOrderBinding
import com.example.softwareprojectapp.models.Order
import com.example.softwareprojectapp.viewmodels.ViewModelViewOrder
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class ViewOrderActivity : AppCompatActivity(), OnMapReadyCallback, View.OnClickListener {

    private lateinit var viewOrderActivityBinding: ActivityViewOrderBinding
    private lateinit var myMap: GoogleMap
    private lateinit var order: Order

    private val vm by lazy { ViewModelProviders.of(this).get(ViewModelViewOrder::class.java) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewOrderActivityBinding = ActivityViewOrderBinding.inflate(layoutInflater)
        setContentView(viewOrderActivityBinding.root)

        order = intent.getSerializableExtra("order") as Order

        viewOrderActivityBinding.textViewDestinationPlace.setText(order.address)
        viewOrderActivityBinding.btnUpdateStateOrder.setOnClickListener(this)
        viewOrderActivityBinding.editTextOrderState.setText(order.state)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.fragmentMap) as SupportMapFragment
        mapFragment.getMapAsync(this)

    }

    override fun onMapReady(map: GoogleMap) {
        myMap = map
        myMap.uiSettings.isZoomControlsEnabled = true
        val latLngOrder = LatLng(order.latitude, order.longitude)
        myMap.addMarker(MarkerOptions().position(latLngOrder).title("Destino de la orden"))
        myMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngOrder, 18f))
    }

    override fun onClick(v: View?) {
        when(v?.id){
            viewOrderActivityBinding.btnUpdateStateOrder.id -> {
                updateStateOrder()
                onBackPressed()
            }
        }
    }

    private fun updateStateOrder(){
        val state = viewOrderActivityBinding.editTextOrderState.text.toString()
        if (state.isNotEmpty()){
            vm.updateStateOrder(order.id, state)
        }
    }

}