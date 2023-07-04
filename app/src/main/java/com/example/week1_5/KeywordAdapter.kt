package com.example.week1_5

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class KeywordAdapter(private val keywords: MutableList<String>) : RecyclerView.Adapter<KeywordAdapter.KeywordViewHolder>() {

    inner class KeywordViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val keywordTextView: TextView = view.findViewById(R.id.keyword_text)

        init {
            view.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    removeKeyword(position)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): KeywordViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.keyword_item, parent, false)
        return KeywordViewHolder(view)
    }

    override fun getItemCount(): Int = keywords.size

    override fun onBindViewHolder(holder: KeywordViewHolder, position: Int) {
        holder.keywordTextView.text = keywords[position]
    }

    fun addKeyword(keyword: String) {
        keywords.add(keyword)
        Log.d("KeywordAdapter", "Added keyword: $keyword")
        notifyItemInserted(keywords.size - 1)
    }

    fun removeKeyword(position: Int) {
        keywords.removeAt(position)
        notifyItemRemoved(position)
    }

    fun getAllKeywords(): List<String> {
        return keywords
    }
}
