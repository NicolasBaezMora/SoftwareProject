package com.example.softwareprojectapp.viewmodels

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.softwareprojectapp.firebase_repo.FirebaseRepo
import com.example.softwareprojectapp.models.Product
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ViewModelProductHome @ViewModelInject constructor(private val firebaseRepo: FirebaseRepo): ViewModel() {

    fun fetchProductsSnapshot(): LiveData<List<Product>> {
        val listProductsLiveData = MutableLiveData<List<Product>>()
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO){
                firebaseRepo.fetchProductsSnapshot()
            }
            result.observeForever {
                listProductsLiveData.value = it
            }
        }
        return listProductsLiveData
    }

}