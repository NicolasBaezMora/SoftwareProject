package com.example.softwareprojectapp.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.example.softwareprojectapp.R
import com.example.softwareprojectapp.databinding.FragmentSignInBinding
import com.example.softwareprojectapp.models.User
import com.example.softwareprojectapp.viewmodels.ViewModelSignIn
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignInFragment : Fragment(), View.OnClickListener {

    private lateinit var signInFragBinding: FragmentSignInBinding
    private val navigator by lazy { findNavController()}
    private val vm by lazy { ViewModelProvider(this).get(ViewModelSignIn::class.java) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_sign_in, container, false)
        signInFragBinding = FragmentSignInBinding.inflate(layoutInflater)
        return signInFragBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        signInFragBinding.btnComeBack.setOnClickListener(this)
        signInFragBinding.btnNextSignIn.setOnClickListener(this)

        vm.progressBar.observe(viewLifecycleOwner, Observer {
            signInFragBinding.progressBarSignIn.visibility = if(it) View.VISIBLE else View.GONE
        })

    }

    override fun onClick(v: View?) {
        when(v?.id){
            signInFragBinding.btnComeBack.id -> navigator.navigateUp()
            signInFragBinding.btnNextSignIn.id -> emailAndPasswordSignIn()
        }
    }

    private fun emailAndPasswordSignIn(){
        val name = signInFragBinding.editTextName.text.toString()
        val email = signInFragBinding.editTextEmail.text.toString()
        val password = signInFragBinding.editTextPass.text.toString()
        val confirmPassword = signInFragBinding.editTextConfirmPass.text.toString()

        if(checkOutFields(name, email, password, confirmPassword)){
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(
                    email,
                    password
            ).addOnCompleteListener {
                if(it.isSuccessful){
                    Toast.makeText(requireContext(), "Registrado exitosamente", Toast.LENGTH_SHORT).show()
                    checkOutUserExistAndGoPhotoProfile(
                            User(
                                    name,
                                    email,
                                    ""
                            )
                    )
                }else{
                    Snackbar.make(requireView(), "El correo no es valido.", Snackbar.LENGTH_SHORT).show()
                }
            }
        }else{
            Snackbar.make(requireView(), "Debes llenar todos los campos correctamente.", Snackbar.LENGTH_SHORT).show()
        }

    }

    private fun checkOutUserExistAndGoPhotoProfile(user: User) {
        vm.checkOutUserExistAndAdd(
                User(
                        user.name,
                        user.email,
                        user.photoUrl
                )
        )
        val dataInfo = bundleOf("user" to user)
        navigator.navigate(R.id.action_signInFragment_to_addProfilePhotoFragment, dataInfo)
    }

    private fun checkOutFields(name: String, email: String, password: String, confirmPassword: String): Boolean{
        return if ((name.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || (password != confirmPassword))){
            false
        }else{
            if (password.length < 8) {
                signInFragBinding.textViewLengthPass.text = getString(R.string.info_invalid_length_pass)
                false
            }else{
                signInFragBinding.textViewLengthPass.text = ""
                true
            }
        }
    }

}