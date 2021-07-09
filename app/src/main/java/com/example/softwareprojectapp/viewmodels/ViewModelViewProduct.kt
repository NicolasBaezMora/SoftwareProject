package com.example.softwareprojectapp.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ViewModelViewProduct: ViewModel() {

    private var count = 1
    private val _amount = MutableLiveData(1)
    val amount: LiveData<Int> get() = _amount

    fun increaseAmount(){
        count++
        _amount.value = count
    }

    fun decreaseAmount(){
        if (count > 1) {
            count--
            _amount.value = count
        }
    }

}