package com.dscescom.pregunta2

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_contactos.*

class ContactsActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private var semaforo = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contactos)
        auth = Firebase.auth

        setup()
    }

    private fun setup() {
        backButton.setOnClickListener() {
            val backIntent = Intent(this, MenuActivity::class.java)
            startActivity(backIntent)
        }

        val currentUser = auth.currentUser

        addButton.setOnClickListener() {
            if (currentUser != null) {
                addContact(currentUser)
            }
        }

        if (currentUser != null) {
            setContacts(currentUser)
        }
    }

    private fun setContacts(user: FirebaseUser) {
        cleanLayout()
        val db = Firebase.firestore
        val userRef = db.collection("users").document(user.uid)
        userRef.get().addOnSuccessListener { doc ->
            if (doc.get("friends") != null) {
                semaforo = false
                var contactos = doc.get("friends") as List<*>
                val linear = findViewById<LinearLayout>(R.id.linear)
                for (contacto in 0..contactos.size - 1) {
                    val contactRef = db.collection("users").document("" + contactos[contacto])
                    contactRef.get().addOnSuccessListener { docContact ->
                        if (docContact != null) {
                            val correo = TextView(this)
                            correo.setTextColor(Color.WHITE)
                            correo.setPadding(20, 20, 20, 10)
                            correo.setTextSize(25F)
                            correo.setText("" + docContact.get("correo"))
                            correo.setOnClickListener {
                                createMatch(docContact.id)
                            }

                            val nombre = TextView(this)
                            nombre.setTextColor(Color.GRAY)
                            nombre.setPadding(40, 5, 30, 20)
                            nombre.setTextSize(15F)
                            nombre.setText("@" + docContact.get("nombre"))
                            nombre.setOnClickListener {
                                createMatch(docContact.id)
                            }

                            linear.addView(correo)
                            linear.addView(nombre)
                        }
                    }
                }
            } else {
                if (semaforo == true) {
                    setContacts(user)
                }
                val bienvenida = TextView(this)
                bienvenida.setTextColor(Color.WHITE)
                bienvenida.setPadding(30, 30, 30, 20)
                bienvenida.setTextSize(20F)
                bienvenida.setText("Añade amigos para mejorar tu experiencia de juego!")

                linear.addView(bienvenida)
            }
        }
    }

    private fun createMatch(id: String) {
        val newMatch = MatchDoc(auth.uid, id, auth.uid, 0, 0)
        val db = Firebase.firestore
        db.collection("matches").add(newMatch)
            .addOnSuccessListener {
                Toast.makeText(this, "Has creado un nuevo reto!", Toast.LENGTH_SHORT).show()
                val menuIntent = Intent(this, MenuActivity::class.java)
                startActivity(menuIntent)
            }
            .addOnFailureListener {
                Toast.makeText(this, "Ocurrio un error.", Toast.LENGTH_SHORT).show()
            }
    }

    private fun cleanLayout() {
        val linear = findViewById<LinearLayout>(R.id.linear)
        linear.removeAllViews()
    }

    private fun addContact(user: FirebaseUser) {
        val email = findViewById<EditText>(R.id.emailContact)
        if (email.text.isEmpty()) {
            Toast.makeText(this, "Introduce un correo de contacto", Toast.LENGTH_LONG).show()
        } else {
            val db = Firebase.firestore
            val contactRef = db.collection("users")
                .whereEqualTo("correo", "" + email.text).get()

            contactRef.addOnSuccessListener { documents ->
                if (documents.size() == 0) {
                    Toast.makeText(this, "No se encontró ese correo de contacto", Toast.LENGTH_LONG)
                        .show()
                } else {
                    for (document in documents) {
                        val userRef = db.collection("users").document(user.uid)
                        userRef.update("friends", FieldValue.arrayUnion(document.id))

                        db.collection("users").document(document.id)
                            .update("friends", FieldValue.arrayUnion(user.uid))
                        semaforo = true
                        break
                    }
                }
            }.addOnFailureListener { exception ->
                Toast.makeText(this, "Error intentalo más tarde!", Toast.LENGTH_LONG).show()
            }


            Toast.makeText(this, "Contacto añadido con éxito!", Toast.LENGTH_LONG).show()
            setContacts(user)
        }
    }
}