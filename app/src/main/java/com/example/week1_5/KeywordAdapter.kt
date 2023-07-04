package com.example.week1_5

import android.content.Context
import android.net.Uri
import android.os.Parcel
import android.os.Parcelable
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.week1_5.ImageViewActivity
import com.example.week1_5.R
import com.example.week1_5.DiaryItem
import java.util.Date

class DiaryItemAdapter(val items: MutableList<DiaryItem>) :
    RecyclerView.Adapter<DiaryItemAdapter.DiaryItemViewHolder>() {

    inner class DiaryItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.imageView)
        val tagLayout: LinearLayout = view.findViewById(R.id.tag_layout)
        val addTagButton: Button = view.findViewById(R.id.add_tag_button)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiaryItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.diary_item, parent, false)
        return DiaryItemViewHolder(view)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: DiaryItemViewHolder, position: Int) {
        val item = items[position]

        holder.imageView.setImageURI(item.imageUri)

        holder.tagLayout.removeAllViews() // Clear all existing views
        for (tag in item.tags) {
            val tagView = createTagView(tag, holder.itemView.context, item)
            holder.tagLayout.addView(tagView)
        }

        holder.addTagButton.setOnClickListener {
            // Show dialog and add tag
            showDialogAndAddTag(holder.itemView.context, item)
        }
    }



    private fun createTagView(tag: String, context: Context, item: DiaryItem): View {
        val inflater = LayoutInflater.from(context)
        val tagView = inflater.inflate(R.layout.keyword_item, null, false)

        val textView = tagView.findViewById<TextView>(R.id.tag_text)
        textView.text = tag

        val deleteButton = tagView.findViewById<ImageButton>(R.id.delete_button)
        deleteButton.setOnClickListener {
            item.tags.remove(tag)
            notifyDataSetChanged()
        }

        return tagView
    }


    fun addItem(diaryItem: DiaryItem) {
        this.items.add(diaryItem)
    }
    private fun showDialogAndAddTag(context: Context, item: DiaryItem) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(context)
        builder.setTitle("Add Tag")

        val input = EditText(context)
        input.inputType = InputType.TYPE_CLASS_TEXT
        builder.setView(input)

        builder.setPositiveButton("OK") { dialog, _ ->
            val tag = input.text.toString()
            if (tag.isNotEmpty()) {
                item.tags.add(tag)
                notifyDataSetChanged()  // Update RecyclerView
            }
            dialog.dismiss()
        }
        builder.setNegativeButton("Cancel") { dialog, _ -> dialog.cancel() }

        builder.show()
    }

}
