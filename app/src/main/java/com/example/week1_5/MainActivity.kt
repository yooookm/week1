package com.example.week1_5

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.content.Intent
import android.widget.TextView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // 카드뷰 1
        val card1 = findViewById<View>(R.id.contact)
        val card1Header = card1.findViewById<TextView>(R.id.card_header) // 여기서 id는 TextView의 id를 참조해야 합니다.
        val card1SubHeader = card1.findViewById<TextView>(R.id.card_subheader) // 여기서 id는 TextView의 id를 참조해야 합니다.

        card1Header.text = "Contact"
        card1SubHeader.text = "Shows the contact information saved on your phone. You can add, modify, and delete contacts"
        card1.setOnClickListener{
            ContactClick(card1)
        }

        // 카드뷰 2
        val card2 = findViewById<View>(R.id.gallery)
        val card2Header = card2.findViewById<TextView>(R.id.card_header)
        val card2SubHeader = card2.findViewById<TextView>(R.id.card_subheader)

        card2Header.text = "Gallary"
        card2SubHeader.text = "It shows the pictures saved on the phone. You can view detailed information about the picture. and also you can take pictures and delete them."
        card2.setOnClickListener{
            GalleryClick(card2)
        }
        // 카드뷰 3
        val card3 = findViewById<View>(R.id.button)
        val card3Header = card3.findViewById<TextView>(R.id.card_header)
        val card3SubHeader = card3.findViewById<TextView>(R.id.card_subheader)

        card3Header.text = "GPT Diary"
        card3SubHeader.text = "Selecting a picture recognizes the image and generates a tag. The generated tags allow GPT to ask questions and keep a diary."
        card3.setOnClickListener{
            BtnClick(card3)
        }
    }

    fun ContactClick(v: View){
        val intent = Intent(this, ContactActivity::class.java)
        startActivity(intent)
    }

    fun GalleryClick(v: View){
        val intent = Intent(this, GalleryActivity::class.java)
        startActivity(intent)
    }

    fun BtnClick(v: View){
        val intent = Intent(this, ChatbotActivity::class.java)
        startActivity(intent)
    }
}