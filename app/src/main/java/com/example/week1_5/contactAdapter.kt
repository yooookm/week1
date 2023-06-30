package com.example.week1_5

import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView // 추가

class contactAdapter(val itemList: ArrayList<contactInfo>) : RecyclerView.Adapter<contactAdapter.ContactViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.contact_item, parent, false)
        return ContactViewHolder(view)
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        holder.contactName.text = itemList[position].name
        holder.contactNumber.text = itemList[position].contactNum
    }

    override fun getItemCount(): Int {
        return itemList.count()
    }

    inner class ContactViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val contactName: TextView = itemView.findViewById(R.id.contact_name)
        val contactNumber: TextView = itemView.findViewById(R.id.contact_number)
    }
}
