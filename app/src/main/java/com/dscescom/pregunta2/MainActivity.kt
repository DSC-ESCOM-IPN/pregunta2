package com.dscescom.pregunta2

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    //declare_analytics
    private lateinit var firebaseAnalytics: FirebaseAnalytics

    //declare_authentication
    private lateinit var auth: FirebaseAuth

    private lateinit var googleSignInClient: GoogleSignInClient
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Analytics
        firebaseAnalytics = Firebase.analytics

        //Auth
        auth = Firebase.auth

        //Setup
        setup()

    }

    public override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if(currentUser != null){
            getUserData(currentUser)
        }
    }


    private fun setup(){
        registerButton.setOnClickListener {
            val registerIntent = Intent(this, RegisterActivity::class.java)
            startActivity(registerIntent)
        }

        logInButton.setOnClickListener {
            val loginIntent = Intent(this, SigningActivity::class.java)
            startActivity(loginIntent)
        }
    }

    private fun getUserData(user: FirebaseUser) {
        val db = Firebase.firestore
        val userRef = db.collection("users").document(user.uid)

        userRef.get().addOnSuccessListener { doc ->
            if (!doc.exists()) {
                val user = doc.toObject<User>()
                val menuIntent = Intent(this, MenuActivity::class.java).apply {
                    putExtra("user", user)
                }
                startActivity(menuIntent)
            }
        }
    }

}