package com.uniandes.appmoviles.vinilos.model

data class ArtistDetail(
    val id: Int,
    val name: String,
    val image: String,
    val description: String,
    val date: String,
    val albums: List<Album> = emptyList()
)
