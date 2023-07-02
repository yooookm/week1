package com.example.week1_5

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import java.io.File

class ImageViewActivity : AppCompatActivity() {

    private lateinit var imageUri: Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_view)

        val imageView = findViewById<ImageView>(R.id.full_image)
        val imageUri: Uri = intent.getParcelableExtra(IMAGE_URI)!!

        Glide.with(this).load(imageUri).into(imageView)

        imageView.setOnClickListener {
            deleteImage(imageUri)
        }
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

        fun newIntent(context: Context, imageUri: Uri): Intent {
            val intent = Intent(context, ImageViewActivity::class.java)
            intent.putExtra(IMAGE_URI, imageUri)
            return intent
        }
    }
}
