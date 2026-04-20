package com.uniandes.appmoviles.vinilos.repository.impl

import android.content.Context
import com.uniandes.appmoviles.vinilos.model.Album
import com.uniandes.appmoviles.vinilos.network.NetworkServiceAdapter
import com.uniandes.appmoviles.vinilos.repository.interfaces.AlbumRepository

class AlbumRepositoryImpl(private val context: Context) : AlbumRepository {

    private val networkAdapter = NetworkServiceAdapter.getInstance(context)

    override fun getAlbums(
        onComplete: (List<Album>) -> Unit,
        onError: (Exception) -> Unit
    ) {
        networkAdapter.getAlbums(onComplete, onError)
    }

    override fun getAlbum(
        albumId: Int,
        onComplete: (Album) -> Unit,
        onError: (Exception) -> Unit
    ) {
        networkAdapter.getAlbum(albumId, onComplete, onError)
    }
}