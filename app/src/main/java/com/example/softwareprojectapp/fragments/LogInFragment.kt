package com.example.softwareprojectapp.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.example.softwareprojectapp.MainActivity
import com.example.softwareprojectapp.R
import com.example.softwareprojectapp.activities.NavigationAppActivity
import com.example.softwareprojectapp.databinding.FragmentLogInBinding
import com.example.softwareprojectapp.models.User
import com.example.softwareprojectapp.viewmodels.ViewModelLogIn
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LogInFragment : Fragment(), View.OnClickListener {

    private lateinit var logInFragBinding: FragmentLogInBinding
    private val navigator by lazy { findNavController() }
    private val GOOGLE_SIGN_IN = 100
    private lateinit var myGoogleClient: GoogleSignInClient
    private var account: GoogleSignInAccount? = null
    private val vm by lazy { ViewModelProvider(this).get(ViewModelLogIn::class.java) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_log_in, container, false)
        logInFragBinding = FragmentLogInBinding.inflate(layoutInflater)
        return logInFragBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sessionCheckOut()

        logInFragBinding.btnLogIn.setOnClickListener(this)
        logInFragBinding.btnSignIn.setOnClickListener(this)
        logInFragBinding.cardViewBtnSignInGoogle.setOnClickListener(this)

        vm.progressBar.observe(viewLifecycleOwner, Observer {
            logInFragBinding.progressBarLogIn.visibility = if(it) View.VISIBLE else View.GONE
        })
    }

    private fun sessionCheckOut(){
        val preferences = activity?.getSharedPreferences(R.string.prefs_file.toString(), Context.MODE_PRIVATE)
        val email = preferences?.getString("email", null)

        if (email != null){
            goMainFlowNavigation(email)
        }
    }

    //With this function we navigate to the main flow of the navigation of the app
    private fun goMainFlowNavigation(email: String){
        val intentGotoMainFlowNavigation = Intent(requireContext(), NavigationAppActivity::class.java).apply {
            putExtra("email", email)
        }
        startActivity(intentGotoMainFlowNavigation)
        (activity as MainActivity).finish()
    }

    override fun onClick(view: View?){
        when(view?.id){
            logInFragBinding.btnSignIn.id -> navigator.navigate(R.id.action_logInFragment_to_signInFragment)
            logInFragBinding.btnLogIn.id -> {
                emailPassSignIn()
            }
            logInFragBinding.cardViewBtnSignInGoogle.id -> {
                googleSignIn()
            }
        }
    }

    private fun emailPassSignIn(){
        val email = logInFragBinding.editTextEmail.text.toString()
        val password = logInFragBinding.editTextPassword.text.toString()

        if (checkOutFields(email, password)){
            FirebaseAuth.getInstance().signInWithEmailAndPassword(
                    email,
                    password
            ).addOnCompleteListener {
                if (it.isSuccessful){
                    goMainFlowNavigation(email)
                }else{
                    Snackbar.make(requireView(), "Credenciales incorrectas.", Snackbar.LENGTH_SHORT).show()
                }
            }
        }else{
            Snackbar.make(requireView(), "Debes llenar todos los campos.", Snackbar.LENGTH_SHORT).show()
        }

    }

    private fun googleSignIn(){
        val googleConfiguration = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .requestProfile()
                .build()
        myGoogleClient = GoogleSignIn.getClient(requireContext(), googleConfiguration)
        startActivityForResult(myGoogleClient.signInIntent, GOOGLE_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == GOOGLE_SIGN_IN){
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                account = task.getResult(ApiException::class.java)
                if (account != null){
                    val credential = GoogleAuthProvider.getCredential(account!!.idToken, null)
                    FirebaseAuth.getInstance().signInWithCredential(credential).addOnCompleteListener {
                        if (it.isSuccessful){
                            checkOutUserExist(
                                    User(
                                            account!!.displayName!!,
                                            account!!.email!!,
                                            account?.photoUrl.toString()
                                    )
                            )
                            goMainFlowNavigation(account!!.email!!)
                        }else{
                            Snackbar.make(requireView(), "No fue posible iniciar la sesion ${it.exception}", Snackbar.LENGTH_SHORT).show()
                        }
                    }
                }
            }catch (e: ApiException){
                Snackbar.make(requireView(), "Error: ${e.message}", Snackbar.LENGTH_SHORT).show()
            }
        }

    }

    private fun checkOutUserExist(user: User){
        vm.checkOutUserExistAndAdd(
                User(
                        user.name,
                        user.email,
                        user.photoUrl
                )
        )
    }

    private fun checkOutFields(email: String, password: String): Boolean{
        return !(email.isEmpty() || password.isEmpty())
    }

}