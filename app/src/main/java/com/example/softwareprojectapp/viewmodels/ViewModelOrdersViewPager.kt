package com.example.softwareprojectapp.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.softwareprojectapp.firebase_repo.FirebaseRepo
import com.example.softwareprojectapp.models.Order
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ViewModelOrdersViewPager: ViewModel() {

    private val firebaseRepo = FirebaseRepo()

    fun fetchOrders(emailBuyer: String): LiveData<List<Order>> {
        val listMyOrders = MutableLiveData<List<Order>>()
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO){
                firebaseRepo.fetchOrders(emailBuyer)
            }
            result.observeForever {
                listMyOrders.value = it
            }
        }
        return listMyOrders
    }
}