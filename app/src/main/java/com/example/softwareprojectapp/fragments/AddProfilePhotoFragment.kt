package com.example.softwareprojectapp.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.example.softwareprojectapp.MainActivity
import com.example.softwareprojectapp.R
import com.example.softwareprojectapp.activities.NavigationAppActivity
import com.example.softwareprojectapp.databinding.FragmentAddProfilePhotoBinding
import com.example.softwareprojectapp.models.User
import com.example.softwareprojectapp.viewmodels.ViewModelAddPhotoProfile
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.ByteArrayOutputStream


class AddProfilePhotoFragment : Fragment(), View.OnClickListener {

    private lateinit var addProfilePhotoFragBinding: FragmentAddProfilePhotoBinding
    private lateinit var imageUri: Uri
    private lateinit var dataUser: User

    private val navigator by lazy { findNavController() }
    private val storageReference by lazy { FirebaseStorage.getInstance().reference }
    private val vm by lazy { ViewModelProviders.of(this).get(ViewModelAddPhotoProfile::class.java) }
    private val codePermissionCamera by lazy { 100 }
    private val codePermissionMediaStore by lazy { 102 }
    private val codeActivityResultCamera by lazy { 101 }
    private val codeActivityResultMediaStore by lazy { 103 }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment
        addProfilePhotoFragBinding = FragmentAddProfilePhotoBinding.inflate(layoutInflater)
        return addProfilePhotoFragBinding.root
        //return inflater.inflate(R.layout.fragment_add_profile_photo, container, false)
    }

    //With this function we navigate to the main flow of the navigation of the app
    private fun goMainFlowNavigation(email: String){
        val intentGotoMainFlowNavigation = Intent(requireContext(), NavigationAppActivity::class.java).apply {
            putExtra("email", email)
        }
        startActivity(intentGotoMainFlowNavigation)
        (activity as MainActivity).finish()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dataUser = arguments?.get("user") as User

        addProfilePhotoFragBinding.btnComeBack.setOnClickListener(this)
        addProfilePhotoFragBinding.btnTakePhoto.setOnClickListener(this)
        addProfilePhotoFragBinding.btnUploadPhoto.setOnClickListener(this)



    }

    override fun onClick(view: View?){
        when(view?.id){
            addProfilePhotoFragBinding.btnComeBack.id -> {
                val builderDialog = AlertDialog.Builder(requireContext())
                builderDialog.setTitle("¿Deseas volver?")
                builderDialog.setMessage("Si vuelves deberas agregar tu foto desde tu perfil.")
                builderDialog.setPositiveButton("Si"){ _, _ -> navigator.navigate(R.id.action_addProfilePhotoFragment_to_signInFragment) }
                builderDialog.setNegativeButton("Cancelar"){ _, _ ->}
                builderDialog.create()
                builderDialog.show()
            }
            addProfilePhotoFragBinding.btnTakePhoto.id -> checkCameraPermission()
            addProfilePhotoFragBinding.btnUploadPhoto.id -> checkMediaStorePermission()
            addProfilePhotoFragBinding.btnSkipPhoto.id -> goMainFlowNavigation(dataUser.email)
        }
    }

    private fun updatePhotoProfile(imageProfile: Uri){
        val imgPhotoProfileReference = storageReference.child("imagesPhotoProfile/${dataUser.email}/photo_profile_${dataUser.email}")
        imgPhotoProfileReference.putFile(imageProfile).addOnCompleteListener{ taskPutFile ->
            if (taskPutFile.isSuccessful){
                imgPhotoProfileReference.downloadUrl.addOnCompleteListener{ taskGetUrl ->
                    if (taskGetUrl.isSuccessful) vm.updatePhotoProfileUser(dataUser.email, taskGetUrl.result.toString())
                }
            }
        }
    }

    private fun checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)){
                //PERMISSIONS ALREADY DENIED
                Toast.makeText(
                        requireContext(),
                    "Concede los permisos directamente en la configuración de la app",
                    Toast.LENGTH_LONG
                ).show()
            }else{
                requestPermissions(
                        arrayOf(
                                Manifest.permission.CAMERA
                        ),
                        codePermissionCamera
                )
            }
        }else{
            openCamera()
        }
    }

    private fun checkMediaStorePermission(){
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            if (shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)){
                Toast.makeText(
                        requireContext(),
                    "Concede los permisos directamente en la configuración de la app",
                    Toast.LENGTH_LONG
                ).show()
            }else{
                requestPermissions(
                        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                        codePermissionMediaStore
                )
            }
        }else{
            openMediaStore()
        }
    }


    @SuppressLint("QueryPermissionsNeeded")
    private fun openCamera(){
        val intentCamera = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        /*if (intentCamera.resolveActivity(requireContext().packageManager) != null){
            startActivityForResult(intentCamera, codeActivityResultCamera)
        }*/ //That might returns false even if the device has a camera
        try {
            startActivityForResult(intentCamera, codeActivityResultCamera)
        }catch (e: ActivityNotFoundException){
            Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
    private fun openMediaStore() {
        val intentMediaStore = Intent(Intent.ACTION_PICK)
        intentMediaStore.type = "image/*"
        try {
            startActivityForResult(intentMediaStore, codeActivityResultMediaStore)
        }catch (e: ActivityNotFoundException){
            Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            codePermissionCamera -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) openCamera()
                else Snackbar.make(requireView(), "Al rechazar los permisos deberas confirmarlos manualmente", Snackbar.LENGTH_SHORT).show()
            }
            codePermissionMediaStore -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) openMediaStore()
                else Snackbar.make(requireView(), "Al rechazar los permisos deberas confirmarlos manualmente", Snackbar.LENGTH_SHORT).show()
            }
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode){
            codeActivityResultCamera -> {
                if (resultCode == RESULT_OK){
                    val imageBitMap: Bitmap = data?.extras?.get("data") as Bitmap
                    addProfilePhotoFragBinding.imageViewProfilePhoto.setImageBitmap(imageBitMap)
                    imageUri = convertBitmapToUri(imageBitMap)
                    confirmPhotoProfile()
                }
            }
            codeActivityResultMediaStore -> {
                if (resultCode == RESULT_OK) {
                    imageUri = data?.data!!
                    addProfilePhotoFragBinding.imageViewProfilePhoto.setImageURI(imageUri)
                    confirmPhotoProfile()
                }
            }
        }
    }

    private fun confirmPhotoProfile(){
        val builderDialog = AlertDialog.Builder(requireContext())
        builderDialog.setTitle("Confirmar foto de perfil")
        builderDialog.setMessage("¿Deseas usar esta imagen como foto de perfil?")
        builderDialog.setPositiveButton("Si"){ id, dialog ->
            updatePhotoProfile(imageUri)
            goMainFlowNavigation(dataUser.email)
        }
        builderDialog.setNegativeButton("Elegir otra"){ _, _ ->}
        builderDialog.create()
        builderDialog.show()
    }

    private fun convertBitmapToUri(imageBitmap: Bitmap): Uri{
        imageBitmap.compress(
                Bitmap.CompressFormat.JPEG,
                100,
                ByteArrayOutputStream()
        )
        val path = MediaStore.Images.Media.insertImage(
                requireContext().contentResolver,
                imageBitmap,
                "my title",
                null
        )
        return Uri.parse(path)
    }

}