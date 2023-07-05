package com.example.week1_5

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentUris
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder.ImageInfo
import android.graphics.Rect
import android.icu.text.SimpleDateFormat
import android.media.Image
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.MediaStore.Audio.Media
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.week1_5.databinding.GalleryViewBinding
import java.util.Date
import java.util.Locale


class GalleryActivity : AppCompatActivity() {
    private val REQUEST_GALLERY_PERMISSION = 100
    private val REQUEST_WRITE_PERMISSION = 101
    lateinit var binding: GalleryViewBinding
    lateinit var galleryRV: RecyclerView
    lateinit var imageList: ArrayList<imageInfo>

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_GALLERY_PERMISSION -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    Log.d("test", "READ permission granted")
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), REQUEST_WRITE_PERMISSION)
                    } else {
                        loadGallery()
                    }
                } else {
                    Log.d("test", "READ permission denied")
                }
            }
            REQUEST_WRITE_PERMISSION -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    Log.d("test", "WRITE permission granted")
                    loadGallery()
                } else {
                    Log.d("test", "WRITE permission denied")
                }
            }
            else -> {
                super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            }
        }
    }

    override fun onResume() {

        super.onResume()
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
            && ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            loadGallery()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var binding: GalleryViewBinding
        binding = GalleryViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        galleryRV = findViewById<RecyclerView>(R.id.gallery_RV)
        val itemDecoration = VerticalSpaceItemDeco(20)  // 10px의 공간을 추가
        galleryRV.addItemDecoration(itemDecoration)

        imageList = ArrayList<imageInfo>()
        binding.cameraButton.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 1)
            } else {
                val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                if (takePictureIntent.resolveActivity(packageManager) != null) {
                    startActivityForResult(takePictureIntent, 1)
                } else {
                    Toast.makeText(this, "No camera app available", Toast.LENGTH_SHORT).show()
                }
            }
        }


        galleryRV.layoutManager = GridLayoutManager(this, 3)
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), REQUEST_GALLERY_PERMISSION)
        } else
        {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    REQUEST_WRITE_PERMISSION
                )
            } else {
                loadGallery()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as Bitmap
            val savedImageUri = saveImageToExternalStorage(imageBitmap)
            if (savedImageUri != null) {
                //
            } else {
                Toast.makeText(this, "Failed to save image", Toast.LENGTH_SHORT).show()
            }
        }
    }
    @SuppressLint("NewApi")
    private fun saveImageToExternalStorage(bitmap: Bitmap): Uri? {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val imageFileName = "IMG_$timeStamp.jpg"

        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, imageFileName)
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis() / 1000)
        }

        val resolver = contentResolver
        var imageUri: Uri? = null

        resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)?.let { uri ->
            imageUri = uri
            resolver.openOutputStream(uri)?.use { outputStream ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
            }
        }

        return imageUri
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
                galleryRV.layoutManager = GridLayoutManager(this, 3)

            }
        }
    }
    inner class VerticalSpaceItemDeco(private val verticalSpace: Int) :RecyclerView.ItemDecoration() {
        override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
            outRect.bottom = verticalSpace
        }
    }
}
