package com.warlogqgi.expensetracker.layouts.layouts

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import android.media.Image
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatButton
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.warlogqgi.expensetracker.R
import de.hdodenhof.circleimageview.CircleImageView

class ProfileActivity : AppCompatActivity() {

    lateinit var profImage: CircleImageView
    lateinit var backBtn: ImageView
    lateinit var name: TextView
    lateinit var phone: TextView
    lateinit var location: TextView
    lateinit var email: TextView
    lateinit var edtProfBtn: AppCompatButton
    var usrName: String = " "
    var usrLocation: String = " "
    var usrEmail: String = " "
    var usrNumber: String = " "
    var imageUrl: String = " "


    var currentUserId = FirebaseAuth.getInstance().currentUser!!.uid
    var currentUser = FirebaseAuth.getInstance().currentUser
    var db = Firebase.firestore


    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        // Finding Ids
        profImage = findViewById(R.id.viewProfImage)
        backBtn = findViewById(R.id.backBtnProfile)
        name = findViewById(R.id.viewProfName)
        phone = findViewById(R.id.viewProfPhone)
        location = findViewById(R.id.viewProfLocation)
        email = findViewById(R.id.viewProfEmail)
        edtProfBtn= findViewById(R.id.edtProfBtn)

        getUsrData()

        backBtn.setOnClickListener{
            onBackPressed()
        }

        edtProfBtn.setOnClickListener{
            val intent = Intent(this,CompleteProfile::class.java)
            intent.putExtra("name",usrName)
            intent.putExtra("location",usrLocation)
            intent.putExtra("image",imageUrl)
            startActivity(intent)
        }

    }

    private fun getUsrData() {
        val ref1 = db.collection(currentUserId).document("userdata").get()
        ref1.addOnSuccessListener {
            if (it.data != null) {
                usrName = it.data!!.get("name").toString()
                usrLocation = it.data!!.get("location").toString()
                imageUrl = it.data!!.get("imageUrl").toString()

                usrEmail = currentUser?.email.toString()
                usrNumber = currentUser?.phoneNumber.toString()

                name.setText(usrName)
                phone.setText(usrNumber)
                location.setText(usrLocation)
                email.setText(usrEmail)
                Glide.with(this).load(imageUrl).into(profImage)

            }
        }


    }
}