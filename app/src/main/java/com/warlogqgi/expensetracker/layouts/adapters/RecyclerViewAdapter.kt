package com.warlogqgi.expensetracker.layouts.adapters

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewParent
import android.widget.EditText
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatEditText
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.warlogqgi.expensetracker.R
import com.warlogqgi.expensetracker.layouts.model.DataModel
import org.w3c.dom.Text

class RecyclerViewAdapter : RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder> {


    var dataList: ArrayList<DataModel>
    var context: Context


    constructor(dataList: ArrayList<DataModel>, context: Context) : super() {

        this.dataList = dataList
        this.context = context
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        var vv = LayoutInflater.from(parent.context).inflate(R.layout.recy_card, parent, false)


        return MyViewHolder(vv)

    }

    override fun getItemCount(): Int {

        return dataList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {


        holder.expAmount.setText("INR " + dataList.get(position).expamount)
        holder.category.setText(dataList.get(position).category)
        holder.date.setText(dataList.get(position).date)
        holder.desc.setText(dataList.get(position).desc)
        holder.time.setText(dataList.get(position).time)
        if (dataList.get(position).category.equals("Food")){
            holder.catImg.setImageResource(R.drawable.food)
        }else if(dataList.get(position).category.equals("Entertainment")){
            holder.catImg.setImageResource(R.drawable.entertain)
        }else if(dataList.get(position).category.equals("Transport")){
            holder.catImg.setImageResource(R.drawable.transport)
        }else if(dataList.get(position).category.equals("Other")){
            holder.catImg.setImageResource(R.drawable.other)
        }

        holder.delete_item.setOnClickListener {
            val currentUserId = FirebaseAuth.getInstance().currentUser!!.uid
            val db = FirebaseFirestore.getInstance()
            var colRef = db.collection(currentUserId).document("expense").collection("data")
            var docRef = colRef.document(dataList.get(position).id)
            docRef.delete().addOnSuccessListener {
                dataList.removeAt(position)
                notifyItemRemoved(position)
                Toast.makeText(context, "Successful", Toast.LENGTH_SHORT).show()
            }.addOnFailureListener {
                Toast.makeText(context, "Failure", Toast.LENGTH_SHORT).show()

            }


        }
        holder.edit_btn.setOnClickListener {
            var dialog = Dialog(context)
            dialog.setContentView(R.layout.update_expense)
            dialog.setCancelable(true)

            var edtAmount = dialog.findViewById<EditText>(R.id.updateEdtAmount)
            var pickdate = dialog.findViewById<TextInputEditText>(R.id.updateEdtDate)
            var edtDescc = dialog.findViewById<EditText>(R.id.updateEdtDesc)
            var btnUpdate = dialog.findViewById<AppCompatButton>(R.id.btnUpdateExp)
            var radioGroup = dialog.findViewById<RadioGroup>(R.id.radioGroupUpdate)
            val pickTime = dialog.findViewById<TextInputEditText>(R.id.updateEdtTime)


            edtAmount.setText(dataList.get(position).expamount)
            pickdate.setText(dataList.get(position).date)
            edtDescc.setText(dataList.get(position).desc)
            pickTime.setText(dataList.get(position).time)
            var category: String = dataList.get(position).category.toString()

            if (category.equals("Food")) {
                var radioFood = dialog.findViewById<RadioButton>(R.id.radioFoodUpdate)
                radioFood.isChecked = true
            }else if (category.equals("Transport")){
                var radioTransport = dialog.findViewById<RadioButton>(R.id.radioTransportUpdate)
                radioTransport.isChecked = true

            }else if (category.equals("Entertainment")){
                var radioEntertainment= dialog.findViewById<RadioButton>(R.id.radioEntertainmentUpdate)
                radioEntertainment.isChecked = true

            }else if (category.equals("Other")){
                var radioOther= dialog.findViewById<RadioButton>(R.id.radioOtherUpdate)
                radioOther.isChecked = true

            }

         radioGroup.setOnCheckedChangeListener(
                RadioGroup.OnCheckedChangeListener { group, checkedId ->
                    if (checkedId == R.id.radioFoodUpdate) {
                        category = "Food"
                    } else if (checkedId == R.id.radioEntertainmentUpdate) {
                        category = "Entertainment"
                    } else if (checkedId == R.id.radioTransportUpdate) {
                        category = "Transport"
                    } else if (checkedId == R.id.radioOtherUpdate) {
                        category = "Other"
                    } else {
                        category = "Other"
                    }
                })


            var newAmount =" "
            var newDate = dataList.get(position).date
            var newDesc =" "
            var newTime = dataList.get(position).time

            btnUpdate.setOnClickListener {
                 newDesc = edtDescc.text.toString()
                if (edtAmount.text.toString().isEmpty()) {
                    edtAmount.setError("Enter The amount")
                    edtAmount.requestFocus()
                }
                else {
                    newAmount = edtAmount.text.toString()

                    if (pickdate.text != null) {
                        newDate = pickdate.text.toString()

                    }
                    if (pickTime.text!=null){
                        newTime = pickTime.text.toString()
                    }

                   // updating data
                    // putting data in hashmap
                    var data = hashMapOf(
                        "id" to "",
                        "category" to category,
                        "desc" to newDesc,
                        "expamount" to newAmount,
                        "date" to newDate,
                        "time" to newTime

                    )

                    val currentUserId = FirebaseAuth.getInstance().currentUser!!.uid
                    val db = FirebaseFirestore.getInstance()
                    var colRef = db.collection(currentUserId).document("expense").collection("data")
                    var docRef = colRef.document(dataList.get(position).id)

                    docRef.update(data as Map<String, Any>).addOnSuccessListener {
                        Toast.makeText(context, "Successfully updated data Please refresh the list", Toast.LENGTH_SHORT).show()





                    }.addOnFailureListener {
                        Toast.makeText(context  , "Error in updating data", Toast.LENGTH_SHORT).show()
                    }


                    dialog.hide()

                }
            }

           dialog.show()

        }


    }


    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {


        var category = itemView.findViewById<TextView>(R.id.categoryText)
        var desc = itemView.findViewById<TextView>(R.id.tvDesc)
        var expAmount = itemView.findViewById<TextView>(R.id.tvExp)
        var date = itemView.findViewById<TextView>(R.id.tvDate)
        var delete_item = itemView.findViewById<ImageView>(R.id.delete_Btn)
        var edit_btn = itemView.findViewById<ImageView>(R.id.edit_btn)
        val catImg = itemView.findViewById<ImageView>(R.id.catImage)
        val time = itemView.findViewById<TextView>(R.id.tvTime)


    }


}

