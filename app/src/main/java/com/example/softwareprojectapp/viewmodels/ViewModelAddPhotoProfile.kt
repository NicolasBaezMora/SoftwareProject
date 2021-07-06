package com.example.softwareprojectapp.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.softwareprojectapp.firebase_repo.FirebaseRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ViewModelAddPhotoProfile: ViewModel() {

    private val firebaseRepo = FirebaseRepo()

    fun updatePhotoProfileUser(email: String, photoUrl: String){
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                firebaseRepo.updatePhotoProfileUser(email, photoUrl)
            }
        }
    }

}