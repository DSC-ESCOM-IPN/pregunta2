package com.dscescom.pregunta2

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.DecelerateInterpolator
import android.view.animation.RotateAnimation
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_ruleta.*
import java.io.Serializable
import kotlin.math.floor

class RouleteActivity : AppCompatActivity(), Animation.AnimationListener {
    private lateinit var roulette: View
    private lateinit var auth: FirebaseAuth
    private lateinit var match: Match
    private lateinit var matchId: String
    private lateinit var player1: String
    private lateinit var player2: String

    private var questions: MutableList<Question> = mutableListOf<Question>()
    private var degrees: Long = 0L
    private val categories: List<String> =
        listOf("Movies", "Educational", "Music", "Science", "Sports")
    private val semaphore =
        listOf<Int>(
            R.drawable.circulo_blanco,
            R.drawable.circulo_amarillo,
            R.drawable.circulo_verde
        )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ruleta)

        roulette = findViewById(R.id.imgRoulete)
        roulette.setOnClickListener { onClickRoulette() }

        //Getting the Object Serializable
        match = intent.extras?.getSerializable("match") as Match
        matchId = intent.getStringExtra("match_id").toString()
        val contenders = intent.getStringExtra("contenders")?.split(" vs ")
        player1 = contenders?.get(0) ?: ""
        player2 = contenders?.get(1) ?: ""

        setup()
    }

    private fun setup() {
        auth = Firebase.auth
        backButton.setOnClickListener() {
            val backIntent = Intent(this, MatchesActivity::class.java)
            startActivity(backIntent)
        }
        setLayout()
        getQuestions()
    }

    private fun setLayout() {
        val btnPlayer1 = findViewById<Button>(R.id.player1Button)
        val btnPlayer2 = findViewById<Button>(R.id.player2Button)

        btnPlayer1.text = player1
        btnPlayer2.text = player2

        setChallengerScore()
        setOpponentScore()
    }

    private fun setOpponentScore() {
        val circles = listOf<ImageView>(
            findViewById(R.id.circulo1a),
            findViewById(R.id.circulo2a),
            findViewById(R.id.circulo3a)
        )
        var aux = match.opponent_score!!
        val score = match.opponent_score!!
        if (score == 0)
            return
        for (i in 0..(score - 1) / 3) {
            if (aux / 3 > 0)
                circles[i].setImageResource(semaphore[2])
            else
                circles[i].setImageResource(semaphore[(aux - 1) % 3])
            aux -= 3
        }
    }

    private fun setChallengerScore() {
        val circles = listOf<ImageView>(
            findViewById(R.id.circulo1),
            findViewById(R.id.circulo2),
            findViewById(R.id.circulo3)
        )
        var aux = match.challenger_score!!
        val score = match.challenger_score!!
        if (score == 0)
            return
        for (i in 0..(score - 1) / 3) {
            if (aux / 3 > 0)
                circles[i].setImageResource(semaphore[2])
            else
                circles[i].setImageResource(semaphore[(aux - 1) % 3])
            aux -= 3
        }
    }

    private fun getQuestions() {
        val db = Firebase.firestore
        db.collection("preguntas")
            .get()
            .addOnSuccessListener { result ->
                for (snapDocument in result) {
                    val document = snapDocument.data.toMap()
                    var newQuestion =
                        Question(document["categoria"].toString(), document["pregunta"].toString())
                    for (answer in document["respuestas"] as List<Map<Any, Any>>)
                        newQuestion.respuestas?.add(
                            Answers(
                                answer["answerText"] as String?,
                                answer["isCorrect"] as Boolean?
                            )
                        )
                    this.questions.add((newQuestion))
                    //Log.d("Firestore_debug: ", document.toString())
                }
            }.addOnFailureListener { exception ->
                Toast.makeText(
                    this,
                    exception.toString(),
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    private fun onClickRoulette() {
        if (auth.currentUser!!.uid == match.turn_uid) {
            val rand: Long = ((0..359).random() + 3600).toLong()
            val rotate = RotateAnimation(
                this.degrees.toFloat(),
                (this.degrees + rand).toFloat(),
                1,
                0.5f,
                1,
                0.5f
            )
            degrees = (degrees + rand) % 360
            rotate.duration = rand
            rotate.fillAfter = true
            rotate.interpolator = DecelerateInterpolator()
            rotate.setAnimationListener(this)
            roulette.startAnimation(rotate)
        } else {
            Toast.makeText(
                this,
                "Solo puedes girar la ruleta cuando es tu turno ;)",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onAnimationStart(animation: Animation) {
        Toast.makeText(this, "Lucky", Toast.LENGTH_SHORT).show()
    }

    override fun onAnimationEnd(animation: Animation) {
        val index: Int = (4.0 - floor(degrees.toDouble() / (72.0))).toInt()
        val filteredQuestions =
            this.questions.filter { question: Question -> question.categoria == this.categories[index] }
        val indexQuestion = (filteredQuestions.indices).random()
        val questionIntent = Intent(this, QuestionActivity::class.java).apply {
            putExtra("question", filteredQuestions[indexQuestion])
            putExtra("match", match)
            putExtra("match_id", matchId)
            putExtra("contenders", player1 + " vs " + player2)
        }
        startActivity(questionIntent)
        //Toast.makeText(this, .pregunta, Toast.LENGTH_SHORT).show()
    }

    override fun onAnimationRepeat(animation: Animation) {
    }
}

public data class Question(
    var categoria: String? = null,
    var pregunta: String? = null,
    var respuestas: MutableList<Answers>? = mutableListOf<Answers>()
) : Serializable

data class Answers(
    var answerText: String? = null,
    @field:JvmField
    var isCorrect: Boolean? = null
) : Serializable