package com.dscescom.pregunta2

import android.animation.ValueAnimator
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import android.widget.Toast
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_menu.*
import kotlinx.android.synthetic.main.activity_ruleta.*
import kotlin.math.floor

class RouleteActivity : AppCompatActivity() , Animation.AnimationListener {
    private lateinit var roulette: View
    private var degrees: Long = 0L
    private val categories: List<String> = listOf("Movies","Music","Science","Sports","Educational")

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
        val list: MutableList<String> = ArrayList()

        db.collection("preguntas")
            .get()
            .addOnSuccessListener { result ->
                for(document in result) {
                    //Toast.makeText(this,  "${document.data}", Toast.LENGTH_SHORT).show()
                    list.add("${document.data}")
                }
            }.addOnFailureListener{ exception -> Toast.makeText(this,  "${exception}", Toast.LENGTH_SHORT).show() }
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
        Toast.makeText(this, this.categories[index], Toast.LENGTH_SHORT).show()

    }

    override fun onAnimationRepeat(animation: Animation) {
    }
}