package com.example.week1_5
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatRole
import com.example.week1_5.R

class ChatAdapter @OptIn(BetaOpenAI::class) constructor(private val chatHistory: List<ChatMessage>) : RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {

    class ChatViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val role: TextView = view.findViewById(R.id.role)
        val content: TextView = view.findViewById(R.id.content)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.chat_message_item, parent, false)
        return ChatViewHolder(view)
    }

    @OptIn(BetaOpenAI::class)
    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val chatMessage = chatHistory[position]
        holder.role.text = if (chatMessage.role == ChatRole.User) "You" else "Assistant"
        holder.content.text = chatMessage.content
    }

    @OptIn(BetaOpenAI::class)
    override fun getItemCount(): Int = chatHistory.size
}
