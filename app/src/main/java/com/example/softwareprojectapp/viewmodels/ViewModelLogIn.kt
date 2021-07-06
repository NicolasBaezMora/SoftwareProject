package com.example.softwareprojectapp.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.softwareprojectapp.firebase_repo.FirebaseRepo
import com.example.softwareprojectapp.models.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ViewModelLogIn: ViewModel() {

    private val firebaseRepo = FirebaseRepo()

    private val _progressBar = MutableLiveData<Boolean>()
    val progressBar: LiveData<Boolean> get() = _progressBar

    fun checkOutUserExistAndAdd(user: User){
        viewModelScope.launch{
            _progressBar.value = true
            val queryResult = withContext(Dispatchers.IO){
                firebaseRepo.checkOutUserExist(user.email)
            }
            queryResult.observeForever {
                if (!it){
                    firebaseRepo.addDataUser(user.name, user.email, user.photoUrl)
                }
            }
            _progressBar.value = false
        }

    }

}