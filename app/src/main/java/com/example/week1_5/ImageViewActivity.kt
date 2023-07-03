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
import androidx.viewpager2.widget.ViewPager2
import java.util.*

class ImageViewActivity : AppCompatActivity() {

    private lateinit var imageUriList: ArrayList<Uri>
    private lateinit var imageNameList: ArrayList<String>
    private lateinit var imageDateList: ArrayList<Date>
    private var selectedIndex: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_view)

        val viewPager = findViewById<ViewPager2>(R.id.view_pager)
        val deleteButton = findViewById<ImageButton>(R.id.delete_button)
        val infoButton = findViewById<ImageButton>(R.id.info_button)

        selectedIndex = intent.getIntExtra(IMAGE_INDEX, 0)
        imageUriList = intent.getSerializableExtra(IMAGE_URI_LIST) as ArrayList<Uri>
        imageNameList = intent.getSerializableExtra(IMAGE_NAME_LIST) as ArrayList<String>
        imageDateList = intent.getSerializableExtra(IMAGE_DATE_LIST) as ArrayList<Date>

        val imageViewPagerAdapter = ImageViewPagerAdapter(imageUriList)
        viewPager.adapter = imageViewPagerAdapter
        viewPager.setCurrentItem(selectedIndex, false)

        deleteButton.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Delete Image")
                .setMessage("Do you want to delete this image?")
                .setPositiveButton("Yes") { _, _ ->
                    deleteImage(imageUriList[viewPager.currentItem])
                    imageUriList.removeAt(viewPager.currentItem)
                    imageNameList.removeAt(viewPager.currentItem)
                    imageDateList.removeAt(viewPager.currentItem)
                    imageViewPagerAdapter.notifyItemRemoved(viewPager.currentItem)
                }
                .setNegativeButton("No", null)
                .show()
        }

        infoButton.setOnClickListener {
            showDialog(imageNameList[viewPager.currentItem], imageDateList[viewPager.currentItem])
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
        const val IMAGE_INDEX = "IMAGE_INDEX"
        const val IMAGE_URI_LIST = "IMAGE_URI_LIST"
        const val IMAGE_NAME_LIST = "IMAGE_NAME_LIST"
        const val IMAGE_DATE_LIST = "IMAGE_DATE_LIST"

        fun newIntent(context: Context, selectedIndex: Int, imageUriList: ArrayList<Uri>, imageNameList: ArrayList<String>, imageDateList: ArrayList<Date>): Intent {
            val intent = Intent(context, ImageViewActivity::class.java)
            intent.putExtra(IMAGE_INDEX, selectedIndex)
            intent.putExtra(IMAGE_URI_LIST, imageUriList)
            intent.putExtra(IMAGE_NAME_LIST, imageNameList)
            intent.putExtra(IMAGE_DATE_LIST, imageDateList)
            return intent
        }
    }
}
