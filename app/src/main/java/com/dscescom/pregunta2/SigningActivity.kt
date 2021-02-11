package com.dscescom.pregunta2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_login.*

class SigningActivity : AppCompatActivity() {

    //declare auth vars
    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()


        googleSignInClient = GoogleSignIn.getClient(this, gso)
        auth = Firebase.auth
        setup()
    }

    private fun setup(){
        backButton2.setOnClickListener(){
            val backIntent = Intent(this, MainActivity::class.java)
            startActivity(backIntent)
        }

        logInButton3.setOnClickListener {
            validateData(emailEditText2.text.toString(), passwordEditText2.text.toString())
        }

        gLoginButton.setOnClickListener {
            val signInIntent = googleSignInClient.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }
    }

    private fun validateData(email:String, password:String){
        if (email.isNotEmpty() && password.isNotEmpty()){
            login(email, password)
        }else{
            Toast.makeText(this, "Alguno de los campos esta vacio.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                Toast.makeText(this, "Ocurrio un error intentalo mas tarde.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    val menuIntent = Intent(this, MenuActivity::class.java).apply {
                        putExtra("name", user?.email ?: "")
                    }
                    startActivity(menuIntent)
                } else {
                    Toast.makeText(this, "Ocurrio un error intentalo mas tarde.", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun login(email:String, password:String){
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                val user = auth.currentUser
                val menuIntent = Intent(this, MenuActivity::class.java).apply {
                    putExtra("name", user?.email ?: "")
                }
                startActivity(menuIntent)
            } else {
                Toast.makeText(baseContext, "Credenciales incorrectas.", Toast.LENGTH_SHORT).show()
            }
            if (!task.isSuccessful) {
                Toast.makeText(this, "Ocurrio un error intentalo mas tarde.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    companion object {
        private const val RC_SIGN_IN = 9001
    }

}