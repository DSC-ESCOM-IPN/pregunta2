package com.dscescom.pregunta2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.DecelerateInterpolator
import android.view.animation.RotateAnimation
import android.widget.Toast
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_ruleta.*
import java.io.Serializable
import java.util.*
import kotlin.math.floor

class RouleteActivity : AppCompatActivity() , Animation.AnimationListener {
    private lateinit var roulette: View
    private var questions: MutableList<Question> = mutableListOf<Question>()
    private var degrees: Long = 0L
    private val categories: List<String> = listOf("Movies","Educational","Music","Science","Sports")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ruleta)

        roulette = findViewById(R.id.imgRoulete)
        roulette.setOnClickListener{ onClickRoulette() }
        setup()
    }

    private fun setup(){
        backButton.setOnClickListener(){
            val backIntent = Intent(this, MenuActivity::class.java)
            startActivity(backIntent)
        }

        val db = Firebase.firestore

        db.collection("preguntas")
            .get()
            .addOnSuccessListener { result ->
                for (snapDocument in result) {
                    val document = snapDocument.data.toMap()
                    var newQuestion = Question(document["categoria"].toString(),document["pregunta"].toString())
                    for(answer in document["respuestas"] as List<Map<Any,Any>>)
                        newQuestion.respuestas?.add(Answers(answer["answerText"] as String?,
                            answer["isCorrect"] as Boolean?
                        ))
                    this.questions.add((newQuestion))
                    //Log.d("Firestore_debug: ", document.toString())
                }
            }.addOnFailureListener{ exception -> Toast.makeText(this,  exception.toString(), Toast.LENGTH_SHORT).show() }
    }

    private fun onClickRoulette(){
        val rand: Long = ((0..359).random() + 3600).toLong()
        val rotate = RotateAnimation(this.degrees.toFloat(),(this.degrees + rand).toFloat(),1,0.5f,1,0.5f)
        degrees = (degrees + rand) % 360
        rotate.duration = rand
        rotate.fillAfter = true
        rotate.interpolator = DecelerateInterpolator()
        rotate.setAnimationListener(this)
        roulette.startAnimation(rotate)
    }

    override fun onAnimationStart(animation: Animation) {
        Toast.makeText(this, "Lucky", Toast.LENGTH_SHORT).show()
    }

    override fun onAnimationEnd(animation: Animation) {
        val index: Int = (4.0 - floor(degrees.toDouble()/(72.0))).toInt()
        val filteredQuestions = this.questions.filter { question: Question ->  question.categoria == this.categories[index]}
        val indexQuestion = (filteredQuestions.indices).random()
            val questionIntent = Intent(this, QuestionActivity::class.java).apply {
            putExtra("question", filteredQuestions[indexQuestion])
        }
        Log.d("Firestore_debug: ", filteredQuestions[indexQuestion].toString().toString())
        startActivity(questionIntent)
        //Toast.makeText(this, .pregunta, Toast.LENGTH_SHORT).show()
    }

    override fun onAnimationRepeat(animation: Animation) {
    }
}

public data class Question(
    var categoria: String? = null,
    var pregunta: String ? = null,
    var respuestas: MutableList<Answers>? = mutableListOf<Answers>()
) : Serializable

data class Answers(
    var answerText: String? = null,
    @field:JvmField
    var isCorrect: Boolean? = null
) : Serializable