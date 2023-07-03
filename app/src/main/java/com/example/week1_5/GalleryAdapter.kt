package com.example.week1_5

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import java.util.Date

class GalleryAdapter(private val myImageList: List<imageInfo>) :
    RecyclerView.Adapter<GalleryAdapter.MyViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.gallery_item, parent, false)
        return MyViewHolder(v)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        Glide.with(holder.imageView.context).load(myImageList[position].uri).into(holder.imageView)
    }

    override fun getItemCount(): Int {
        return myImageList.size
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.image)

        init {
            itemView.setOnClickListener {
                val position: Int = adapterPosition
                val context = itemView.context
                val image = myImageList[position]
                val imageUriList = myImageList.map { it.uri } as ArrayList<Uri>
                val imageNameList = myImageList.map { it.name } as ArrayList<String>
                val imageDateList = myImageList.map { it.date } as ArrayList<Date>

                context.startActivity(ImageViewActivity.newIntent(context, position, imageUriList, imageNameList, imageDateList))
            }
        }
    }
}
