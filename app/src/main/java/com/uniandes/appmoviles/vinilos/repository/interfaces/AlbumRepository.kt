package com.uniandes.appmoviles.vinilos.repository.interfaces

import com.uniandes.appmoviles.vinilos.model.Album
import com.uniandes.appmoviles.vinilos.model.AlbumRequest
import com.uniandes.appmoviles.vinilos.model.Track
import com.uniandes.appmoviles.vinilos.model.TrackRequest

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
    fun createAlbum(
        albumRequest: AlbumRequest,
        onComplete: (Album) -> Unit,
        onError: (Exception) -> Unit
    )
    fun addTrack(
        albumId: Int,
        trackRequest: TrackRequest,
        onSuccess: (Track) -> Unit,
        onError: (Exception) -> Unit
    )
}