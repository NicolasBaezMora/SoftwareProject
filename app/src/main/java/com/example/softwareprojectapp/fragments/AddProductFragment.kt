package com.example.softwareprojectapp.fragments

import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.example.softwareprojectapp.R
import com.example.softwareprojectapp.activities.NavigationAppActivity
import com.example.softwareprojectapp.databinding.FragmentAddProductBinding
import com.example.softwareprojectapp.viewmodels.ViewModelAddProduct
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddProductFragment : Fragment(), View.OnClickListener {

    private lateinit var addProductFragBinding: FragmentAddProductBinding
    private var imageProduct: Uri? = null

    private val vm by lazy { ViewModelProvider(this).get(ViewModelAddProduct::class.java) }
    private val codePermissionMediaLocation by lazy { 10 }
    private val codeActivityResultMediaStore by lazy { 12 }
    private val emailUser by lazy { (activity as NavigationAppActivity).emailId }
    private val navigator by lazy { findNavController() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        addProductFragBinding = FragmentAddProductBinding.inflate(layoutInflater)
        return addProductFragBinding.root
        //return inflater.inflate(R.layout.fragment_add_product, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        addProductFragBinding.btnAddProduct.setOnClickListener(this)
        addProductFragBinding.btnAddImage.setOnClickListener(this)

        vm.progressBar.observe(viewLifecycleOwner, Observer {
            if (it){
                addProductFragBinding.frameProgressBar.visibility = View.VISIBLE
            }else{
                addProductFragBinding.frameProgressBar.visibility = View.GONE
                navigator.navigateUp()
            }
            addProductFragBinding.editTextProductDescription.isFocusable = !it
            addProductFragBinding.editTextProductPrice.isFocusable = !it
            addProductFragBinding.editTextProductTitle.isFocusable = !it
            addProductFragBinding.btnAddImage.isFocusable = !it
            addProductFragBinding.btnAddProduct.isFocusable = !it

        })

    }

    override fun onClick(view: View?) {
        when(view?.id){
            addProductFragBinding.btnAddProduct.id -> fillDataProduct()
            addProductFragBinding.btnAddImage.id -> checkMediaPermission()
        }
    }

    private fun fillDataProduct(){
        val title = addProductFragBinding.editTextProductTitle.text.toString()
        val description = addProductFragBinding.editTextProductDescription.text.toString()
        val price = if (addProductFragBinding.editTextProductPrice.text.isNullOrEmpty()) 0.0
        else addProductFragBinding.editTextProductPrice.text.toString().toDouble()

        if (checkDataProduct(title, description, price)){
            if (imageProduct != null){
                addDataProductWithImage(title, description, price)
            }else{
                val alertDialog = AlertDialog.Builder(context)
                alertDialog.setTitle("Imagen de tu producto!")
                alertDialog.setMessage("Estas seguro de añadir tu producto sin una imagen?")
                alertDialog.setPositiveButton("Si"){ dialog, id ->
                    addDataProductWithOutImage(title, description, price)
                    navigator.navigateUp()
                }
                alertDialog.setNegativeButton("Cancelar"){ dialog, id -> }
                alertDialog.create()
                alertDialog.show()
            }
        }else{
            Toast.makeText(requireContext(), "Debes llenar la informacón del producto correctamente", Toast.LENGTH_SHORT).show()
        }

    }

    private fun addDataProductWithOutImage(title: String, description: String, price: Double){
        vm.addProductWithOutImage(emailUser, title, description, price)
    }

    private fun addDataProductWithImage(title: String, description: String, price: Double){
        vm.addProductWithImage(emailUser, title, description, price, imageProduct!!)
    }

    private fun checkDataProduct(title: String, description: String, price: Double): Boolean{
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
                if (imageProduct != null) addProductFragBinding.imageViewPhotoProduct.setImageURI(imageProduct)
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            codePermissionMediaLocation -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) openMediaLocation()
                else Snackbar.make(requireView(), "Al rechazar los permisos deberas confirmarlos manualmente!!", Snackbar.LENGTH_SHORT).show()
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