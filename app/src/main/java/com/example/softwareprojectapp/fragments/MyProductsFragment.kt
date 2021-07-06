package com.example.softwareprojectapp.fragments

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.softwareprojectapp.R
import com.example.softwareprojectapp.adapters.ProductInventoryAdapter
import com.example.softwareprojectapp.databinding.FragmentMyProductsBinding
import com.example.softwareprojectapp.models.Product
import com.example.softwareprojectapp.viewmodels.ViewModelProductInventory
import com.google.firebase.storage.FirebaseStorage

class MyProductsFragment : Fragment(), SearchView.OnQueryTextListener, View.OnClickListener {

    private lateinit var myProductsFragBinding: FragmentMyProductsBinding
    private lateinit var productInventoryAdapter: ProductInventoryAdapter
    private lateinit var dataCopyListProduct: List<Product>

    private val firebaseStorage by lazy { FirebaseStorage.getInstance().reference }
    private val vm by lazy { ViewModelProviders.of(this).get(ViewModelProductInventory::class.java) }
    private val navigator by lazy { findNavController() }
    private val emailId by lazy {
        activity?.getSharedPreferences(R.string.prefs_file.toString(), Context.MODE_PRIVATE)?.getString("email", null) ?: ""
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        myProductsFragBinding = FragmentMyProductsBinding.inflate(layoutInflater)
        return myProductsFragBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpRecycleView()
        fetchProductSnapshot(emailId)

        myProductsFragBinding.editTextSearchProducts.setOnQueryTextListener(this)
        myProductsFragBinding.floatingBtnAddProduct.setOnClickListener(this)
    }


    override fun onClick(view: View?){
        when(view?.id){
            myProductsFragBinding.floatingBtnAddProduct.id -> {
                navigator.navigate(R.id.action_myProductsFragment_to_addProductFragment)
            }
        }
    }

    private fun setUpRecycleView(){
        productInventoryAdapter = ProductInventoryAdapter(
            onClickEditListener = {
                navigator.navigate(R.id.action_myProductsFragment_to_editProductFragment, bundleOf("product" to it))
            },
            onClickDeleteListener = {
                val alertDialog = AlertDialog.Builder(requireContext())
                alertDialog.setTitle("Eliminar producto")
                alertDialog.setMessage("¿Estas seguro de eliminar este producto?")
                alertDialog.setPositiveButton("Si"){ dialog, id ->
                    //firebaseStorage.child("imagesPhotoProduct/${it.id}/photo_product$emailId").delete()
                    vm.deleteProduct(it.id)
                }
                alertDialog.setNegativeButton("Cancelar"){ _, _ ->}
                alertDialog.create()
                alertDialog.show()
            }
        )
        myProductsFragBinding.recyclerViewMyProducts.adapter = productInventoryAdapter
        myProductsFragBinding.recyclerViewMyProducts.layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.VERTICAL,
            false
        )
    }

    private fun fetchProductSnapshot(emailId: String){
        vm.fetchProductSnapshot(emailId).observe(viewLifecycleOwner, Observer {
            dataCopyListProduct = it
            productInventoryAdapter.listProducts = it
            productInventoryAdapter.notifyDataSetChanged()
        })
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        if (dataCopyListProduct.map { it.title }.contains(query)){
            val filterProductList = dataCopyListProduct.filter { it.title.toLowerCase().contains(query!!.toLowerCase()) }
            productInventoryAdapter.listProducts = filterProductList
            productInventoryAdapter.notifyDataSetChanged()
        }
        return false
    }
    override fun onQueryTextChange(newText: String?): Boolean {
        if (newText != ""){
            val filterProductList = dataCopyListProduct.filter { it.title.toLowerCase().contains(newText!!.toLowerCase()) }
            productInventoryAdapter.listProducts = filterProductList
            productInventoryAdapter.notifyDataSetChanged()
        }else{
            productInventoryAdapter.listProducts = dataCopyListProduct
            productInventoryAdapter.notifyDataSetChanged()
        }
        return false
    }

}

