package com.dscescom.pregunta2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_contactos.*

class ContactsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contactos)

        setup()
    }

    private fun setup() {
         backButton.setOnClickListener(){
             val backIntent = Intent(this, MenuActivity::class.java)
             startActivity(backIntent)
         }
    }
}