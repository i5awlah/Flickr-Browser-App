package com.example.flickrbrowserapp.services

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class APIClient {
    companion object {
        var retrofit: Retrofit? = null

        fun getClient() : Retrofit? {

            retrofit = Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl("https://api.flickr.com/")
                .build()
            return retrofit
        }
    }
}