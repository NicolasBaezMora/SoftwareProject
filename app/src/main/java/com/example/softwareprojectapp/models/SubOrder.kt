package com.example.softwareprojectapp.models

import java.io.Serializable

data class SubOrder(
        var product: Product,
        var amount: Int = 1,
        var total: Double
): Serializable