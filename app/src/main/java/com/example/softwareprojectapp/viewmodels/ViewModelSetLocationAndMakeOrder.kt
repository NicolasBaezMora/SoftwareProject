package com.example.softwareprojectapp.viewmodels

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.softwareprojectapp.firebase_repo.FirebaseRepo
import com.example.softwareprojectapp.models.SubOrder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ViewModelSetLocationAndMakeOrder @ViewModelInject constructor(private val firebaseRepo: FirebaseRepo): ViewModel() {

    fun addOrder(emailBuyer: String, subOrderDataList: List<SubOrder>, latitude: Double, longitude: Double, address: String, state: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                firebaseRepo.addDataOrder(emailBuyer, subOrderDataList, latitude, longitude, address, state)
            }
        }
    }

}