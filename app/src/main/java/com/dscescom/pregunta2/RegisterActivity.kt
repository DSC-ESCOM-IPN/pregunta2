package com.dscescom.pregunta2

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_register.*
import java.io.Serializable

class RegisterActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var preferences: SharedPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        auth = Firebase.auth
        setup()
    }

    private fun setup() {

        preferences = getSharedPreferences("userData", MODE_PRIVATE)

        backButton.setOnClickListener {
            val backIntent = Intent(this, MainActivity::class.java)
            startActivity(backIntent)
        }

        signInButton.setOnClickListener {
            val registerIntent = Intent(this, SigningActivity::class.java)
            startActivity(registerIntent)
        }

        logInButton.setOnClickListener {
            validateData(emailEditText.text.toString(), passwordEditText.text.toString(), passwordCEditText.text.toString())
        }


    }

    //validate data
    private fun validateData(email: String, password: String, passwordConfirm: String) {
        if (email.isNotEmpty() && password.isNotEmpty() && passwordConfirm.isNotEmpty()) {
            if (password == passwordConfirm) {
                createAccount(email, password)
            } else {
                Toast.makeText(this, "Las contraseñas no coinciden.", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "Alguno de los campos esta vacio.", Toast.LENGTH_SHORT).show()
        }
    }

    //Create account
    private fun createAccount(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                val user = auth.currentUser
                if (user != null)
                    registerUser(user)
                else
                    err()
            } else {
                err()
            }
        }
    }

    //Register user account in Firestore
    private fun registerUser(user: FirebaseUser) {
        val db = Firebase.firestore
        val userData = User(user.displayName, user.email, 0)
        db.collection("users").document(user.uid)
                .set(userData)
                .addOnSuccessListener {
                    val menuIntent = Intent(this, MenuActivity::class.java).apply {
                        putExtra("user", userData)
                    }

                    startActivity(menuIntent)
                }
                .addOnFailureListener { err() }
    }

    //Toast Error function
    private fun err() {
        Toast.makeText(this, "Ocurrio un error intentalo mas tarde.", Toast.LENGTH_SHORT).show()
    }
}

public data class User(
        val nombre: String? = null,
        val correo: String? = null,
        var score: Int? = null
) : Serializable