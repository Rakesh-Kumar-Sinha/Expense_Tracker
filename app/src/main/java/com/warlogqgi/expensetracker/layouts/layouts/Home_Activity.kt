package com.warlogqgi.expensetracker.layouts.layouts

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.RadioGroup
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts

import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.PopupMenu
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide

import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

import com.warlogqgi.expensetracker.R
import com.warlogqgi.expensetracker.layouts.model.DataModel
import de.hdodenhof.circleimageview.CircleImageView
import java.util.Calendar


class Home_Activity : AppCompatActivity() {

    // All views

    lateinit var textVTotalAmount: TextView
    lateinit var goalBalance: TextView
    lateinit var nameUser: TextView
    lateinit var viewAllExpen: FloatingActionButton
    lateinit var profileImg: CircleImageView
    lateinit var pieChart: PieChart
    lateinit var progress: ProgressBar
    lateinit var relativeLayout: RelativeLayout
    lateinit var currentBalance: TextView


    //for manipulating data
    var expFood: Int = 0
    var expTransport: Int = 0
    var expEntertainment: Int = 0
    var expOther: Int = 0
    var expTotalAmount: Int = 0
    var goalAmount: Int = 0
    var first = true
    var currentBal = 0

    //
    var helper: ArrayList<DataModel> = ArrayList()

    //date
    var calendar = Calendar.getInstance()
    var YEAR = calendar.get(Calendar.YEAR)
    var MONTH = calendar.get(Calendar.MONTH) + 1
    var DAY = calendar.get(Calendar.DAY_OF_MONTH)
    var HOUR = calendar.get(Calendar.HOUR)
    var MINUTES = calendar.get(Calendar.MINUTE)
    var AM = calendar.get(Calendar.AM_PM)
    lateinit var AMPM: String
    var currentDate = "$DAY/$MONTH/$YEAR"
    lateinit var currentTime: String
    var curentMonthCheck = "/$MONTH/"


    //firebase
    var currentUserId = FirebaseAuth.getInstance().currentUser!!.uid
    var currentUser = FirebaseAuth.getInstance()
    var db = Firebase.firestore

    val notificationId = 1
    private lateinit var permissionLauncher: ActivityResultLauncher<Array<String>>
    private var isNotificationPermissionGranted = false

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        viewAllExpen = findViewById(R.id.viewAllExpend)
        db = FirebaseFirestore.getInstance()
        textVTotalAmount = findViewById(R.id.tvTotalExp)
        profileImg = findViewById(R.id.profileImg)
        nameUser = findViewById(R.id.nameUser)
        pieChart = findViewById(R.id.piChart)
        progress = findViewById(R.id.progressbar)
        goalBalance = findViewById(R.id.tvTotalBalance)
        relativeLayout = findViewById(R.id.relativeLayoutMain)
        currentBalance = findViewById(R.id.tvCurrentBalance)
        var fab = findViewById<FloatingActionButton>(R.id.btnAdd)


        //setting day night mode as default
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)


//permission for send notification
        permissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permission ->
                isNotificationPermissionGranted=
                    permission[android.Manifest.permission.POST_NOTIFICATIONS]
                        ?: isNotificationPermissionGranted
            }
        requestPermission()




        if (isInternetAvailable()) {
            //getting name and goal amount and setting the views
            getNG()
            //getting data and setting views
            getData()
        } else {
            var dialog = AlertDialog.Builder(this)
            dialog.setTitle("Error")
            dialog.setIcon(R.drawable.baseline_network_cell_24)
            dialog.setMessage("Internet is not Available Pls switch on Internet and reload app")
            dialog.setPositiveButton(
                "Ok",
                DialogInterface.OnClickListener { dialogInterface: DialogInterface, i: Int ->
                    finish()
                })
            dialog.show()
            Toast.makeText(this, "Please Switch On Data Connection", Toast.LENGTH_SHORT).show()
        }




        //for menu
        profileImg.setOnClickListener { v ->
            val popupMenu: PopupMenu = PopupMenu(this, profileImg)
            popupMenu.menuInflater.inflate(R.menu.home_menu, popupMenu.menu)
            popupMenu.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.LogOut -> {
                        currentUser.signOut()
                        startActivity(Intent(this, MainActivity::class.java))
                    }
                    R.id.seeProfile ->{
                        startActivity(Intent(this,ProfileActivity::class.java))

                    }
                    R.id.About ->{
                        startActivity(Intent(this, DevelopersDetails::class.java))
                    }


                    R.id.SetGoalMoney -> {
                        val dialog = Dialog(this)
                        dialog.setCancelable(false)
                        dialog.setContentView(R.layout.dialog_set_goal_money)
                        val edtGoalAmount = dialog.findViewById<EditText>(R.id.edtGoalAmount)
                        val btnCancel = dialog.findViewById<AppCompatButton>(R.id.dialogCancelBtn)
                        val btnSetGoal = dialog.findViewById<AppCompatButton>(R.id.dialogSetGoalBtn)
                        btnCancel.setOnClickListener {
                            dialog.hide()
                        }
                        btnSetGoal.setOnClickListener {
                            if (edtGoalAmount.text.isEmpty()) {
                                edtGoalAmount.setError("Enter Amount")
                                edtGoalAmount.requestFocus()
                            }

                            var goal = edtGoalAmount.text.toString()

                            var upload = hashMapOf(
                                "goal" to goal
                            )

                            db.collection(currentUserId).document("goal").set(upload)
                                .addOnSuccessListener {
                                    Toast.makeText(
                                        this,
                                        "Goal Set Successfully",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    getNG()
                                    dialog.hide()
                                }
                                .addOnFailureListener {
                                    Toast.makeText(
                                        this,
                                        "Error in setting Goal Try Again",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    dialog.hide()
                                }


                        }


                        dialog.show()


                    }
                }
                true
            })
            popupMenu.show()


        }


        //detail expense
        viewAllExpen.setOnClickListener {

            var inext = Intent(this, ExpenseDetails::class.java)
            startActivity(inext)

        }




        fab.setOnClickListener {
            val dialog = Dialog(this)

            dialog.setCancelable(true)
            dialog.setContentView(R.layout.dialog_add_expense)


            var edtAmount = dialog.findViewById<EditText>(R.id.edtAmount)
            var pickdate = dialog.findViewById<TextInputEditText>(R.id.edtDate)
            var edtDescc = dialog.findViewById<EditText>(R.id.edtDesc)
            var addExpBtn = dialog.findViewById<AppCompatButton>(R.id.btnAddExp)
            var radioGroup = dialog.findViewById<RadioGroup>(R.id.radioGroupCategory)
            var cancelBtn = dialog.findViewById<AppCompatButton>(R.id.addExpBtnCancel)
            val edtTime = dialog.findViewById<TextInputEditText>(R.id.edtTime)

            cancelBtn.setOnClickListener {
                dialog.dismiss()
            }
            YEAR = calendar.get(Calendar.YEAR)
            MONTH = calendar.get(Calendar.MONTH) + 1
            DAY = calendar.get(Calendar.DAY_OF_MONTH)
            HOUR = calendar.get(Calendar.HOUR)
            MINUTES = calendar.get(Calendar.MINUTE)
            AM = calendar.get(Calendar.AM_PM)
            if (AM == 1) {
                AMPM = "PM"
            } else {
                AMPM = "AM"
            }
            currentTime = "$HOUR : $MINUTES $AMPM "

            edtTime.setText(currentTime)
            pickdate.setText(currentDate)
            var category: String = "Other"

            radioGroup.setOnCheckedChangeListener(
                RadioGroup.OnCheckedChangeListener { group, checkedId ->
                    if (checkedId == R.id.radioFood) {
                        category = "Food"
                    } else if (checkedId == R.id.radioEntertainment) {
                        category = "Entertainment"
                    } else if (checkedId == R.id.radioTransport) {
                        category = "Transport"
                    } else if (checkedId == R.id.radioOther) {
                        category = "Other"
                    } else {
                        category = "Other"
                    }
                })

            addExpBtn.setOnClickListener {


                var desc = edtDescc.text.toString()

                if (edtAmount.text.toString().isEmpty()) {
                    edtAmount.setError("Enter The amount")
                    edtAmount.requestFocus()
                } else {
                    var expAmount = edtAmount.text.toString().toInt()

                    if (pickdate.text != null) {
                        currentDate = pickdate.text.toString()
                    } else {
                        pickdate.requestFocus()
                        pickdate.setError("Enter date")
                    }
                    if (edtTime.text != null) {
                        currentTime = edtTime.text.toString()
                    } else {
                        pickdate.requestFocus()
                        pickdate.setError("Enter time")
                    }

                    uploadData(expAmount, desc, category, currentDate, currentTime)
                    getData()

                    dialog.hide()

                }
            }

            dialog.show()

        }


    }


    private fun uploadData(
        expAmount: Int,
        desc: String,
        category: String,
        date: String,
        time: String
    ) {
        var data = hashMapOf(
            "id" to "",
            "category" to category,
            "desc" to desc,
            "expamount" to expAmount,
            "date" to date,
            "time" to time

        )
        var docRef = db.collection(currentUserId).document("expense").collection("data").add(data)

        docRef.addOnSuccessListener {
            val snackbar = Snackbar.make(
                relativeLayout, // Replace with your layout's root view
                "Data uploaded Successfully", // Snackbar text
                Snackbar.LENGTH_LONG // Duration
            )

            // Customize the Snackbar
            snackbar.setAction("Show") {
                startActivity(Intent(this, ExpenseDetails::class.java))
            }

            snackbar.show()

        }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failure", Toast.LENGTH_SHORT).show()
            }


    }


    @SuppressLint("SetTextI18n", "ResourceAsColor")
    private fun getData() {
        helper.clear()
        val ref = db.collection(currentUserId).document("expense").collection("data").get()
        ref.addOnSuccessListener { result ->
            for (elem in result) {
                if (elem.data.get("date").toString().contains(curentMonthCheck)) {
                    helper.add(
                        DataModel(
                            elem.id.toString(),
                            elem.data.get("category").toString(),
                            elem.data.get("desc").toString(),
                            elem.data.getValue("expamount").toString(),
                            elem.data.get("date").toString()
                        )
                    )

                }
            }
            expFood = 0
            expEntertainment = 0
            expTransport = 0
            expOther = 0

            for (i in 0 until helper.size) {
                if (helper.get(i).category.equals("Food")) {
                    expFood += (helper[i].expamount?.toInt() ?: 0)
                } else if (helper[i].category.equals("Entertainment")) {
                    expEntertainment += (helper[i].expamount?.toInt() ?: 0)

                } else if (helper[i].category.equals("Transport")) {
                    expTransport += (helper.get(i).expamount?.toInt() ?: 0)
                } else if (helper[i].category.equals("Other")) {
                    expOther += (helper[i].expamount?.toInt() ?: 0)
                } else {
                    Toast.makeText(this, "Empty data", Toast.LENGTH_SHORT).show()
                }
            }


            textVTotalAmount.text =
                (expFood + expEntertainment + expOther + expTransport).toString()

            expTotalAmount = (expFood + expEntertainment + expOther + expTransport)

            currentBal = goalAmount-expTotalAmount
            currentBalance.setText(currentBal.toString())
            if(currentBal in 1..499){
                currentBalance.setTextColor(R.color.yellow)
            } else if (currentBal<0){
                currentBalance.setTextColor(Color.RED)
            }else{
                currentBalance.setTextColor(Color.BLACK)
            }


            val entries = ArrayList<PieEntry>()
            entries.add(PieEntry(expFood?.toFloat() ?: 0f, "Food"))
            entries.add(PieEntry(expEntertainment?.toFloat() ?: 0f, "Entertainment"))
            entries.add(PieEntry(expTransport?.toFloat() ?: 0f, "Transport"))
            entries.add(PieEntry(expOther?.toFloat() ?: 0f, "Others"))
            val dataSet = PieDataSet(entries, " ")
            dataSet.colors = ColorTemplate.MATERIAL_COLORS.toList() // Use predefined colors
            dataSet.valueTextSize = 18f
            val data = PieData(dataSet)
            pieChart.setEntryLabelColor(Color.WHITE)
            pieChart.setHoleColor(R.color.acent_1)
            pieChart.data = data
            pieChart.description.isEnabled = false
            pieChart.centerText = "Expense Chart"
            pieChart.setCenterTextSize(18f)
            pieChart.setEntryLabelTextSize(22f)
            pieChart.legend.textSize = 11f
            pieChart.legend.textColor = Color.WHITE
            pieChart.invalidate()


        }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("ResourceAsColor")
    fun getNG() {

        var ref2 = db.collection(currentUserId).document("goal").get()
        ref2.addOnSuccessListener {

            if (it.data != null) {
                goalAmount = it.data?.get("goal").toString().toInt()
                goalBalance.text = goalAmount.toString()

            } else {
                Log.e("error", "Error in goal Dara")

            }

            if (goalAmount < expTotalAmount) {
                goalBalance.setTextColor(Color.RED)
                sendNotification(this);
            } else {
                goalBalance.setTextColor(Color.WHITE)

            }
            currentBal = goalAmount - expTotalAmount
            currentBalance.setText(currentBal.toString())
            if (currentBal < 500) {
                currentBalance.setTextColor(Color.YELLOW)
            } else if (currentBal < 1) {
                currentBalance.setTextColor(Color.RED)
            } else {
                currentBalance.setTextColor(Color.BLACK)
            }


        }
        val ref3 = db.collection(currentUserId).document("userdata").get()
        ref3.addOnSuccessListener {
            if (it.data != null) {
               var imageUrl = it.data!!.get("imageUrl").toString()
                var  name = it.data!!.get("name").toString()
                nameUser.setText(name)
                Glide.with(this).load(imageUrl).into(profileImg)
            }


        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onResume() {
        super.onResume()
        if (first) {
            first = false
            return
        }
        getData()
        getNG()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun sendNotification(context: Context) {
        val channelId = "channel_id"

        val channel =
            NotificationChannel(channelId, "Expense", NotificationManager.IMPORTANCE_DEFAULT)
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)

        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.expense)
            .setContentTitle("Expense Tracker ")
            .setContentText("Your Goal Amount has been exceed")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)

        val notification = builder.build()
        notificationManager.notify(notificationId, notification)
    }

    private fun isInternetAvailable(): Boolean {
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork
        val capabilities = connectivityManager.getNetworkCapabilities(network)

        return capabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
    }

    private fun requestPermission() {
        isNotificationPermissionGranted = ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
        val permissionRequest: MutableList<String> = ArrayList()
        if (!isNotificationPermissionGranted) {
            permissionRequest.add(android.Manifest.permission.POST_NOTIFICATIONS)

        }
        if (permissionRequest.isNotEmpty()) {
            permissionLauncher.launch(permissionRequest.toTypedArray())

        }
    }
}


