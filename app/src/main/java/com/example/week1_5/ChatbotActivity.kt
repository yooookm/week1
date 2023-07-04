package com.example.week1_5

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.chat.ChatCompletion
import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatRole
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ChatbotActivity : AppCompatActivity() {
    val openAI = OpenAI("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")
    private lateinit var viewModel: ChatbotViewModel
    private lateinit var adapter: ChatAdapter

    @OptIn(BetaOpenAI::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.free_view)

        viewModel = ViewModelProvider(this).get(ChatbotViewModel::class.java)

        val chatbox = findViewById<EditText>(R.id.chatbox)
        val callApiButton = findViewById<Button>(R.id.api_button)
        val chatHistoryView = findViewById<RecyclerView>(R.id.chat_history)

        adapter = ChatAdapter(viewModel.chatHistory.value!!)
        chatHistoryView.layoutManager = LinearLayoutManager(this)
        chatHistoryView.adapter = adapter

        callApiButton.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                val userInput = withContext(Dispatchers.Main) {
                    val input = chatbox.text.toString()
                    chatbox.setText("") // Clear the EditText
                    viewModel.chatHistory.value!!.add(ChatMessage(ChatRole.User, input))
                    adapter.notifyItemInserted(viewModel.chatHistory.value!!.size - 1)
                    input
                }
                callOpenAI(userInput)
            }
        }
    }

    @OptIn(BetaOpenAI::class)
    private suspend fun callOpenAI(userInput: String) {
        val chatCompletionRequest = ChatCompletionRequest(
            model = ModelId("gpt-3.5-turbo"),
            messages = listOf(
                ChatMessage(
                    role = ChatRole.Assistant,
                    content = "You are my girlfriend. Answer me kindly like a girlfriend. But in informal speech. Sympathize with my feelings."
                ),
                ChatMessage(
                    role = ChatRole.User,
                    content = userInput
                )
            ),
            maxTokens = 50
        )

        val completion: ChatCompletion = openAI.chatCompletion(chatCompletionRequest)
        withContext(Dispatchers.Main) {
            Log.d("Result", "${completion.choices.first().message?.content}")
            Toast.makeText(this@ChatbotActivity, "${completion.choices.first().message?.content}", Toast.LENGTH_SHORT).show()
            viewModel.chatHistory.value!!.add(ChatMessage(ChatRole.Assistant, completion.choices.first().message?.content ?: "")) // Store AI's response
            adapter.notifyItemInserted(viewModel.chatHistory.value!!.size - 1) // Notify the adapter that the data set has changed
        }
    }
}
