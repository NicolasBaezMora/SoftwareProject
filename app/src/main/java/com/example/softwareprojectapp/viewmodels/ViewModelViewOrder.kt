package com.example.softwareprojectapp.viewmodels

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.softwareprojectapp.firebase_repo.FirebaseRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ViewModelViewOrder @ViewModelInject constructor(private val firebaseRepo: FirebaseRepo): ViewModel() {

    fun updateStateOrder(idOrder: String, newState: String){
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                firebaseRepo.updateStateOrder(idOrder, newState)
            }
        }
    }
}