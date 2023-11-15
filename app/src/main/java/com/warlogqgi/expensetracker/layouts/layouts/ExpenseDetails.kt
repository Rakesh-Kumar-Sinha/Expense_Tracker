package com.warlogqgi.expensetracker.layouts.layouts
import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.warlogqgi.expensetracker.R
import com.warlogqgi.expensetracker.layouts.adapters.RecyclerViewAdapter
import com.warlogqgi.expensetracker.layouts.model.DataModel
import java.io.File
import java.io.IOException
import java.util.Calendar
class ExpenseDetails : AppCompatActivity() {

    lateinit var imgBackBtn: ImageView
    lateinit var imgMenuBtn: ImageView
    lateinit var toolImgMenu: ImageView
    lateinit var recyclerViewExpend: RecyclerView
    lateinit var dataList: ArrayList<DataModel>
    lateinit var catTransTextView: TextView
    private lateinit var adapeter: RecyclerViewAdapter

    var calendar = Calendar.getInstance()
    var YEAR = calendar.get(Calendar.YEAR)
    var MONTH = calendar.get(Calendar.MONTH) + 1
    var DAY = calendar.get(Calendar.DAY_OF_MONTH)
    var currentDate = "/$MONTH/"


    var currentUserId = FirebaseAuth.getInstance().currentUser!!.uid
    var db = FirebaseFirestore.getInstance()

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_expense_details)
        //setting day night mode as default
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        imgBackBtn = findViewById(R.id.tooImgBarBack)
        imgMenuBtn = findViewById(R.id.toolImgMenu)
        toolImgMenu = findViewById(R.id.toolImgMenu)
        catTransTextView = findViewById(R.id.catTransTextView)
        recyclerViewExpend = findViewById(R.id.RecyclerViewExpend)
        loaddata("All")


        imgBackBtn.setOnClickListener {
            onBackPressed()
        }
        recyclerViewExpend.layoutManager = LinearLayoutManager(this)
        dataList = arrayListOf()
        adapeter = RecyclerViewAdapter(dataList, this)

        recyclerViewExpend.adapter = adapeter
        toolImgMenu.setOnClickListener {
            val popupMenu: PopupMenu = PopupMenu(this, toolImgMenu)
            popupMenu.menuInflater.inflate(R.menu.catogery_menu, popupMenu.menu)
            popupMenu.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener {

                when (it.itemId) {
                    R.id.menuAll -> {
                        dataList.clear()
                        adapeter.notifyDataSetChanged()
                        loaddata("All")
                        catTransTextView.text = "All"


                    }

                    R.id.menuFood -> {
                        dataList.clear()
                        adapeter.notifyDataSetChanged()
                        loaddata("Food")
                        catTransTextView.text = "Food"


                    }

                    R.id.menuEntertainment -> {
                        dataList.clear()
                        adapeter.notifyDataSetChanged()
                        loaddata("Entertainment")
                        catTransTextView.text = "Entertainment"

                    }

                    R.id.menuTransport -> {
                        dataList.clear()
                        adapeter.notifyDataSetChanged()
                        loaddata("Transport")
                        catTransTextView.text = "Transport"

                    }

                    R.id.menuOther -> {
                        dataList.clear()
                        adapeter.notifyDataSetChanged()
                        loaddata("Other")
                        catTransTextView.text = "Other"


                    }

                    R.id.menuAllTime -> {
                        dataList.clear()
                        adapeter.notifyDataSetChanged()
                        loaddata("AllTime")
                        catTransTextView.text = "All time"
                    }

                    R.id.menuExport -> {
                        exportCsv(dataList)
                        catTransTextView.text = "Exported"
                    }


                }



                return@OnMenuItemClickListener true
            })
            popupMenu.show()


        }


    }

    private fun exportCsv(data: ArrayList<DataModel>) {
        val csvHeader = "ID ,Category ,Description ,Expend Amount ,Date\n"
        val csvData =
            data.joinToString("\n") { "${it.id},${it.category},${it.desc},${it.expamount},${it.date}" }
        val csvContent = csvHeader + csvData
        val csvFileName = "expense.csv"
        val csvFile = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
            csvFileName
        )

        try {
            csvFile.bufferedWriter().use { out ->
                out.write(csvContent)
                Toast.makeText(this, "Exported data ", Toast.LENGTH_SHORT).show()
            }
            // File saved successfully
        } catch (e: IOException) {
            Toast.makeText(this, "Error in exporting file", Toast.LENGTH_SHORT).show()
        }

    }


    private fun loaddata(category: String) {
        var ref = db.collection(currentUserId).document("expense").collection("data").get()
        ref.addOnSuccessListener { result ->
            for (data in result) {
                if (data.get("category").toString().equals(category) && data.get("date").toString()
                        .contains(currentDate)
                ) {
                    dataList.add(
                        DataModel(
                            data.id.toString(),
                            data.data.get("category").toString(),
                            data.data.get("desc").toString(),
                            data.data.get("expamount").toString(),
                            data.data.get("date").toString(),
                            data.data.get("time").toString()
                        )
                    )
                } else if (category.equals("All") && data.get("date").toString()
                        .contains(currentDate)
                ) {

                    dataList.add(
                        DataModel(
                            data.id.toString(),
                            data.data.get("category").toString(),
                            data.data.get("desc").toString(),
                            data.data.get("expamount").toString(),
                            data.data.get("date").toString(),
                            data.data.get("time").toString()
                        )
                    )


                } else if (category.equals("AllTime")
                ) {

                    dataList.add(
                        DataModel(
                            data.id.toString(),
                            data.data.get("category").toString(),
                            data.data.get("desc").toString(),
                            data.data.get("expamount").toString(),
                            data.data.get("date").toString(),
                            data.data.get("time").toString()
                        )
                    )
                }
            }

            adapeter.notifyDataSetChanged()


        }.addOnFailureListener {
            Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show()
        }
    }
}
