package com.example.flickrbrowserapp

import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.flickrbrowserapp.databinding.ActivityMainBinding
import com.example.flickrbrowserapp.databinding.ImageAlertBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.lang.Exception
import java.net.URL

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var rvPhotos: RecyclerView
    private lateinit var adapter: PhotoAdapter

    private var numberOfImage = 10
    private val apiKey = "2c129a93d0769924c943bbcb558d68b3"
    private var tag = "dog"

    private var photosList = arrayListOf<Photo>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val seek = binding.seekBar
        seek?.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seek: SeekBar, progress: Int, fromUser: Boolean) {
                binding.tvNumberOfImage.text = seek.progress.toString()
            }

            override fun onStartTrackingTouch(seek: SeekBar) {
                // write custom code for progress is started
                Log.d("Main", "onStartTrackingTouch")
            }

            override fun onStopTrackingTouch(seek: SeekBar) {
                numberOfImage = seek.progress.toString().toInt()
                requestAPI()
            }
        })

        setupRV()
        requestAPI()

        binding.btnSearch.setOnClickListener {
            tag = binding.etSearch.text.toString()
            if (tag.isNotEmpty()) {
                requestAPI()
            }
        }


    }

    private fun setupRV() {
        rvPhotos = binding.rvPhotos
        adapter = PhotoAdapter(photosList, this)
        rvPhotos.adapter = adapter
        rvPhotos.layoutManager = GridLayoutManager(this, 3)
    }

    private fun requestAPI() {
        photosList.clear()
        CoroutineScope(IO).launch {
            val data = async { getData() }.await()
            if (data.isNotEmpty()) {
                populateRV(data)
            } else {
                Log.d("Main", "Unable to get data")
            }
        }

    }

    private fun getData() : String {
        var response = ""
        try {
            val url = "https://api.flickr.com/services/rest/?method=flickr.photos.search&tags=$tag&per_page=$numberOfImage&api_key=$apiKey&format=json&&nojsoncallback=1"
            response = URL(url).readText()
        } catch (e: Exception) {
            Log.d("Main", "Error: $e")
        }
        return response
    }

    private suspend fun populateRV(data: String) {
        withContext(Main) {
            Log.d("Main", "Data: $data")
            val jsonObject = JSONObject(data)
            val photos = jsonObject.getJSONObject("photos").getJSONArray("photo")

            for (i in 0 until photos.length()) {
                val farm = photos.getJSONObject(i).getInt("farm")
                val id = photos.getJSONObject(i).getString("id")
                val isfamily = photos.getJSONObject(i).getInt("isfamily")
                val isfriend = photos.getJSONObject(i).getInt("isfriend")
                val ispublic = photos.getJSONObject(i).getInt("ispublic")
                val owner = photos.getJSONObject(i).getString("owner")
                val secret = photos.getJSONObject(i).getString("secret")
                val server = photos.getJSONObject(i).getString("server")
                val title = photos.getJSONObject(i).getString("title")

                // Photo Image URLs: https://live.staticflickr.com/{server-id}/{id}_{secret}_{size-suffix}.jpg
                val thumbnailsSmall = "https://live.staticflickr.com/${server}/${id}_${secret}_q.jpg" // q -> thumbnail 150 px
                val thumbnailsBig = "https://live.staticflickr.com/${server}/${id}_${secret}_b.jpg"  // b -> large 1024 px


                val newPhoto = Photo(farm, id, isfamily, isfriend, ispublic, owner, secret, server, title, thumbnailsSmall, thumbnailsBig)
                photosList.add(newPhoto)
            }
            adapter.update(photosList)

            for (i in 0 until photos.length()) {
                Log.d("Main", "${photosList[i].thumbnailsSmall}")
                Log.d("Main", "${photosList[i].thumbnailsBig}")
            }
        }
    }

    fun showImageAlert (imageUrl: String, title: String) {
        val dialogView = layoutInflater.inflate(R.layout.image_alert, null)
        val binding = ImageAlertBinding.bind(dialogView)

        Glide.with(this).load(imageUrl).into(binding.ivBig)
        binding.tvTitle.text = title

        val dialogBuilder = AlertDialog.Builder(this)


        dialogBuilder.setView(dialogView)
        dialogBuilder.show()

    }
}