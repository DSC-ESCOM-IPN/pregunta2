package com.dscescom.pregunta2

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.io.Serializable

class QuestionActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pregunta)

        val obj = intent.extras?.getSerializable("question") as Question

        auth = Firebase.auth
        val uid = auth.currentUser!!.uid
        setup(obj, uid)
    }

    private fun setup(obj: Question, uid: String) {
        val txtQuestion = findViewById<TextView>(R.id.txtQuestion)
        txtQuestion.setText(obj.pregunta.toString()).toString()
        val btnAns1 = findViewById<Button>(R.id.btnAns1)
        btnAns1.setText(obj.respuestas?.get(0)?.answerText.toString()).toString()
        val btnAns2 = findViewById<Button>(R.id.btnAns2)
        btnAns2.setText(obj.respuestas?.get(1)?.answerText.toString()).toString()
        val btnAns3 = findViewById<Button>(R.id.btnAns3)
        btnAns3.setText(obj.respuestas?.get(2)?.answerText.toString()).toString()
        val mapAnswers = mapAnswerIsCorrect(obj.respuestas)
        btnAns1.setOnClickListener {
            findUser(mapAnswers[btnAns1.text].toString().toBoolean(), uid)
        }
        btnAns2.setOnClickListener {
            findUser(mapAnswers[btnAns2.text].toString().toBoolean(), uid)

        }
        btnAns3.setOnClickListener {
            findUser(mapAnswers[btnAns3.text].toString().toBoolean(), uid)
        }
    }

    private fun mapAnswerIsCorrect(obj: MutableList<Answers>?): MutableMap<String, Boolean> {
        val mapAnswers:MutableMap<String, Boolean> = mutableMapOf()
        for((answer, isCorrect) in obj!!) {
            mapAnswers[answer!!.toString()] = isCorrect!!.toString().toBoolean()
        }
        return mapAnswers
    }

    private fun findUser(isCorrect:Boolean, uid:String){
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
}

public data class Score(
    var uid: String? = null,
    var score: Number ? = null
) : Serializable