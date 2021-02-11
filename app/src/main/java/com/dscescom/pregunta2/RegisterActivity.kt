package com.dscescom.pregunta2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        auth = Firebase.auth
        setup()
    }

    private fun setup(){
        backButton.setOnClickListener {
            val backIntent = Intent(this, MainActivity::class.java)
            startActivity(backIntent)
        }

        signInButton.setOnClickListener {
            val registerIntent = Intent(this, SigningActivity::class.java)
            startActivity(registerIntent)
        }

        logInButton.setOnClickListener {
            validateData(emailEditText.text.toString(), passwordEditText.text.toString(), passwordCEditText.text.toString())
        }


    }

    //validate data
    private fun validateData(email:String, password:String, passwordConfirm:String){
        if (email.isNotEmpty() && password.isNotEmpty() && passwordConfirm.isNotEmpty()){
            if (password == passwordConfirm){
                createAccount(email, password)
            }else{
                Toast.makeText(this, "Las contraseñas no coinciden.", Toast.LENGTH_SHORT).show()
            }
        }else{
            Toast.makeText(this, "Alguno de los campos esta vacio.", Toast.LENGTH_SHORT).show()
        }
    }

    //Create account
    private fun createAccount(email:String, password:String){
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this) {task ->
            if (task.isSuccessful) {
                val user = auth.currentUser
                val menuIntent = Intent(this, MenuActivity::class.java).apply {
                    putExtra("name", user?.email ?: "")
                }
                startActivity(menuIntent)
            }else{
                Toast.makeText(this, "Ocurrio un error intentalo mas tarde.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}