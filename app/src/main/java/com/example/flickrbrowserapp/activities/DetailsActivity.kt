package com.example.flickrbrowserapp.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.example.flickrbrowserapp.databinding.ActivityDetailsBinding

class DetailsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val title = intent.getStringExtra("title")
        val imageUrl = intent.getStringExtra("url")

        binding.tvTitle.text = title
        Glide.with(this).load(imageUrl).into(binding.ivBig)
        binding.tvTitle.text = title

        binding.clMain.setOnClickListener {
            this.finish()
        }
    }
}