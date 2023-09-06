package com.warlogqgi.expensetracker.layouts.layouts

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.warlogqgi.expensetracker.R
import de.hdodenhof.circleimageview.CircleImageView
import java.util.concurrent.TimeUnit

class AddNumber : AppCompatActivity() {


    lateinit var phone: TextInputEditText
    lateinit var otp: TextInputEditText
    lateinit var submitBtn: AppCompatButton
    lateinit var requestOtp: TextView
    lateinit var progressBar: ProgressBar



    var auth = FirebaseAuth.getInstance()
    lateinit var storedVerificationId :String
    lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
    var currntUset = FirebaseAuth.getInstance().currentUser


    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_number)
        phone = findViewById(R.id.completeNumber)
        otp = findViewById(R.id.completeOtp)
        submitBtn = findViewById(R.id.verifyOtp)
        requestOtp = findViewById(R.id.profileReqOtp)
        progressBar=findViewById(R.id.progressbar1)



        var callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(p0: PhoneAuthCredential) {
                TODO("Not yet implemented")
            }

            override fun onVerificationFailed(p0: FirebaseException) {
                Log.e("AUTH",p0.toString())
                Toast.makeText(applicationContext, "Failed "+p0.toString(), Toast.LENGTH_SHORT).show()
            }

            override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {

                storedVerificationId = verificationId
                resendToken = token
                progressBar.visibility= View.INVISIBLE


            }


        }



        requestOtp.setOnClickListener {
            progressBar.visibility= View.VISIBLE

           var phoneNumber = "+91"+phone.text.toString()

            val options = PhoneAuthOptions.newBuilder(auth)
                .setPhoneNumber(phoneNumber) // Phone number to verify
                .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                .setActivity(this) // Activity (for callback binding)
                .setCallbacks(callbacks) // OnVerificationStateChangedCallbacks
                .build()
            PhoneAuthProvider.verifyPhoneNumber(options)
        }
        submitBtn.setOnClickListener{
            progressBar.visibility=View.VISIBLE
            var code = otp.text.toString()


            val credential = PhoneAuthProvider.getCredential(storedVerificationId!!, code)
            currntUset?.updatePhoneNumber(credential)?.addOnCompleteListener {
                Toast.makeText(this, " Successfully added Phone ", Toast.LENGTH_SHORT).show()
                progressBar.visibility=View.INVISIBLE
                startActivity(Intent(this,CompleteProfile::class.java))

                finish()

            }?.addOnFailureListener {
                progressBar.visibility=View.INVISIBLE
                Toast.makeText(this, "Failed To link Phone", Toast.LENGTH_SHORT).show()
            }



        }





    }


}