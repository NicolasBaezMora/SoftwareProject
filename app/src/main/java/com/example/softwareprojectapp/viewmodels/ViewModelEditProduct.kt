package com.example.softwareprojectapp.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.softwareprojectapp.firebase_repo.FirebaseRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ViewModelEditProduct: ViewModel() {

    private val firebaseRepo = FirebaseRepo()

    fun editProductData(idProduct: String, title: String, description: String, price: Double){
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                firebaseRepo.updateProductData(idProduct, title, description, price)
            }
        }
    }

    fun updatePhotoProduct(idProduct: String, photoUrl: String){
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                firebaseRepo.updatePhotoProduct(idProduct, photoUrl)
            }
        }
    }

}