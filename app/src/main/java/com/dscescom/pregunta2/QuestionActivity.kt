package com.dscescom.pregunta2

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.io.Serializable

class QuestionActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    //Map answer and isCorrect
    private lateinit var mapAnswers: MutableMap<String, Boolean>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pregunta)
        val obj = intent.extras?.getSerializable("question") as Question
        mapAnswers = mapAnswerIsCorrect(obj.respuestas)
        setup(obj)
    }

    private fun setup(obj: Question) {
        val txtQuestion = findViewById<TextView>(R.id.txtQuestion)
        val btnAns1 = findViewById<Button>(R.id.btnAns1)
        val btnAns2 = findViewById<Button>(R.id.btnAns2)
        val btnAns3 = findViewById<Button>(R.id.btnAns3)
        var iterator = 0

        txtQuestion.setText(obj.pregunta.toString()).toString()
        //setText in buttons
        listOf(btnAns1, btnAns2, btnAns3).forEach {
            it.text = obj.respuestas?.get(iterator)?.answerText.toString()
            iterator += 1
        }
        //add EventListener
        listOf(btnAns1, btnAns2, btnAns3).forEach {
            it.setOnClickListener(::handleButtonClick)
        }
    }

    private fun mapAnswerIsCorrect(obj: MutableList<Answers>?): MutableMap<String, Boolean> {
        val mapAnswers: MutableMap<String, Boolean> = mutableMapOf()
        for ((answer, isCorrect) in obj!!) {
            mapAnswers[answer!!.toString()] = isCorrect!!.toString().toBoolean()
        }
        return mapAnswers
    }

    private fun findUser(isCorrect: Boolean) {
        auth = Firebase.auth
        val uid = auth.currentUser!!.uid
        if (isCorrect) {
            updateScore(uid)
            Toast.makeText(this, "Correcto! :)", Toast.LENGTH_SHORT).show()
        } else
            Toast.makeText(this, "Error :c", Toast.LENGTH_SHORT).show()
        val rouletteIntent = Intent(this, RouleteActivity::class.java)
        startActivity(rouletteIntent)
    }

    private fun updateScore(uid: String) {
        val db = Firebase.firestore
        db.collection("users").document(uid)
            .update("score", FieldValue.increment(1))
            .addOnSuccessListener { Log.d("TAG", "Registrado correctamente") }
            .addOnFailureListener { e -> Log.w("TAG", "Ocurrio un error", e) }
    }

    private fun handleButtonClick(view: View) {
        with(view as Button) {
            findUser(mapAnswers[text].toString().toBoolean())
        }
    }
}

public data class Score(
    var score: Number? = null
) : Serializable