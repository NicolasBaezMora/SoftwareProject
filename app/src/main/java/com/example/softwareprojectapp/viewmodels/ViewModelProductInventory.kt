package com.example.softwareprojectapp.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.softwareprojectapp.firebase_repo.FirebaseRepo
import com.example.softwareprojectapp.models.Product
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ViewModelProductInventory: ViewModel() {

    private val firebaseRepo = FirebaseRepo()

    fun fetchProductSnapshot(userEmailId: String): LiveData<List<Product>>{
        val listProductLiveData = MutableLiveData<List<Product>>()
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO){
                firebaseRepo.fetchProductsOfUserSnapshot(userEmailId)
            }
            result.observeForever {
                listProductLiveData.value = it
            }
        }
        return listProductLiveData
    }

    fun deleteProduct(idProduct: String){
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                firebaseRepo.deleteProduct(idProduct)
            }
        }
    }

}