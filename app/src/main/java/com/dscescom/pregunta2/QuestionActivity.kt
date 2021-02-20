package com.dscescom.pregunta2

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.io.Serializable

class QuestionActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    //Map answer and isCorrect
    private lateinit var mapAnswers:MutableMap<String, Boolean>
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
        val mapAnswers:MutableMap<String, Boolean> = mutableMapOf()
        for((answer, isCorrect) in obj!!) {
            mapAnswers[answer!!.toString()] = isCorrect!!.toString().toBoolean()
        }
        return mapAnswers
    }

    private fun findUser(isCorrect:Boolean){
        auth = Firebase.auth
        val uid = auth.currentUser!!.uid
        val db = Firebase.firestore
        if(isCorrect){
            db.collection("scores")
                .whereEqualTo("uid", uid)
                .get()
                .addOnSuccessListener { result ->
                    var score = 1
                    if(result.size()!=0){
                        for (snapDocument in result) {
                            val document = snapDocument.data.toMap()
                            score = document["score"].toString().toInt() + 1
                            val userScore = Score(uid,score)
                            updateScore(userScore, snapDocument.id)
                        }
                    }else{
                        val userScore = Score(uid,score)
                        updateScore(userScore, hashCode().toString())
                    }
                    val rouleteIntent = Intent(this, RouleteActivity::class.java)
                    Toast.makeText(this,  "Correcto :D", Toast.LENGTH_SHORT).show()
                    startActivity(rouleteIntent)
                }.addOnFailureListener{ exception -> Toast.makeText(this,  exception.toString(), Toast.LENGTH_SHORT).show() }
        }else{
            val rouleteIntent = Intent(this, RouleteActivity::class.java)
            Toast.makeText(this,  "Error :c", Toast.LENGTH_SHORT).show()
            startActivity(rouleteIntent)
        }
    }

    private fun updateScore(score: Score, idDocument: String){
        val db = Firebase.firestore
        db.collection("scores").document(idDocument)
            .set(score)
            .addOnSuccessListener { Log.d("TAG", "Registro actualizado con exito") }
            .addOnFailureListener { e -> Log.w("TAG", "Error al escribir", e) }
    }

    private fun handleButtonClick(view: View) {
        with (view as Button) {
            findUser(mapAnswers[text].toString().toBoolean())
        }
    }
}

public data class Score(
    var uid: String? = null,
    var score: Number ? = null
) : Serializable