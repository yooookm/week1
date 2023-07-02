package com.example.week1_5

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import java.io.File
import java.util.Date

class ImageViewActivity : AppCompatActivity() {

    private lateinit var imageUri: Uri
    private lateinit var imageName: String
    private lateinit var imageDate: Date

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_view)

        val imageView = findViewById<ImageView>(R.id.full_image)
        val deleteButton = findViewById<ImageButton>(R.id.delete_button)
        val infoButton = findViewById<ImageButton>(R.id.info_button)

        imageUri = intent.getParcelableExtra(ImageViewActivity.IMAGE_URI)!!
        imageName = intent.getStringExtra(ImageViewActivity.IMAGE_NAME)!!
        imageDate = intent.getSerializableExtra(ImageViewActivity.IMAGE_DATE) as Date

        Glide.with(this).load(imageUri).into(imageView)

        deleteButton.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Delete Image")
                .setMessage("Do you want to delete this image?")
                .setPositiveButton("Yes") { _, _ ->
                    deleteImage(imageUri)
                }
                .setNegativeButton("No", null)
                .show()
        }


        infoButton.setOnClickListener {
            showDialog(imageName, imageDate)
        }
    }

    private fun showDialog(name: String, date: Date) {
        AlertDialog.Builder(this)
            .setTitle(name)
            .setMessage("Date Taken: ${date.toString()}")
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }.show()
    }
    private fun deleteImage(uri: Uri) {
        try {
            contentResolver.delete(uri, null, null)
            Toast.makeText(this, "Image deleted successfully", Toast.LENGTH_SHORT).show()
            finish() // Close the activity after deleting the image
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "${e} Failed to delete image", Toast.LENGTH_SHORT).show()
        }
    }


    companion object {
        const val IMAGE_URI = "IMAGE_URI"
        const val IMAGE_NAME = "IMAGE_NAME"
        const val IMAGE_DATE = "IMAGE_DATE"

        fun newIntent(context: Context, imageUri: Uri, imageName: String, imageDate: Date): Intent {
            val intent = Intent(context, ImageViewActivity::class.java)
            intent.putExtra(IMAGE_URI, imageUri)
            intent.putExtra(IMAGE_NAME, imageName)
            intent.putExtra(IMAGE_DATE, imageDate)
            return intent
        }
    }
}
