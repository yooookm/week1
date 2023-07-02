package com.example.week1_5

import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.View
import android.widget.TextView
import android.widget.Toast // 추가
import androidx.recyclerview.widget.RecyclerView

class contactAdapter(val itemList: ArrayList<contactInfo>) : RecyclerView.Adapter<contactAdapter.ContactViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.contact_item, parent, false)
        return ContactViewHolder(view)
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        holder.contactName.text = itemList[position].name
    }

    override fun getItemCount(): Int {
        return itemList.count()
    }

    inner class ContactViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val contactName: TextView = itemView.findViewById(R.id.contact_name)

        init {
            itemView.setOnClickListener {
                val position: Int = adapterPosition
                val context = itemView.context
                Toast.makeText(context, "Item ID: ${itemList[position].id}", Toast.LENGTH_SHORT).show()

                // Add a dialog to show the phone number
                val builder = AlertDialog.Builder(context)
                builder.setTitle(itemList[position].name)
                builder.setMessage(itemList[position].contactNum) // Use the correct reference here

                builder.setPositiveButton("OK") { dialog, _ ->
                    dialog.dismiss()
                }

                builder.show()
            }
        }
    }


}
