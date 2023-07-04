package com.example.week1_5

import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class QuestionActivity : AppCompatActivity() {
    private lateinit var imageUriList: List<Uri>
    private lateinit var questionList: List<String>
    private lateinit var answerList: MutableList<String?>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.chatview)

        // Retrieve the data from the Intent extras
        imageUriList = intent.getParcelableArrayListExtra("imageUriList")!!
        questionList = intent.getStringArrayListExtra("keywordsList")!!

        var answer: MutableList<String> = mutableListOf()


        val chatHistory = findViewById<RecyclerView>(R.id.chat_history)
        val questionAdapter = QuestionAdapter(imageUriList, questionList, answer.toMutableList())
        chatHistory.layoutManager = LinearLayoutManager(this)
        chatHistory.adapter = questionAdapter

        val apiButton = findViewById<Button>(R.id.api_button)
        val chatbox = findViewById<EditText>(R.id.chatbox)
        apiButton.setOnClickListener {
            val userinput = chatbox.text.toString()

            questionAdapter.addAnswer(userinput)
            questionAdapter.notifyDataSetChanged() // 버튼이 클릭될 때마다 RecyclerView 갱신

            chatbox.setText("")
        }
    }
}
