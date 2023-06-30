package com.example.week1_5

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View

class ContactActivity :AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.contact_view)

        val contactAdapter = MainListAdapter(this, contactList)
        mainListView.adapter = contactAdapter
    }
}

var contactList = arrayListOf<contactInfo>()