package com.example.softwareprojectapp.objects

import com.example.softwareprojectapp.models.SubOrder

object Car {

    var listOrder = mutableListOf<SubOrder>()

    fun addOrderToCar(subOrder: SubOrder){
        listOrder.add(subOrder)
    }

    fun removeOrderFromCar(subOrder: SubOrder){
        if (listOrder.isNotEmpty()){
            listOrder.remove(subOrder)
        }
    }

    fun getTotal(): Double{
        return if(listOrder.isEmpty()) 0.0 else listOrder.map { it.total }.reduce { acc, d -> acc + d }
    }

}