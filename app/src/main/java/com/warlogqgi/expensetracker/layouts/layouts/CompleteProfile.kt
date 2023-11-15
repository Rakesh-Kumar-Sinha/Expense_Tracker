package com.warlogqgi.expensetracker.layouts.layouts

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatButton
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.warlogqgi.expensetracker.R
import com.warlogqgi.expensetracker.layouts.model.UserModel
import de.hdodenhof.circleimageview.CircleImageView
import java.io.IOException
import java.util.Date
import java.util.Locale

class CompleteProfile : AppCompatActivity() {
    lateinit var edtName: TextInputEditText
    lateinit var edtLocation: TextInputEditText
    lateinit var profImgShow: CircleImageView
    lateinit var addPhoto: ImageView
    lateinit var submitBtn: AppCompatButton
    lateinit var progressBar: ProgressBar

    lateinit var inpName: String
    lateinit var inpLocation: String
    lateinit var imageUri: Uri

    var database = Firebase.firestore
    lateinit var storage: FirebaseStorage
    lateinit var auth: FirebaseAuth

    var currentUsrId = FirebaseAuth.getInstance().currentUser!!.uid


    //for location Permission
    private lateinit var permissionLauncher: ActivityResultLauncher<Array<String>>
    private var isLocationPermissionGranted = false

    //finding location
    private lateinit var fusedLocationClient: FusedLocationProviderClient


    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_complete_profile)
        edtName = findViewById(R.id.completeProfName)
        profImgShow = findViewById(R.id.completeProfImage)
        addPhoto = findViewById(R.id.selectPhoto)
        submitBtn = findViewById(R.id.completeProfileSubmit)
        edtLocation = findViewById(R.id.completeProfLocation)
        progressBar = findViewById(R.id.progressBarCProfile)
        database = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance()
        auth = FirebaseAuth.getInstance()





            var intent :Intent
            intent= getIntent()
            Glide.with(this).load(intent.getStringExtra("image")).into(profImgShow)
            edtName.setText(intent.getStringExtra("name"))
            edtLocation.setText(intent.getStringExtra("location"))

        permissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permission ->
                isLocationPermissionGranted =
                    permission[android.Manifest.permission.ACCESS_FINE_LOCATION]
                        ?: isLocationPermissionGranted
            }
        requestPermission()








        addPhoto.setOnClickListener {
            val intent = Intent()
            intent.action = Intent.ACTION_GET_CONTENT
            intent.type = "image/*"
            startActivityForResult(intent, 1)


        }



        submitBtn.setOnClickListener {

            getLocation()
            inpName = edtName.text.toString()
            inpLocation = edtLocation.text.toString()
            if (inpName.isEmpty()) {
                edtName.setError("Input Name")
                edtName.requestFocus()

            } else if (imageUri == null) {
                Toast.makeText(this, "Select a profile photo first", Toast.LENGTH_SHORT).show()
            } else if (inpLocation.isEmpty()) {
                Toast.makeText(this, "Location is empty", Toast.LENGTH_SHORT).show()
            } else {
                uploadData()

            }


        }


    }

    private fun uploadData() {
        progressBar.visibility = View.VISIBLE

        val reference = storage.reference.child("Profile").child(Date().time.toString())
        reference.putFile(imageUri).addOnCompleteListener {
            if (it.isSuccessful) {
                reference.downloadUrl.addOnSuccessListener { task ->
                    uploadInfo(task.toString())
                }
            }
        }
    }

    private fun uploadInfo(imageUrl: String) {
        val user = UserModel(auth.uid.toString(), inpName, imageUrl, inpLocation)
        var docRef = database.collection(currentUsrId).document("userdata").set(user)

        docRef.addOnSuccessListener {
            progressBar.visibility = View.INVISIBLE
            Toast.makeText(this, "Data Uploaded Successfully", Toast.LENGTH_SHORT).show()
            finish()
            startActivity(Intent(this, MainActivity::class.java))


        }.addOnFailureListener {
            Toast.makeText(this, "Some error Occurred", Toast.LENGTH_SHORT).show()
            progressBar.visibility = View.INVISIBLE
        }


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data != null) {
            if (data.data != null) {
                imageUri = data.data!!
                profImgShow.setImageURI(imageUri)

            }
        }
    }

    private fun requestPermission() {
        isLocationPermissionGranted = ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        val permissionRequest: MutableList<String> = ArrayList()
        if (!isLocationPermissionGranted) {
            permissionRequest.add(android.Manifest.permission.ACCESS_FINE_LOCATION)

        }
        if (permissionRequest.isNotEmpty()) {
            permissionLauncher.launch(permissionRequest.toTypedArray())

        }
    }

    private fun getLocation() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        val locationPermission = Manifest.permission.ACCESS_FINE_LOCATION
        val requestCode = 1

        if (ContextCompat.checkSelfPermission(
                this,
                locationPermission
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this, arrayOf(locationPermission), requestCode)
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                val latitute = location.latitude
                val longitute = location.longitude
                getCityName(latitute, longitute)
            }

        }
    }

    private fun getCityName(latitute: Double, longitute: Double) {
        val geocoder = Geocoder(this, Locale.getDefault())
        try {
            val address = geocoder.getFromLocation(latitute, longitute, 1)
            if (address?.isNotEmpty() == true) {
                inpLocation = address[0]?.locality.toString()
                edtLocation.setText(inpLocation)
                Log.e("Location", inpLocation)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }
}