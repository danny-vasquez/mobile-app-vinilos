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

    override fun getComments(
        albumId: Int,
        onComplete: (List<com.uniandes.appmoviles.vinilos.model.Comment>) -> Unit,
        onError: (Exception) -> Unit
    ) {
        networkAdapter.getComments(albumId, { comments ->
            networkAdapter.getCollectors({ collectorsMap ->
                comments.forEach { comment ->
                    // Si el nombre viene vacío de la API de comentarios, lo buscamos en el mapa de coleccionistas
                    if (comment.collectorName.isEmpty() && comment.collectorId != null) {
                        comment.collectorName = collectorsMap[comment.collectorId] ?: "Coleccionista ${comment.collectorId}"
                    } else if (comment.collectorName.isEmpty()) {
                        comment.collectorName = "Anónimo"
                    }
                }
                onComplete(comments)
            }, {
                // Si falla obtener coleccionistas, nos aseguramos de que no queden nombres vacíos
                comments.forEach { if (it.collectorName.isEmpty()) it.collectorName = "Coleccionista" }
                onComplete(comments)
            })
        }, onError)
    }

    override fun addComment(
        albumId: Int,
        name: String,
        telephone: String,
        email: String,
        description: String,
        rating: Int,
        onComplete: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        networkAdapter.postCollector(
            name = name,
            telephone = telephone,
            email = email,
            onComplete = { collectorId ->
                networkAdapter.postComment(
                    albumId = albumId,
                    collectorId = collectorId,
                    description = description,
                    rating = rating,
                    onComplete = onComplete,
                    onError = onError
                )
            },
            onError = onError
        )
    }
}