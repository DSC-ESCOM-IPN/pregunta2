package com.dscescom.pregunta2

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_contactos.*

class ProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_perfil)

        setup()
    }

    private fun setup() {
        val auth = Firebase.auth
        val db = Firebase.firestore
        val uid = auth.currentUser!!.uid
        val txtEmail = findViewById<TextView>(R.id.emailText)
        val txtNombre = findViewById<TextView>(R.id.nameText)
        val txtScore = findViewById<TextView>(R.id.scoreText)

        db.collection("users").document(uid).get().addOnSuccessListener { doc ->
            val obj = doc.toObject<User>()
            txtEmail.text = obj?.correo ?: ""
            txtNombre.text = obj?.nombre ?: ""
            txtScore.text = obj?.score.toString() + " pts." ?: ""
        }

        backButton.setOnClickListener() {
            val backIntent = Intent(this, MenuActivity::class.java)
            startActivity(backIntent)
        }
    }
}