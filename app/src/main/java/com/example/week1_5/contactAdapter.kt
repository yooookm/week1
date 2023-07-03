package com.example.week1_5

import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.week1_5.contactAdapter.ContactViewHolder

class contactAdapter(val itemList: ArrayList<contactInfo>) : RecyclerView.Adapter<ContactViewHolder>() {


    private var isItemClickable: Boolean = true

    // 아이템 클릭 가능 상태를 변경하는 메서드
    fun setItemClickable(isClickable: Boolean) {
        this.isItemClickable = isClickable
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.contact_item, parent, false)
        return ContactViewHolder(view)
    }


    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        holder.contactName.text = itemList[position].name
        holder.itemView.isClickable = isItemClickable
    }

    override fun getItemCount(): Int {
        return itemList.count()
    }

    fun getId(position:Int) : String {
        return itemList[position].id
    }

    inner class ContactViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val contactName: TextView = itemView.findViewById(R.id.contact_name)

        init {
            itemView.setOnClickListener {
                if (isItemClickable) {
                    val position: Int = adapterPosition
                    val context = itemView.context

                    // Add a dialog to show the phone number
                    val builder = AlertDialog.Builder(context)
                    builder.setTitle(itemList[position].name)
                    builder.setMessage(itemList[position].contactNum)

                    builder.setPositiveButton("OK") { dialog, _ ->
                        dialog.dismiss()
                    }

                    builder.show()
                }
            }
        }

    }
}
