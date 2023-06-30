package com.example.week1_5

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.BaseAdapter
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView

class contactListAdapter(val context: Context, val contactList: ArrayList<contactInfo>) : BaseAdapter(){
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View = LayoutInflater.from(context).inflate(R.layout.contactitem_sample, null)

        val contactPhoto = view.findViewById<ImageView>(R.id.contact_img)
        val contactName = view.findViewById<TextView>(R.id.contact_name)

        val contact = contactList[position]
        val resourceid = context.resources.getIdentifier(contact.photo, "drawable", context.packageName)
        contactPhoto.setImageResource(resourceid)
        contactName.text = contact.name
        return view
    }

    override fun getItem(position: Int): Any {
        return contactList[position]
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getCount(): Int {
        return contactList.size
    }
}