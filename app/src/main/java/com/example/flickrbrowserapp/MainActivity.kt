package com.example.flickrbrowserapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.SeekBar
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.flickrbrowserapp.databinding.ActivityMainBinding
import com.example.flickrbrowserapp.databinding.ImageAlertBinding
import com.example.flickrbrowserapp.model.PhotoModel
import com.example.flickrbrowserapp.services.APIClient
import com.example.flickrbrowserapp.services.APIInterface
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

    private var photosList = arrayListOf<PhotoModel>()


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
            }

            override fun onStopTrackingTouch(seek: SeekBar) {
                numberOfImage = seek.progress.toString().toInt()
//                requestAPI()
                requestAPIByRetrofit()
            }
        })

        setupRV()
//        requestAPI()
        requestAPIByRetrofit()
        binding.btnSearch.setOnClickListener {
            tag = binding.etSearch.text.toString()
            if (tag.isNotEmpty()) {
//                requestAPI()
                requestAPIByRetrofit()
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
                val id = photos.getJSONObject(i).getString("id")
                val secret = photos.getJSONObject(i).getString("secret")
                val server = photos.getJSONObject(i).getString("server")
                val title = photos.getJSONObject(i).getString("title")

                // Photo Image URLs: https://live.staticflickr.com/{server-id}/{id}_{secret}_{size-suffix}.jpg
                val thumbnailsSmall = "https://live.staticflickr.com/${server}/${id}_${secret}_q.jpg" // q -> thumbnail 150 px
                val thumbnailsBig = "https://live.staticflickr.com/${server}/${id}_${secret}_b.jpg"  // b -> large 1024 px


                val newPhoto = PhotoModel(title, thumbnailsSmall, thumbnailsBig)
                photosList.add(newPhoto)
            }
            adapter.update(photosList)
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

    private fun requestAPIByRetrofit() {
        photosList.clear()
        CoroutineScope(IO).launch {
            val response = APIClient.getClient()?.create(APIInterface::class.java)!!.getPhotos(tag, numberOfImage)

            if (response.isSuccessful()) {
                var photos = response?.body()!!.photos.photo

                for (i in 0 until photos.size) {
                    val id = photos[i].id
                    val server = photos[i].server
                    val secret = photos[i].secret

                    val thumbnailsSmall = "https://live.staticflickr.com/$server/${id}_${secret}_q.jpg" // q -> thumbnail 150 px
                    val thumbnailsBig = "https://live.staticflickr.com/$server/${id}_${secret}_b.jpg"  // b -> large 1024 px
                    photosList.add(PhotoModel(photos[i].title, thumbnailsSmall, thumbnailsBig))
                }

                withContext(Main){
                    adapter.update(photosList)
                }

            } else {
                Log.d("Main", "Unable to get data")
            }
        }

    }
}