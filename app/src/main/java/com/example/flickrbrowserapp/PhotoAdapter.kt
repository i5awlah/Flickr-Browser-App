package com.example.flickrbrowserapp

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.flickrbrowserapp.databinding.PhotoRowBinding

class PhotoAdapter(private var photos: ArrayList<Photo>, private val activity: MainActivity): RecyclerView.Adapter<PhotoAdapter.PhotoViewHolder>() {
    class PhotoViewHolder(val binding: PhotoRowBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        return PhotoViewHolder(
            PhotoRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        val photo = photos[position]
        holder.binding.apply {
            Glide.with(activity)
                .load(photo.thumbnailsSmall)
                .into(imageView)

            cvPhoto.setOnClickListener {
                activity.showImageAlert(photo.thumbnailsBig, photo.title)
            }
        }
    }

    override fun getItemCount() = photos.size


    fun update (photos: ArrayList<Photo>) {
        this.photos = photos
        notifyDataSetChanged()
    }

}