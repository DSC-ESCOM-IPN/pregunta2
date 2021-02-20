package com.dscescom.pregunta2

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.marginTop
import kotlinx.android.synthetic.main.activity_pregunta.*
import kotlinx.android.synthetic.main.activity_ruleta.*
import kotlinx.android.synthetic.main.activity_ruleta.backButton


class QuestionActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pregunta)

        val obj = intent.extras?.get("question") as Object
        //Toast.makeText(this, obj.toString(), Toast.LENGTH_SHORT).show()
        Log.d("Firestore_debug: ", obj.toString().toString())
        setup()
    }

    private fun setup() {
        backButton.setOnClickListener(){
            val backIntent = Intent(this, RouleteActivity::class.java)
            startActivity(backIntent)
        }
    }

    private fun onClickAnswer(v: View){
        Toast.makeText(this, v.id.toString(), Toast.LENGTH_SHORT).show()
    }
}