package com.example.flickrbrowserapp.services

import com.example.flickrbrowserapp.models.jsonModel.PhotoResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface APIInterface {
    companion object {
        const val apiKey = "2c129a93d0769924c943bbcb558d68b3"
    }
    @GET("services/rest/?method=flickr.photos.search&api_key=$apiKey&format=json&&nojsoncallback=1")
    suspend fun getPhotos(@Query("tags") tag: String,
                          @Query("per_page") numberOfImage: Int
    ): Response<PhotoResponse>
}