package com.example.softwareprojectapp.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Html
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.example.softwareprojectapp.R
import com.example.softwareprojectapp.databinding.FragmentProductViewBinding
import com.example.softwareprojectapp.models.SubOrder
import com.example.softwareprojectapp.models.Product
import com.example.softwareprojectapp.objects.Car
import com.example.softwareprojectapp.viewmodels.ViewModelViewProduct
import com.squareup.picasso.Picasso

class ProductViewFragment : Fragment(), View.OnClickListener {

    private lateinit var productViewFragBinding: FragmentProductViewBinding

    private val navigator by lazy { findNavController() }
    private val productData by lazy { arguments?.getSerializable("productData") as Product }
    private val vm by lazy { ViewModelProviders.of(this).get(ViewModelViewProduct::class.java) }
    private val signDollar by lazy { Html.fromHtml("<b>$</b>") }



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment

        productViewFragBinding = FragmentProductViewBinding.inflate(layoutInflater)
        return productViewFragBinding.root
        //return inflater.inflate(R.layout.fragment_product_view, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupInfoProduct()


        productViewFragBinding.btnAdd.setOnClickListener(this)
        productViewFragBinding.btnSubtract.setOnClickListener(this)
        productViewFragBinding.btnAddToCar.setOnClickListener(this)
    }

    @SuppressLint("SetTextI18n")
    override fun onClick(view: View?){
        when(view?.id){
            productViewFragBinding.btnAdd.id -> {
                vm.increaseAmount()
                productViewFragBinding.amount.text = vm.amount.toString()
                productViewFragBinding.textViewPrice.text = "$signDollar${vm.amount * productData.price}"
            }
            productViewFragBinding.btnSubtract.id -> {
                vm.decreaseAmount()
                productViewFragBinding.amount.text = vm.amount.toString()
                productViewFragBinding.textViewPrice.text = "$signDollar${vm.amount * productData.price}"
            }
            productViewFragBinding.btnAddToCar.id -> {
                makeOrder()
                Toast.makeText(requireContext(), "Agregado al carrito", Toast.LENGTH_SHORT).show()
                navigator.navigateUp()
            }
        }
    }

    private fun makeOrder(){
        val newOrder = SubOrder(
                productData,
                vm.amount,
                (vm.amount * productData.price)
        )
        Car.addOrderToCar(newOrder)
    }

    @SuppressLint("SetTextI18n")
    private fun setupInfoProduct() {
        if (productData.urlImage != "") Picasso.get().load(productData.urlImage).into(productViewFragBinding.imageViewProduct)
        productViewFragBinding.textViewTitleProduct.text = Html.fromHtml("<b>${productData.title}</b>")
        productViewFragBinding.textViewDescriptionProduct.text = productData.description
        productViewFragBinding.amount.text = vm.amount.toString()
        productViewFragBinding.textViewPrice.text = "$signDollar${vm.amount * productData.price}"
    }
}