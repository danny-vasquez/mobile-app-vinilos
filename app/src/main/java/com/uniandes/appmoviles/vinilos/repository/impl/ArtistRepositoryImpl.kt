package com.uniandes.appmoviles.vinilos.repository.impl

import android.content.Context
import com.uniandes.appmoviles.vinilos.model.Artist
import com.uniandes.appmoviles.vinilos.network.NetworkServiceAdapter
import com.uniandes.appmoviles.vinilos.repository.interfaces.ArtistRepository

class ArtistRepositoryImpl(private val context: Context) : ArtistRepository {

    private val networkAdapter = NetworkServiceAdapter.getInstance(context)

    override fun getArtists(onComplete: (List<Artist>) -> Unit, onError: (Exception) -> Unit) {
        networkAdapter.getArtists(onComplete, onError)
    }

    override fun getArtist(artistId: Int, onComplete: (Artist) -> Unit, onError: (Exception) -> Unit) {
        networkAdapter.getArtist(artistId, onComplete, onError)
    }
}
