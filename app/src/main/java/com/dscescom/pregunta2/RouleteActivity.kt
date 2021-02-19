package com.dscescom.pregunta2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_menu.*

class RouleteActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ruleta)

        setup()
    }

    private fun setup(){
        val db = Firebase.firestore
        val list: MutableList<String> = ArrayList()

        db.collection("preguntas")
            .get()
            .addOnSuccessListener { result ->
                for(document in result) {
                    Toast.makeText(this,  "${document.data}", Toast.LENGTH_SHORT).show()
                    list.add("${document.data}")
                }
            }.addOnFailureListener{ exception -> Toast.makeText(this,  "${exception}", Toast.LENGTH_SHORT).show() }
    }
}