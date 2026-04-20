package com.uniandes.appmoviles.vinilos.repository.interfaces

import com.uniandes.appmoviles.vinilos.model.Album

interface AlbumRepository {
    fun getAlbums(onComplete: (List<Album>) -> Unit, onError: (Exception) -> Unit)
    fun getAlbum(albumId: Int, onComplete: (Album) -> Unit, onError: (Exception) -> Unit)
}