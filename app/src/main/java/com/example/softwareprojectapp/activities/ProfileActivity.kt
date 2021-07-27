package com.example.softwareprojectapp.activities

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.example.softwareprojectapp.R
import com.example.softwareprojectapp.adapters.PagerAdapter
import com.example.softwareprojectapp.databinding.ActivityProfileBinding
import com.example.softwareprojectapp.viewmodels.ViewModelProfile
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ProfileActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var profileActivityBinding: ActivityProfileBinding

    private val vm by lazy { ViewModelProvider(this).get(ViewModelProfile::class.java) }
    private val emailId by lazy {
        this.getSharedPreferences(R.string.prefs_file.toString(), Context.MODE_PRIVATE).getString("email", null)
    }
    private val codePermissionAccessExternalStorage by lazy { 101 }
    private val codeIntentMediaStorage by lazy { 102 }

    @Inject
    lateinit var storageReference: StorageReference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        profileActivityBinding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(profileActivityBinding.root)

        setupViewPager()
        setupDataUser(emailId)
        profileActivityBinding.btnSettingsChangePhotoProfile.setOnClickListener(this)
        profileActivityBinding.btnSettingsLogOut.setOnClickListener(this)
    }


    private fun setupDataUser(emailIdUser: String?){
        if (emailIdUser != null){
            vm.getDataFromUser(emailIdUser).observe(this, Observer {
                Picasso.get().load(it.photoUrl).into(profileActivityBinding.imageViewPhotoProfile)
                profileActivityBinding.textViewName.text = Html.fromHtml("<b>${it.name}</b>")
                profileActivityBinding.textViewEmail.text = it.email
            })
        }
    }

    private fun setupViewPager() {
        profileActivityBinding.tabsLayout.setupWithViewPager(profileActivityBinding.viewPager)
        profileActivityBinding.viewPager.adapter = PagerAdapter(supportFragmentManager)
    }

    override fun onClick(v: View?) {
        when(v?.id){
            profileActivityBinding.btnSettingsChangePhotoProfile.id -> {
                changePhotoProfile()
            }
            profileActivityBinding.btnSettingsLogOut.id -> {
                val prefs = getSharedPreferences(R.string.prefs_file.toString(), Context.MODE_PRIVATE).edit()
                prefs.clear()
                prefs.apply()
                finish()
            }
        }
    }

    private fun changePhotoProfile() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED){
            if(shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)){
                Toast.makeText(this, "Configura los permisos en los ajustes de la aplicación", Toast.LENGTH_SHORT).show()
            }else{
                requestPermissions(
                        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                        codePermissionAccessExternalStorage
                )
            }
        }else{
            choicePhoto()
        }
    }

    private fun choicePhoto() {
        val intentMediaStorage = Intent(Intent.ACTION_PICK)
        intentMediaStorage.type = "image/*"
        try {
            startActivityForResult(intentMediaStorage, codeIntentMediaStorage)
        }catch (e: ActivityNotFoundException){
            Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            codePermissionAccessExternalStorage -> if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                choicePhoto()
            } else {
                Toast.makeText(this, "Al rechazar los permisos deberas habilitarlos manualmente", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode){
            codeIntentMediaStorage -> if (resultCode == RESULT_OK) {
                val imageUri: Uri = data?.data!!
                val imgPhotoProfileReference = storageReference.child("imagesPhotoProfile/$emailId/photo_profile_$emailId")
                imgPhotoProfileReference.putFile(imageUri).addOnSuccessListener {
                    Toast.makeText(this, "Foto actualizada", Toast.LENGTH_SHORT).show()
                }.addOnFailureListener { Toast.makeText(this, "No fue posible actualizar la foto de perfil", Toast.LENGTH_SHORT).show() }
            }
        }
    }
}