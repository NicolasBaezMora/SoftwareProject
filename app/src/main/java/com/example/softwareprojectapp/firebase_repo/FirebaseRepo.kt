package com.example.softwareprojectapp.firebase_repo

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.softwareprojectapp.models.Order
import com.example.softwareprojectapp.models.SubOrder
import com.example.softwareprojectapp.models.Product
import com.example.softwareprojectapp.models.User
import com.google.firebase.firestore.FirebaseFirestore

class FirebaseRepo {

    private val firebaseDataBase = FirebaseFirestore.getInstance()

    fun addDataUser(name: String, email: String, photoUrl: String){
        firebaseDataBase.collection("USERS").document(email).set(
            mapOf(
                "NAME" to name,
                "EMAIL" to email,
                "PHOTOURL" to photoUrl
            )
        )
    }

    fun addDataOrder(emailBuyer: String, subOrderData: SubOrder, latitude: Double, longitude: Double, address: String, state: String){
        firebaseDataBase.collection("ORDERS").add(
            mapOf(
                "TITLE" to subOrderData.product.title,
                "EMAILBUYER" to emailBuyer,
                "EMAILSELLER" to subOrderData.product.emailId,
                "URLIMAGE" to subOrderData.product.urlImage,
                "AMOUNT" to subOrderData.amount,
                "TOTAL" to subOrderData.total,
                "LATITUDE" to latitude,
                "LONGITUDE" to longitude,
                "ADDRESS" to address,
                "STATE" to state
            )
        )
    }

    fun addDataProduct(emailUserId: String, title: String, description: String, price: Double): LiveData<String>{
        val idProductLiveData = MutableLiveData<String>()
        firebaseDataBase.collection("PRODUCTS").add(
                mapOf(
                        "EMAILIDUSER" to emailUserId,
                        "TITLE" to title,
                        "DESCRIPTION" to description,
                        "PRICE" to price
                )
        ).addOnSuccessListener {
            idProductLiveData.value = it.id
        }
        return idProductLiveData
    }

    fun updateProductData(idProduct: String, title: String, description: String, price: Double){
        firebaseDataBase.collection("PRODUCTS").document(idProduct).update(
            mapOf(
                "TITLE" to title,
                "DESCRIPTION" to description,
                "PRICE" to price
            )
        )
    }

    fun updateStateOrder(idOrder: String, newState: String){
        firebaseDataBase.collection("ORDERS").document(idOrder).update(
                mapOf(
                        "STATE" to newState
                )
        )
    }

    fun checkOutUserExist(email: String): LiveData<Boolean>{
        val mutableDataUserExist = MutableLiveData<Boolean>()
        firebaseDataBase.collection("USERS").whereEqualTo("EMAIL", email).get().addOnCompleteListener {
            mutableDataUserExist.value = it.isSuccessful && !it.result!!.isEmpty
        }
        return mutableDataUserExist
    }

    fun updatePhotoProfileUser(email: String, photoUrl: String){
        firebaseDataBase.collection("USERS").document(email).update(
            mapOf(
                "PHOTOURL" to photoUrl
            )
        )
    }

    fun deleteProduct(idProduct: String){
        firebaseDataBase.collection("PRODUCTS").document(idProduct).delete()
    }

    fun updatePhotoProduct(idProduct: String, photoUrl: String){
        firebaseDataBase.collection("PRODUCTS").document(idProduct).update(
                mapOf( "URLIMAGE" to photoUrl )
        )
    }

    fun fetchProductsOfUserSnapshot(userEmailId: String): LiveData<List<Product>>{
        val listProductsLiveData = MutableLiveData<List<Product>>()
        firebaseDataBase.collection("PRODUCTS")
                .whereEqualTo("EMAILIDUSER", userEmailId)
                .addSnapshotListener { value, error ->
                    if (error != null){
                        Log.e("Error", "${error.message}")
                        return@addSnapshotListener
                    }
                    val listProducts = mutableListOf<Product>()
                    for (item in value!!){
                        listProducts.add(
                                Product(
                                        item.id,
                                        item.getString("EMAILIDUSER")!!,
                                        item.getString("URLIMAGE") ?: "",
                                        item.getString("TITLE")!!,
                                        item.getString("DESCRIPTION")!!,
                                        item.getDouble("PRICE")!!
                                )
                        )
                    }
                    listProductsLiveData.value = listProducts
                }
        return listProductsLiveData
    }

    fun fetchProductsSnapshot(): LiveData<List<Product>>{
        val listProductsLiveData = MutableLiveData<List<Product>>()
        firebaseDataBase.collection("PRODUCTS").addSnapshotListener{ value, errorException ->
            if(errorException != null){
                Log.e("Error", "${errorException.message}")
                return@addSnapshotListener
            }
            val listProducts = mutableListOf<Product>()
            value?.forEach {
                listProducts.add(
                        Product(
                                it.id,
                                it.getString("EMAILIDUSER")!!,
                                it.getString("URLIMAGE") ?: "",
                                it.getString("TITLE")!!,
                                it.getString("DESCRIPTION")!!,
                                it.getDouble("PRICE")!!
                        )
                )
            }
            listProductsLiveData.value = listProducts
        }
        return listProductsLiveData
    }

    fun fetchUserByEmailI(emailUserId: String): LiveData<User>{
        val userData = MutableLiveData<User>()
        firebaseDataBase.collection("USERS").document(emailUserId).get().addOnCompleteListener {
            if (it.isSuccessful && it.result != null){
                userData.value = User(
                        it.result.getString("NAME")!!,
                        it.result.getString("EMAIL")!!,
                        it.result.getString("PHOTOURL") ?: ""
                )
            }
        }
        return userData
    }

    fun fetchMyOrders(emailIdSeller: String): LiveData<List<Order>>{
        val listOrders = MutableLiveData<List<Order>>()
        firebaseDataBase.collection("ORDERS").whereEqualTo("EMAILSELLER", emailIdSeller).addSnapshotListener { value, error ->
            if (error != null || value!!.documents.isEmpty()){
                return@addSnapshotListener
            }
            val myListOrders = mutableListOf<Order>()
            value.forEach {
                myListOrders.add(
                        Order(
                                it.id,
                                it.getString("TITLE")!!,
                                it.getString("ADDRESS")!!,
                                it.getDouble("AMOUNT")!!,
                                it.getString("EMAILBUYER")!!,
                                it.getString("EMAILSELLER")!!,
                                it.getDouble("LATITUDE")!!,
                                it.getDouble("LONGITUDE")!!,
                                it.getDouble("TOTAL")!!,
                                it.getString("URLIMAGE") ?: "",
                                it.getString("STATE") ?: ""
                        )
                )
            }
            listOrders.value = myListOrders
        }
        return listOrders
    }

    fun fetchOrders(emailIdBuyer: String): LiveData<List<Order>>{
        val listOrders = MutableLiveData<List<Order>>()
        firebaseDataBase.collection("ORDERS").whereEqualTo("EMAILBUYER", emailIdBuyer).addSnapshotListener { value, error ->
            if (error != null || value!!.documents.isEmpty()){
                Log.e("", "ERERERER")
                return@addSnapshotListener
            }
            val myListOrders = mutableListOf<Order>()
            value.forEach {
                myListOrders.add(
                        Order(
                                it.id,
                                it.getString("TITLE")!!,
                                it.getString("ADDRESS")!!,
                                it.getDouble("AMOUNT")!!,
                                it.getString("EMAILBUYER")!!,
                                it.getString("EMAILSELLER")!!,
                                it.getDouble("LATITUDE")!!,
                                it.getDouble("LONGITUDE")!!,
                                it.getDouble("TOTAL")!!,
                                it.getString("URLIMAGE") ?: "",
                                it.getString("STATE")?: ""
                        )
                )
            }
            listOrders.value = myListOrders
        }
        return listOrders
    }

}