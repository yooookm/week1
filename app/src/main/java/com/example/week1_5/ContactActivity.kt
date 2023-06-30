package com.example.week1_5

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ContactActivity :AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.contact_view)

        val contactRV = findViewById<RecyclerView>(R.id.contact_RV)
        val itemlist = ArrayList<contactInfo>()

        itemlist.add(contactInfo("차민호", "010-3846-5035", "1"))
        itemlist.add(contactInfo("유경미", "010-2171-9876", "2"))

        val contactAdapter1 = contactAdapter(itemlist)
        contactAdapter1.notifyDataSetChanged()

        contactRV.adapter = contactAdapter1
        contactRV.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
    }
}