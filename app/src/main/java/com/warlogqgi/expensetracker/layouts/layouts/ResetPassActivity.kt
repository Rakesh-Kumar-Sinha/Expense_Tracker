package com.warlogqgi.expensetracker.layouts.layouts

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.AppCompatButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.warlogqgi.expensetracker.R

class ResetPassActivity : AppCompatActivity() {

    private  lateinit var edtmail: TextInputEditText
  private  lateinit var btnReset: AppCompatButton
  private lateinit var btnLogin :TextView
    private var firebasseIns = FirebaseAuth.getInstance()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_pass)
        edtmail = findViewById(R.id.passResetEmail)
        btnReset = findViewById(R.id.btnResetPass)
        btnLogin=findViewById(R.id.resetLogin);

        //setting day night mode as default
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)


        btnLogin.setOnClickListener{
            startActivity(Intent(this,MainActivity::class.java))
            finish()
        }

        btnReset.setOnClickListener {
            if (edtmail.text.toString().isEmpty()) {
                Toast.makeText(this, "Enter Email to continue", Toast.LENGTH_SHORT).show()
                edtmail.error = "Enter mail"
                edtmail.requestFocus()
            } else {

                firebasseIns.sendPasswordResetEmail(edtmail.text.toString()).addOnSuccessListener {

                    Toast.makeText(this, "Password Reset Mail sent", Toast.LENGTH_SHORT).show()
                    finish()
                    startActivity(Intent(this, MainActivity::class.java))



                }.addOnFailureListener {
                    Toast.makeText(this, "Some error Occurred", Toast.LENGTH_SHORT).show()
                }


            }


        }


    }
}