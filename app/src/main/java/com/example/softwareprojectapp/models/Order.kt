package com.example.softwareprojectapp.models

import java.io.Serializable

data class Order(
        val id: String,
        val title: String,
        val address: String,
        val amount: Double,
        val emailBuyer: String,
        val emailSeller: String,
        val latitude: Double,
        val longitude: Double,
        val total: Double,
        val urlImage: String = "",
        val state: String
): Serializable