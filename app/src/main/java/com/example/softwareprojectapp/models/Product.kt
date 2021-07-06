package com.example.softwareprojectapp.models

import java.io.Serializable

data class Product(
        val id: String,
        val emailId: String,
        val urlImage: String = "",
        val title: String,
        val description: String,
        val price: Double
): Serializable