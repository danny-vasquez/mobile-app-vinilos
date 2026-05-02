package com.uniandes.appmoviles.vinilos.repository.interfaces

import com.uniandes.appmoviles.vinilos.model.Artist

interface ArtistRepository {
    fun getArtists(onComplete: (List<Artist>) -> Unit, onError: (Exception) -> Unit)
}
