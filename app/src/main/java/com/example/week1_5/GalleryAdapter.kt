package com.example.week1_5

import android.media.Image
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class GalleryAdapter(private val myImageList: List<imageInfo>) :
    RecyclerView.Adapter<GalleryAdapter.MyViewHolder>() {

    // ViewHolder 클래스
    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imageView: ImageView = itemView.findViewById(R.id.image)
    }

    // onCreateViewHolder에서는 item 레이아웃을 inflate 합니다.
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.gallery_item, parent, false)
        return MyViewHolder(v)
    }

    // onBindViewHolder에서는 ViewHolder에 데이터를 바인딩 합니다.
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        Glide.with(holder.imageView.context).load(myImageList[position].uri).into(holder.imageView)
    }

    // getItemCount에서는 아이템의 총 개수를 반환합니다.
    override fun getItemCount(): Int {
        return myImageList.size
    }
}