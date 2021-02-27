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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        auth = Firebase.auth

        val obj = intent.extras?.getSerializable("user") as User
        val username = obj.nombre ?: obj.correo
        //val username = ""
        setup(username ?: "")
    }

    private fun setup(username: String) {

        Toast.makeText(this, "Hola $username", Toast.LENGTH_SHORT).show()

        //Toast.makeText(this, "Pref ${userData.correo}", Toast.LENGTH_SHORT).show()
        logoutButton.setOnClickListener {
            signOut()
        }

        playButton.setOnClickListener {
            val rouletteIntent = Intent(this, RouleteActivity::class.java)
            startActivity(rouletteIntent)
        }

    }

    private fun signOut() {
        auth.signOut()
        val mainIntent = Intent(this, MainActivity::class.java)
        startActivity(mainIntent)
    }

}
