package com.example.softwareprojectapp.models

import android.net.Uri
import java.io.Serializable

data class User(

    val name: String = "",
    val email: String = "",
    val photoUrl: String

): Serializable