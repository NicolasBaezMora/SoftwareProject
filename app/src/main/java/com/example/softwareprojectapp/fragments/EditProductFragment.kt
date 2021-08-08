package com.example.softwareprojectapp.fragments

import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.example.softwareprojectapp.R
import com.example.softwareprojectapp.databinding.FragmentEditProductBinding
import com.example.softwareprojectapp.models.Product
import com.example.softwareprojectapp.viewmodels.ViewModelEditProduct
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import java.lang.Exception
import javax.inject.Inject

@AndroidEntryPoint
class EditProductFragment : Fragment(), View.OnClickListener {

    private lateinit var editProductFragBinding: FragmentEditProductBinding
    private lateinit var productData: Product

    private val navigator by lazy { findNavController() }
    private val codePermissionMediaLocation by lazy { 101 }
    private val codeActivityResultMediaStore by lazy { 102 }
    private val vm by lazy { ViewModelProvider(this).get(ViewModelEditProduct::class.java) }

    @Inject
    lateinit var storageReference: StorageReference

    private var imageProduct: Uri? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment
        editProductFragBinding = FragmentEditProductBinding.inflate(layoutInflater)
        return editProductFragBinding.root

        //return inflater.inflate(R.layout.fragment_edit_product, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        productData = arguments?.getSerializable("product") as Product
        setupInputs()

        editProductFragBinding.btnAddProduct.setOnClickListener(this)
        editProductFragBinding.btnAddImage.setOnClickListener(this)

    }

    override fun onClick(view: View?){
        when(view?.id){
            editProductFragBinding.btnAddImage.id -> checkMediaPermission()
            editProductFragBinding.btnAddProduct.id -> updateProduct()
        }
    }

    private fun setupInputs(){
        editProductFragBinding.editTextProductTitle.setText(productData.title)
        editProductFragBinding.editTextProductDescription.setText(productData.description)
        editProductFragBinding.editTextProductPrice.setText(productData.price.toString())
        try{
            Picasso.get().load(productData.urlImage)
                .into(editProductFragBinding.imageViewPhotoProduct)
        }catch (e: Exception){
            Log.e("Error", e.message.toString())
        }
    }

    private fun updateProduct(){
        val title = editProductFragBinding.editTextProductTitle.text.toString()
        val description = editProductFragBinding.editTextProductDescription.text.toString()
        val price = if (editProductFragBinding.editTextProductPrice.text.isNullOrEmpty()) 0.0
        else editProductFragBinding.editTextProductPrice.text.toString().toDouble()

        if (checkOutData(title, description, price)){
            if (imageProduct != null){
                val imageReference = storageReference.child("imagesPhotoProduct/${productData.id}/photo_product${productData.emailId}")
                imageReference.putFile(imageProduct!!).addOnSuccessListener {
                    imageReference.downloadUrl.addOnSuccessListener {
                        vm.updatePhotoProduct(productData.id, it.toString())
                    }
                }
            }
            vm.editProductData(productData.id, title, description, price)
            navigator.navigateUp()
        }else{
            Toast.makeText(requireContext(), "Debes llenar la informacón del producto correctamente", Toast.LENGTH_SHORT).show()
        }
    }

    private fun checkOutData(title: String, description: String, price: Double): Boolean{
        return !(title.isEmpty() || description.isEmpty() || price == 0.0)
    }


    private fun checkMediaPermission() {
        if(ContextCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED){
            if (shouldShowRequestPermissionRationale(android.Manifest.permission.READ_EXTERNAL_STORAGE)){
                Toast.makeText(
                    requireContext(),
                    "Concede los permisos directamente en la configuración de la app",
                    Toast.LENGTH_LONG
                ).show()
            }else{
                requestPermissions(
                    arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                    codePermissionMediaLocation
                )
            }
        }else{
            openMediaLocation()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode){
            codeActivityResultMediaStore -> {
                imageProduct = data?.data
                if (imageProduct != null) editProductFragBinding.imageViewPhotoProduct.setImageURI(imageProduct)
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            codePermissionMediaLocation -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) openMediaLocation()
                else Snackbar.make(requireView(), "Al rechazar los permisos deberas confirmarlos manualmente", Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    private fun openMediaLocation() {
        val intentMediaStore = Intent(Intent.ACTION_PICK)
        intentMediaStore.type = "image/*"
        try {
            startActivityForResult(intentMediaStore, codeActivityResultMediaStore)
        }catch (e: ActivityNotFoundException){
            Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
}