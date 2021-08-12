package com.example.softwareprojectapp.viewmodels

import android.net.Uri
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.softwareprojectapp.R
import com.example.softwareprojectapp.firebase_repo.FirebaseRepo
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ViewModelAddProduct
@ViewModelInject constructor(private val firebaseRepo: FirebaseRepo, private val storageReference: StorageReference) : ViewModel() {

    private val _progressBar = MutableLiveData<Boolean>()
    val progressBar: LiveData<Boolean> get() = _progressBar

    fun addProductWithImage(emailUserId: String, title: String, description: String, price: Double, imageProduct: Uri): LiveData<String> {
        val idProductLiveData = MutableLiveData<String>()
        viewModelScope.launch {
            _progressBar.value = true
            val result = withContext(Dispatchers.IO) {
                firebaseRepo.addDataProduct(emailUserId, title, description, price)
            }
            result.observeForever {
                idProductLiveData.value = it
                val imgPhotoProduct =
                        storageReference.child("imagesPhotoProduct/$it/photo_product$emailUserId")
                imgPhotoProduct.putFile(imageProduct).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        imgPhotoProduct.downloadUrl.addOnCompleteListener { taskUriResult ->
                            if (taskUriResult.isSuccessful) updatePhotoProduct(
                                    it,
                                    taskUriResult.result.toString()
                            )
                        }
                    }

                }
            }
        }
        return idProductLiveData
    }

    private fun updatePhotoProduct(idProduct: String, photoUrl: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                firebaseRepo.updatePhotoProduct(idProduct, photoUrl)
            }
            _progressBar.value = false
        }
    }

    fun addProductWithOutImage(emailUserId: String, title: String, description: String, price: Double) {
        viewModelScope.launch {
            _progressBar.value = true
            withContext(Dispatchers.IO) {
                firebaseRepo.addDataProduct(emailUserId, title, description, price)
            }
            _progressBar.value = false
        }
    }

}