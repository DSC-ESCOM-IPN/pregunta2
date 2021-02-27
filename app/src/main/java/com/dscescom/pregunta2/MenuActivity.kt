package com.dscescom.pregunta2

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.api.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_menu.*

class MenuActivity : AppCompatActivity() {

    //declare_authentication
    private lateinit var auth: FirebaseAuth
    private lateinit var currentUser: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        auth = Firebase.auth

        if(intent.hasExtra("user")){
            currentUser = intent.extras?.getSerializable("user") as User
        }

        setup()
    }

    private fun setup() {

        logoutButton.setOnClickListener {
            signOut()
        }

        playButton.setOnClickListener {
            val rouletteIntent = Intent(this, MatchesActivity::class.java)
            startActivity(rouletteIntent)
        }

        contactsButton.setOnClickListener {
            val contactsIntent = Intent(this, ContactsActivity::class.java)
            startActivity(contactsIntent)
        }

        profileButton.setOnClickListener {
            val profileIntent = Intent(this, ProfileActivity::class.java)
            startActivity(profileIntent)
        }

    }

    private fun signOut() {
        auth.signOut()
        val mainIntent = Intent(this, MainActivity::class.java)
        startActivity(mainIntent)
    }

}
