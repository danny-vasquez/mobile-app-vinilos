package com.uniandes.appmoviles.vinilos.model

data class Comment(
    val id: Int,
    val description: String,
    val rating: Int,
    val collectorId: Int? = null,
    var collectorName: String = ""
)