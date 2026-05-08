package com.uniandes.appmoviles.vinilos.repository.impl

import android.content.Context
import com.uniandes.appmoviles.vinilos.model.ArtistDetail
import com.uniandes.appmoviles.vinilos.network.NetworkServiceAdapter

class ArtistDetailRepository(private val context: Context) {

    private val networkAdapter = NetworkServiceAdapter.getInstance(context)

    fun getArtistDetail(
        artistId: Int,
        artistType: String,
        onComplete: (ArtistDetail) -> Unit,
        onError: (Exception) -> Unit
    ) {
        networkAdapter.getArtistDetail(artistId, artistType, onComplete, onError)
    }
}
