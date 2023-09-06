package com.warlogqgi.expensetracker.layouts.layouts

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.AppCompatButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.warlogqgi.expensetracker.R

class SignUpActivity : AppCompatActivity() {

    lateinit var edtMail: TextInputEditText
    lateinit var edtPass: TextInputEditText
    lateinit var edtPass2: TextInputEditText

    lateinit var btnSignUp: AppCompatButton
    lateinit var btnSignIn: TextView
    lateinit var mail: String
    lateinit var pass: String
    lateinit var name: String
    lateinit var passCnf: String
    val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

    var db = FirebaseFirestore.getInstance()


    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        //setting day night mode as default
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        init()

        btnSignIn.setOnClickListener {
            finish()
            startActivity(Intent(this, MainActivity::class.java))

        }

        btnSignUp.setOnClickListener {
            mail = edtMail.text.toString()
            pass = edtPass.text.toString()
            passCnf = edtPass2.text.toString()

            if (mail.isEmpty()) {
                edtMail.setError("Enter Mail ")
                edtMail.requestFocus()
            } else if (pass.isEmpty()) {
                edtPass.setError(("Enter Password"))
                edtPass.requestFocus()
            } else if (passCnf.isEmpty()) {
                edtPass2.setError("Confirm Password")
                edtPass2.requestFocus()
            }  else {
                if (!pass.equals(passCnf)) {
                    Toast.makeText(this, "Password Does not matches", Toast.LENGTH_SHORT).show()
                    edtPass.setError("Password not matched")
                    edtPass2.setError("Password not matched")
                    edtPass.requestFocus()
                    edtPass2.requestFocus()
                } else {
                    createAccount(mail, pass)
                }


            }


        }


    }

    private fun createAccount(mail: String, pass: String) {
        firebaseAuth.createUserWithEmailAndPassword(mail, pass).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(this, "Successfully Created Account ", Toast.LENGTH_SHORT).show()
                sendVerificationCode()
                finish()
                startActivity(Intent(this, AddNumber::class.java))



            } else {
                Toast.makeText(this, "Some Error Occurred Pls Try again", Toast.LENGTH_SHORT).show()
            }

        }


    }

    private fun sendVerificationCode() {
        val firebaseUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser

        firebaseUser?.let {
            it.sendEmailVerification().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(
                        this,
                        "Verification link sent verify before sign in",
                        Toast.LENGTH_SHORT
                    ).show()
                    finish();
                } else {
                    Toast.makeText(
                        this,
                        "Some error occurred in sending verification link",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }


    private fun init() {
        edtMail = findViewById(R.id.EdtSignUpEmail)
        edtPass = findViewById(R.id.EdtSignUpPass)

        btnSignUp = findViewById(R.id.btnSignUp)
        btnSignIn = findViewById(R.id.tvSignIn)
        edtPass2 = findViewById(R.id.EdtSignUpPassConfirm)

    }
}