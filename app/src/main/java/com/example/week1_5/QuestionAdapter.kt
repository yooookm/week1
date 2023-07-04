package com.example.week1_5

import android.net.Uri
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class QuestionAdapter(
    private val imageUris: List<Uri>,
    private val questions: List<String>,
    private val answers: MutableList<String?> // 'null' 값을 허용하는 새로운 인자를 추가했습니다
) : RecyclerView.Adapter<QuestionAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.chat_item, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentUri = imageUris[position]
        Glide.with(holder.imageView.context).load(currentUri).into(holder.imageView)

        val currentQuestion = questions[position]
        holder.questionTextView.text = currentQuestion

        val currentAnswer = answers[position]
        holder.answerTextView.text =
            (currentAnswer ?: "") as Editable? // 답변이 null일 경우, 아무것도 표시하지 않습니다
    }

    override fun getItemCount() = questions.size

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.image)
        val questionTextView: TextView = itemView.findViewById(R.id.question)
        val answerTextView: EditText = itemView.findViewById(R.id.answer)
    }
}
