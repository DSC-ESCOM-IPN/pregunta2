package com.dscescom.pregunta2

import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.marginTop


class QuestionActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pregunta)

        setup()
    }

    private fun setup() {

    }

    private fun onClickAnswer(v: View){
        Toast.makeText(this, v.id.toString(), Toast.LENGTH_SHORT).show()
    }
}