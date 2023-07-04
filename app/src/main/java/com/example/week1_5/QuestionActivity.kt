package com.example.week1_5

import android.net.Uri
import android.os.Bundle
import android.os.PersistableBundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView

class QuestionActivity: AppCompatActivity() {
    private lateinit var imageUriList: List<Uri>
    private lateinit var questionList: List<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.chatview)

        // Retrieve the data from the Intent extras
        imageUriList = intent.getParcelableArrayListExtra("imageUriList")!!
        questionList = intent.getStringArrayListExtra("keywordsList")!!

        val chatHistory = findViewById<RecyclerView>(R.id.chat_history)

        // rest of the code
    }

}