package com.example.week1_5

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.content.Intent

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun ContactClick(v: View){
        val intent = Intent(this, ContactActivity::class.java)
        startActivity(intent)
    }

    fun GalleryClick(v: View){
        val intent = Intent(this, GalleryActivity::class.java)
        startActivity(intent)
    }

    fun BtnClick(v: View){
        val intent = Intent(this, FreeActivity::class.java)
        startActivity(intent)
    }
}