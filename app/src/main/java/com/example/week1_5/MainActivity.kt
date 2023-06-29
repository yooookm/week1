package com.example.week1_5

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

    }

    fun ContactClick(v: View){
        setContentView(R.layout.contact_view)
    }

   fun GalleryClick(v: View){
        setContentView(R.layout.contact_view)
    }

   fun BtnClick(v: View){
        setContentView(R.layout.contact_view)
    }

}