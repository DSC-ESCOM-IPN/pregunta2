package com.dscescom.pregunta2

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_login.*
import java.io.Serializable

class SigningActivity : AppCompatActivity() {

    //declare auth vars
    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()


        googleSignInClient = GoogleSignIn.getClient(this, gso)
        auth = Firebase.auth
        setup()
    }

    private fun setup() {

        backButton2.setOnClickListener() {
            val backIntent = Intent(this, MainActivity::class.java)
            startActivity(backIntent)
        }

        logInButton3.setOnClickListener {
            validateData(emailEditText2.text.toString(), passwordEditText2.text.toString())
        }

        gLoginButton.setOnClickListener {
            val signInIntent = googleSignInClient.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }
    }

    private fun validateData(email: String, password: String) {
        if (email.isNotEmpty() && password.isNotEmpty()) {
            login(email, password)
        } else {
            Toast.makeText(this, "Alguno de los campos esta vacio.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                err()
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val user = auth.currentUser
                        if (user != null)
                            validateUserRegistration(user)
                        else
                            err()
                    } else
                        err()
                }
    }

    private fun login(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                val user = auth.currentUser
                if (user != null)
                    validateUserRegistration(user)
                else
                    err()
            } else {
                Toast.makeText(baseContext, "Credenciales incorrectas.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    //Register user account in Firestore
    private fun validateUserRegistration(user: FirebaseUser) {
        val db = Firebase.firestore
        val userRef = db.collection("users").document(user.uid)
        var userData = User(user.displayName, user.email, 0)

        userRef.get().addOnSuccessListener { doc ->
            if (!doc.exists()) {
                userRef.set(userData)
                        .addOnSuccessListener {
                            Toast.makeText(this, "Usuario Registrado", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener {
                            err()
                        }
            }else{
                userData = doc.toObject<User>()!!
            }
            val menuIntent = Intent(this, MenuActivity::class.java).apply {
                putExtra("user", userData)
            }
            startActivity(menuIntent)
        }

    }

    //Toast Error function
    private fun err() {
        Toast.makeText(this, "Ocurrio un error intentalo mas tarde.", Toast.LENGTH_SHORT).show()
    }

    companion object {
        private const val RC_SIGN_IN = 9001
    }

}