package com.uniandes.appmoviles.vinilos.repository.interfaces

import com.uniandes.appmoviles.vinilos.model.Album

interface AlbumRepository {
    fun getAlbums(onComplete: (List<Album>) -> Unit, onError: (Exception) -> Unit)
    fun getAlbum(albumId: Int, onComplete: (Album) -> Unit, onError: (Exception) -> Unit)
    fun getComments(albumId: Int, onComplete: (List<com.uniandes.appmoviles.vinilos.model.Comment>) -> Unit, onError: (Exception) -> Unit)
    fun addComment(
        albumId: Int,
        name: String,
        telephone: String,
        email: String,
        description: String,
        rating: Int,
        onComplete: () -> Unit,
        onError: (Exception) -> Unit
    )
}