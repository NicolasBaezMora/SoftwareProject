package com.example.softwareprojectapp.viewmodels

import androidx.lifecycle.ViewModel

class ViewModelViewProduct(var priceProduct: Double = 0.0): ViewModel() {

    var amount: Int = 1

    fun increaseAmount(){
        amount++
    }

    fun decreaseAmount(){
        if (amount > 1) amount--
    }

    fun calculatePriceProduct(){
        priceProduct *= amount
    }

}