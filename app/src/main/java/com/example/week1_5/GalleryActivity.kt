package com.example.week1_5

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentUris
import android.content.pm.PackageManager
import android.graphics.ImageDecoder.ImageInfo
import android.media.Image
import android.os.Bundle
import android.provider.MediaStore
import android.provider.MediaStore.Audio.Media
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.week1_5.databinding.GalleryViewBinding
import java.util.Date

class GalleryActivity : AppCompatActivity() {
    private val REQUEST_GALLERY_PERMISSION = 100
    lateinit var binding: GalleryViewBinding
    lateinit var galleryRV: RecyclerView
    lateinit var imageList: ArrayList<imageInfo>


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == REQUEST_GALLERY_PERMISSION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("test", "permission granted")
                loadGallery()
            } else {
                Log.d("test", "permission denied")
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var binding: GalleryViewBinding
        binding = GalleryViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        galleryRV = findViewById<RecyclerView>(R.id.gallery_RV)
        imageList = ArrayList<imageInfo>()
        galleryRV.layoutManager = GridLayoutManager(this, 3)
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), REQUEST_GALLERY_PERMISSION)
        } else {
            loadGallery()
        }
    }

    @SuppressLint("Range")
    fun loadGallery() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), REQUEST_GALLERY_PERMISSION)
        } else {
            imageList.clear()

            val cursor = contentResolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                arrayOf(
                    MediaStore.Images.Media._ID,
                    MediaStore.Images.Media.DISPLAY_NAME,
                    MediaStore.Images.Media.DATE_ADDED
                ),
                null,
                null,
                null
            )

            cursor?.let {
                if (it.moveToFirst()) {
                    do {
                        val id = it.getLong(it.getColumnIndex(MediaStore.Images.Media._ID))
                        val name = it.getString(it.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME))
                        val uri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
                        val dateTaken = Date(it.getLong(it.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_ADDED)))
                        imageList.add(imageInfo(id, name, uri, dateTaken))
                    } while (it.moveToNext())
                }
                it.close()

                imageList.sortBy { it.date }

                val galleryAdapter = GalleryAdapter(imageList)
                galleryAdapter.notifyDataSetChanged()
                galleryRV.adapter =galleryAdapter
                galleryRV.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
            }
        }
    }
}
