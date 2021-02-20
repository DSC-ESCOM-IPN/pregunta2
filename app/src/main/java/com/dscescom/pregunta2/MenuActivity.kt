package com.dscescom.pregunta2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_menu.*

class MenuActivity : AppCompatActivity() {

    //declare_authentication
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        auth = Firebase.auth

        //val bundle = intent.extras
        val username = auth.currentUser?.email
        setup(username ?: "")
    }

    private fun setup(username:String){
        Toast.makeText(this, "Hola $username", Toast.LENGTH_SHORT).show()

        logoutButton.setOnClickListener {
            signOut()
        }

        playButton.setOnClickListener{
            val rouleteIntent = Intent(this, RouleteActivity::class.java)
            startActivity(rouleteIntent)
        }

    }

    private fun signOut() {
        auth.signOut()
        val mainIntent = Intent(this, MainActivity::class.java)
        startActivity(mainIntent)
    }

}
