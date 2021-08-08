package com.example.softwareprojectapp.viewmodels

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.softwareprojectapp.firebase_repo.FirebaseRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ViewModelAddPhotoProfile @ViewModelInject constructor (private val firebaseRepo: FirebaseRepo): ViewModel() {

    fun updatePhotoProfileUser(email: String, photoUrl: String): LiveData<Boolean>{
        val resultFunction = MutableLiveData(false)
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO){
                firebaseRepo.updatePhotoProfileUser(email, photoUrl)
            }
            result.observeForever {
                resultFunction.postValue(true)
            }
        }
        return resultFunction
    }

}