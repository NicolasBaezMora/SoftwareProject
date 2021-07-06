package com.example.softwareprojectapp.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.softwareprojectapp.firebase_repo.FirebaseRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ViewModelViewOrder: ViewModel() {
    private val firebaseRepo = FirebaseRepo()

    fun updateStateOrder(idOrder: String, newState: String){
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                firebaseRepo.updateStateOrder(idOrder, newState)
            }
        }
    }
}