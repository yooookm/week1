package com.example.week1_5

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.chat.ChatMessage

class ChatbotViewModel : ViewModel() {
    @OptIn(BetaOpenAI::class)
    val chatHistory = MutableLiveData<ArrayList<ChatMessage>>(ArrayList())
}
