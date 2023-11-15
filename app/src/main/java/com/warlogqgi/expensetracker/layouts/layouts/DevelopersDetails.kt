package com.warlogqgi.expensetracker.layouts.layouts

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import com.warlogqgi.expensetracker.R

class DevelopersDetails : AppCompatActivity() {
    private lateinit var rakeshImg: ImageView
    private lateinit var utkarshImg : ImageView
    private lateinit var backBtn :ImageView
    private lateinit var imageResource: Drawable
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_devlopers_details)
        rakeshImg = findViewById(R.id.ivRakesh)
        utkarshImg = findViewById(R.id.ivUtkarsh)
        backBtn = findViewById(R.id.toolImgBack)

        backBtn.setOnClickListener {
            onBackPressed()
        }

        rakeshImg.setOnClickListener {
            showImagePreview(resources.getDrawable(R.drawable.rakeshf),"Rakesh Kumar\nB.tech CSE")
        }
        utkarshImg.setOnClickListener {

            showImagePreview(resources.getDrawable(R.drawable.utkarshf),"Utkarsh Kant\nB.tech CSE")
        }

    }
    private fun showImagePreview(image: Drawable, title:String) {
        val inflater = LayoutInflater.from(this)
        val view = inflater.inflate(R.layout.preview_image, null)

        val fullSizeImageView: ImageView = view.findViewById(R.id.fullSizeImageView)
        fullSizeImageView.setImageDrawable(image)

        val dialog = AlertDialog.Builder(this)
            .setView(view)
            .setTitle(title)
            .setPositiveButton("Close") { dialog, _ ->
                dialog.dismiss()
            }
            .create()

        dialog.show()

        // Set the size of the dialog's window to match the image size
        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
    }
}
