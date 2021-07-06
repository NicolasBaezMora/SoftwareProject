package com.example.softwareprojectapp.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.softwareprojectapp.firebase_repo.FirebaseRepo
import com.example.softwareprojectapp.models.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ViewModelProfile: ViewModel() {

    private val firebaseRepo = FirebaseRepo()

    fun getDataFromUser(emailUserId: String): LiveData<User>{
        val userData = MutableLiveData<User>()
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO){
                firebaseRepo.fetchUserByEmailI(emailUserId)
            }
            result.observeForever {
                userData.value = it
            }
        }
        return userData
    }

}