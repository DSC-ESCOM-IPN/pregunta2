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

class QuestionActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var match: Match
    private lateinit var matchId: String
    private lateinit var player1: String
    private lateinit var player2: String

    //Map answer and isCorrect
    private lateinit var mapAnswers: MutableMap<String, Boolean>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pregunta)
        val obj = intent.extras?.getSerializable("question") as Question
        mapAnswers = mapAnswerIsCorrect(obj.respuestas)
        match = intent.extras?.getSerializable("match") as Match
        matchId = intent.getStringExtra("match_id").toString()
        val contenders = intent.getStringExtra("contenders")?.split(" vs ")
        player1 = contenders?.get(0) ?: ""
        player2 = contenders?.get(1) ?: ""

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

    private fun validateQuestion(isCorrect: Boolean) {
        auth = Firebase.auth
        val uid = auth.currentUser!!.uid
        if (isCorrect) {
            updateUserScore(uid)
            updateMatchScore()
        } else {
            switchTurn()
            val menuIntent = Intent(this, MenuActivity::class.java)
            startActivity(menuIntent)
        }

    }

    private fun updateMatchScore() {
        var field = ""
        if (match.isChallenger) {
            field = "challenger_score"
            match.challenger_score += 1
        } else {
            field = "opponent_score"
            match.opponent_score += 1
        }
        val db = Firebase.firestore
        db.collection("matches").document(matchId)
            .update(field, FieldValue.increment(1))
            .addOnSuccessListener {
                Toast.makeText(this, "Correcto! :)", Toast.LENGTH_SHORT).show()
                val rouletteIntent = Intent(this, RouleteActivity::class.java).apply {
                    putExtra("match", match)
                    putExtra("match_id", matchId)
                    putExtra("contenders", player1 + " vs " + player2)
                }
                startActivity(rouletteIntent)
            }
            .addOnFailureListener { e ->
                Toast.makeText(
                    this,
                    "Oh oh, ocurrió un error: $e",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    private fun updateUserScore(uid: String) {
        val db = Firebase.firestore
        db.collection("users").document(uid)
            .update("score", FieldValue.increment(1))
            .addOnSuccessListener { Log.d("TAG", "Registrado correctamente") }
            .addOnFailureListener { e ->
                Toast.makeText(
                    this,
                    "Oh oh, ocurrió un error: $e",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    private fun switchTurn() {
        val turn = if (match.isChallenger)
            match.opponent_uid
        else
            match.challenger_uid
        val db = Firebase.firestore
        db.collection("matches").document(matchId)
            .update("turn_uid", turn)
            .addOnSuccessListener {
                Toast.makeText(
                    this,
                    "Es el turno del contrincante",
                    Toast.LENGTH_SHORT
                ).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(
                    this,
                    "Oh oh, ocurrió un error: $e",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    private fun handleButtonClick(view: View) {
        with(view as Button) {
            validateQuestion(mapAnswers[text].toString().toBoolean())
        }
    }
}