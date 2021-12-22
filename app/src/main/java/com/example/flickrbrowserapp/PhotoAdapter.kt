package com.example.flickrbrowserapp

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.flickrbrowserapp.databinding.PhotoRowBinding
import com.example.flickrbrowserapp.model.PhotoModel

class PhotoAdapter(private var photos: ArrayList<PhotoModel>, private val activity: MainActivity): RecyclerView.Adapter<PhotoAdapter.PhotoViewHolder>() {
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
                activity.startActivity(
                    Intent(activity, DetailsActivity::class.java)
                        .putExtra("title", photo.title)
                        .putExtra("url", photo.thumbnailsBig)
                )
            }
        }
    }

    override fun getItemCount() = photos.size


    fun update (photos: ArrayList<PhotoModel>) {
        this.photos = photos
        notifyDataSetChanged()
    }

}