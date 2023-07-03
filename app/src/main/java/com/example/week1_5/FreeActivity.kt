package com.example.week1_5

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.chat.ChatCompletion
import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatRole
import com.aallam.openai.api.completion.CompletionRequest
import com.aallam.openai.api.completion.TextCompletion
import com.aallam.openai.api.model.Model
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class FreeActivity : AppCompatActivity() {
    val openAI = OpenAI("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")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.free_view)
    }

    @OptIn(BetaOpenAI::class)
    suspend fun generateResponse(input: String): String {
        val chatCompletionRequest = ChatCompletionRequest(
            model = ModelId("gpt-3.5-turbo"),
            messages = listOf(
                ChatMessage(
                    role = ChatRole.System,
                    content = "You are a helpful assistant."
                ),
                ChatMessage(
                    role = ChatRole.User,
                    content = input
                )
            )
        )

        val completion: ChatCompletion = openAI.chatCompletion(chatCompletionRequest)
        return completion.choices.first().message?.content?.toString().toString()
    }


    fun onButtonClicked(view: View) {
        val inputEditText = findViewById<EditText>(R.id.inputEditText)
        val userInput = inputEditText.text.toString()

        CoroutineScope(Dispatchers.Main).launch {
            val response = generateResponse(userInput)
            Toast.makeText(this@FreeActivity, response, Toast.LENGTH_LONG).show()
        }
    }
}
