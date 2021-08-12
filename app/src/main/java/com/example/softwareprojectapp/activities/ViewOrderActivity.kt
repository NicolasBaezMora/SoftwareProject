package com.example.softwareprojectapp.activities

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProvider
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
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ViewOrderActivity : AppCompatActivity(), View.OnClickListener, AdapterView.OnItemSelectedListener {

    private lateinit var viewOrderActivityBinding: ActivityViewOrderBinding
    private lateinit var order: Order

    private val signDollar = Html.fromHtml("<b>$</b>")

    private val vm by lazy { ViewModelProvider(this).get(ViewModelViewOrder::class.java) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewOrderActivityBinding = ActivityViewOrderBinding.inflate(layoutInflater)
        setContentView(viewOrderActivityBinding.root)

        order = intent.getSerializableExtra("order") as Order

        setupViewElements()

    }

    @SuppressLint("SetTextI18n")
    private fun setupViewElements() {
        Picasso.get().load(order.urlImage).into(viewOrderActivityBinding.imageViewProductOrder)
        viewOrderActivityBinding.textViewTitleOrder.text = order.title
        viewOrderActivityBinding.textViewTotal.text = "$signDollar${order.total}"
        viewOrderActivityBinding.textViewAmount.text = order.amount.toInt().toString()
        viewOrderActivityBinding.textViewAddress.text = order.address
        viewOrderActivityBinding.btnViewLocation.setOnClickListener(this)

        ArrayAdapter.createFromResource(
                this,
                R.array.spinner_options,
                android.R.layout.simple_spinner_item
        ).also { arrayAdapter ->
            arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            viewOrderActivityBinding.spinner.adapter = arrayAdapter
        }
        viewOrderActivityBinding.spinner.onItemSelectedListener = this
    }

    override fun onClick(v: View?) {
        when(v?.id){
            viewOrderActivityBinding.btnViewLocation.id -> {
                val intent = Intent(this, ViewLocationOrderActivity::class.java)
                intent.putExtra("latitude", order.latitude)
                intent.putExtra("longitude", order.longitude)
                startActivity(intent)
            }
        }
    }

    private fun updateStateOrder(state: String){
        if (state.isNotEmpty()){
            vm.updateStateOrder(order.id, state)
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        Toast.makeText(this, "Nada seleccionado", Toast.LENGTH_SHORT).show()
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val item = parent?.getItemAtPosition(position)
        updateStateOrder(item.toString())
    }

}