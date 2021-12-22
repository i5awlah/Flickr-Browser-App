package com.example.flickrbrowserapp.models.jsonModel

data class Photos(
    val page: Int,
    val pages: Int,
    val perpage: Int,
    val photo: ArrayList<Photo>,
    val total: Int
)