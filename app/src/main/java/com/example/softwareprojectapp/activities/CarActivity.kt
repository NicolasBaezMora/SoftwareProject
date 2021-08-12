package com.example.softwareprojectapp.activities

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.view.View
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.softwareprojectapp.adapters.OrderCarAdapter
import com.example.softwareprojectapp.databinding.ActivityCarBinding
import com.example.softwareprojectapp.objects.Car

class CarActivity: AppCompatActivity(), View.OnClickListener {

    private lateinit var orderCarAdapter: OrderCarAdapter
    private lateinit var carActivityBinding: ActivityCarBinding

    private val deliveryService = 10500.0

    private val signDollar by lazy { Html.fromHtml("<b>$</b>") }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        carActivityBinding = ActivityCarBinding.inflate(layoutInflater)
        setContentView(carActivityBinding.root)
        if (Car.listOrder.isEmpty()) carActivityBinding.layoutVoidCart.visibility = View.VISIBLE else carActivityBinding.layoutVoidCart.visibility = View.GONE

    }

    @SuppressLint("SetTextI18n")
    override fun onResume() {
        super.onResume()
        setupRecyclerView()

        carActivityBinding.textViewProductsTotal.text = "$signDollar${Car.getTotal()}"
        carActivityBinding.textViewDeliveryServiceTotal.text = "$signDollar$deliveryService"
        carActivityBinding.textViewTotalOrderCar.text = if (Car.getTotal() != 0.0) "$signDollar${Car.getTotal() + deliveryService}" else "${signDollar}0.0"
        carActivityBinding.floatingBtnMakeOrder.setOnClickListener(this)

    }

    @SuppressLint("SetTextI18n")
    private fun setupRecyclerView() {
        orderCarAdapter = OrderCarAdapter(Car.listOrder){
            Car.removeOrderFromCar(it)
            orderCarAdapter.notifyDataSetChanged()
            carActivityBinding.textViewProductsTotal.text = "$signDollar${Car.getTotal()}"
            carActivityBinding.textViewTotalOrderCar.text = if (Car.getTotal() != 0.0) "$signDollar${Car.getTotal() + deliveryService}" else "${signDollar}0.0"
            if (Car.listOrder.isEmpty()) carActivityBinding.layoutVoidCart.visibility = View.VISIBLE else carActivityBinding.layoutVoidCart.visibility = View.GONE
        }
        carActivityBinding.recyclerViewOrders.adapter = orderCarAdapter
        carActivityBinding.recyclerViewOrders.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
    }

    override fun onClick(v: View?) {
        when(v?.id){
            carActivityBinding.floatingBtnMakeOrder.id -> makeOrder()
        }
    }

    private fun makeOrder(){
        if (Car.listOrder.isNotEmpty()){
            val bundleData = bundleOf(
                "listOrders" to Car.listOrder,
                "balance" to Car.getTotal() + deliveryService
            )
            val intentMakeOrder = Intent(this, SetLocationOrderActivity::class.java).apply {
                putExtra("data", bundleData)
            }
            startActivity(intentMakeOrder)
        }else {
            Toast.makeText(this, "No hay pedidos en el carrito", Toast.LENGTH_SHORT).show()
        }
    }
}