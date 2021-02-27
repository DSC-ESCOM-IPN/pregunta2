package com.dscescom.pregunta2

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_contactos.backButton
import kotlinx.android.synthetic.main.activity_matches.*
import java.io.Serializable

class MatchesActivity : AppCompatActivity() {
    private var matches: MutableMap<String, Match> = mutableMapOf<String, Match>()
    private val backgrounds =
        listOf<Int>(R.drawable.bg_btn, R.drawable.bg_btnr4, R.drawable.bg_btn3, R.drawable.bg_btn4)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_matches)
        setup()
    }

    private fun setup() {
        backButton.setOnClickListener() {
            val backIntent = Intent(this, MenuActivity::class.java)
            startActivity(backIntent)
        }

        getMatch("opponent_uid")
        getMatch("challenger_uid")

        findViewById<ImageView>(R.id.refreshButton).setOnClickListener { displayMatches() }
    }

    private fun displayMatches() {
        matchesLayout.removeAllViews()
        val usr = Firebase.auth.currentUser!!
        val db = Firebase.firestore
        var aux: Int = 0
        for ((k, v) in matches) {
            val newButton: Button = Button(this)
            val params = ViewGroup.MarginLayoutParams(MATCH_PARENT, WRAP_CONTENT)
            params.setMargins(0, 0, 0, 10)
            newButton.layoutParams = params
            newButton.tag = k
            newButton.setTextColor(Color.WHITE)
            newButton.setBackgroundResource(backgrounds[aux++ % 4])
            val uid = if (matches[k]!!.isChallenger)
                v.opponent_uid!!
            else
                v.challenger_uid!!
            db.collection("users").document(uid)
                .get().addOnSuccessListener { doc ->
                    val opponent = doc.toObject<User>()
                    newButton.text = if (matches[k]!!.isChallenger)
                        usr.displayName + " vs " + opponent!!.nombre
                    else
                        opponent!!.nombre + " vs " + usr.displayName
                }
            newButton.setOnClickListener {
                val rouletteIntent = Intent(this, RouleteActivity::class.java).apply {
                    putExtra("match", matches[k])
                    putExtra("match_id", k)
                    putExtra("contenders", newButton.text)
                }
                startActivity(rouletteIntent)
            }
            matchesLayout.addView(newButton)
        }
    }

    private fun getMatch(field: String) {
        val uid = Firebase.auth.currentUser!!.uid
        val db = Firebase.firestore
        db.collection("matches").whereEqualTo(field, uid).get().addOnSuccessListener { snap ->
            for (document in snap) {
                val obj = document.toObject<Match>()
                if (field == "challenger_uid")
                    obj.isChallenger = true
                matches[document.id] = obj
            }
            displayMatches()
        }
    }
}

data class Match(
    @field:JvmField
    var isChallenger: Boolean = false,
    val challenger_uid: String? = null,
    val opponent_uid: String? = null,
    val turn_uid: String? = null,
    var challenger_score: Int = 0,
    var opponent_score: Int = 0
) : Serializable