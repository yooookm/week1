package com.example.week1_5

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class ImageViewPagerAdapter(private val imageUriList: ArrayList<Uri>) : RecyclerView.Adapter<ImageViewPagerAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.page_image)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.page_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.imageView.scaleType = ImageView.ScaleType.FIT_CENTER
        Glide.with(holder.itemView.context).load(imageUriList[position]).into(holder.imageView)
    }

    override fun getItemCount(): Int {
        return imageUriList.size
    }
}

