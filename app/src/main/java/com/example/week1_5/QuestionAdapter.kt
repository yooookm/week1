package com.example.week1_5

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import java.lang.StringBuilder

class QuestionAdapter(
    val imageUris: List<Uri>,
    val questions: List<String>,
    val answers: MutableList<String?> // 'null' 값을 허용하는 새로운 인자를 추가했습니다
) : RecyclerView.Adapter<QuestionAdapter.MyViewHolder>() {
    var quesAns =StringBuilder()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.chat_item, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        Log.d("position", "${position}")
        Log.d("answer size", "${answers.size}")
        if (answers.size >= position) {
            val currentUri = imageUris[position]
            Glide.with(holder.imageView.context).load(currentUri).into(holder.imageView)

            val currentQuestion = questions[position]
            holder.questionTextView.text = currentQuestion

        }

        if (answers.size > position) {  // answers.size와 position을 비교해야합니다.
            val currentAnswer = answers[position]
            holder.answerTextView.text = currentAnswer ?: "" // 답변을 설정하는 코드를 추가합니다.
            quesAns.append(questions+ " ")
            quesAns.append(currentAnswer+ "\n")

        } else {
            holder.answerTextView.text = ""  // 답변이 null일 경우, 아무것도 표시하지 않습니다
        }
    }

    fun viewBack(): String {
        return quesAns.toString()
    }

    override fun getItemCount() = questions.size

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.image)
        val questionTextView: TextView = itemView.findViewById(R.id.question)
        val answerTextView: TextView = itemView.findViewById(R.id.answer)
    }

    fun addAnswer(userinput: String) {
        Log.d("answer check","before${answers.size}")
        answers.add(userinput)
        Log.d("answer check","after${answers.size}")
    }
}
