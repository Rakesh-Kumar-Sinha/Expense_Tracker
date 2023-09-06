package com.warlogqgi.expensetracker.layouts.layouts

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.warlogqgi.expensetracker.R

class MainActivity : AppCompatActivity() {
    lateinit var buttnSignIn: Button
    lateinit var edtEmail: TextInputEditText
    lateinit var edtPass: TextInputEditText
    lateinit var btnSignUp: TextView
    lateinit var btnReset: TextView
    var firebaseAuth = FirebaseAuth.getInstance()
    var firebaseUser = firebaseAuth.currentUser


    lateinit var signInBtn: Button

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //setting day night mode as default
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        buttnSignIn = findViewById(R.id.btnSignIn)
        btnSignUp = findViewById(R.id.tvSignUp)
        edtEmail = findViewById(R.id.EdtSignInEmail)
        edtPass = findViewById(R.id.EdtSignInPass)
        btnReset = findViewById(R.id.resetPass)


        if (firebaseUser!=null) {
            firebaseAuth.currentUser?.let {
                if (it.isEmailVerified) {
                    finish()
                    startActivity(Intent(this,Home_Activity::class.java))
                }

            }
        }

        btnReset.setOnClickListener {
            finish()
            startActivity(Intent(this, ResetPassActivity::class.java))
        }



        btnSignUp.setOnClickListener {
            var inext = Intent(this, SignUpActivity::class.java)
            finish()
            startActivity(inext)
        }


        buttnSignIn.setOnClickListener {

            if (edtEmail.text.toString().isEmpty()) {
                Toast.makeText(this, "Pls Enter Email", Toast.LENGTH_SHORT).show()
                edtEmail.setError("Enter Email")
                edtEmail.requestFocus()
            } else if (edtPass.text.toString().isEmpty()) {
                Toast.makeText(this, "Pls Enter Password", Toast.LENGTH_SHORT).show()
                edtPass.setError("Enter Password")
                edtPass.requestFocus()

            } else {

                firebaseAuth.signInWithEmailAndPassword(
                    edtEmail.text.toString(), edtPass.text.toString()
                ).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            firebaseAuth.currentUser?.let {
                                if (it.isEmailVerified) {
                                    Toast.makeText(this, "Successfully Sign In", Toast.LENGTH_SHORT)
                                        .show()
                                    var inext = Intent(this, Home_Activity::class.java)
                                    finish()
                                    startActivity(inext)


                                } else {
                                    Toast.makeText(
                                        this, "Error Verify Your Email First", Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }

                        } else {
                            Toast.makeText(this, "Some error occurred :( ", Toast.LENGTH_SHORT)
                                .show()
                        }


                    }
            }

        }


    }
}