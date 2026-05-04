package com.uniandes.appmoviles.vinilos.ui.albums

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.uniandes.appmoviles.vinilos.model.Album
import com.uniandes.appmoviles.vinilos.repository.impl.AlbumRepositoryImpl

class AlbumDetailViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AlbumRepositoryImpl(application)

    private val _album = MutableLiveData<Album>()
    val album: LiveData<Album> get() = _album

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> get() = _error

    private val _comments = MutableLiveData<List<com.uniandes.appmoviles.vinilos.model.Comment>>()
    val comments: LiveData<List<com.uniandes.appmoviles.vinilos.model.Comment>> get() = _comments

    private val _commentSuccess = MutableLiveData<Boolean>()
    val commentSuccess: LiveData<Boolean> get() = _commentSuccess

    fun fetchAlbum(albumId: Int) {
        if (_isLoading.value == true) return
        _error.value = null
        _isLoading.value = true
        repository.getAlbum(
            albumId = albumId,
            onComplete = { album ->
                _album.postValue(album)
                fetchComments(albumId)
            },
            onError = { exception ->
                _error.postValue(exception.message)
                _isLoading.postValue(false)
            }
        )
    }

    private fun fetchComments(albumId: Int) {
        repository.getComments(
            albumId = albumId,
            onComplete = { comments ->
                _comments.postValue(comments)
                _isLoading.postValue(false)
            },
            onError = { exception ->
                _error.postValue(exception.message)
                _isLoading.postValue(false)
            }
        )
    }

    fun addComment(
        albumId: Int,
        name: String,
        telephone: String,
        email: String,
        description: String,
        rating: Int
    ) {
        _isLoading.value = true
        _error.value = null
        repository.addComment(
            albumId = albumId,
            name = name,
            telephone = telephone,
            email = email,
            description = description,
            rating = rating,
            onComplete = {
                _commentSuccess.postValue(true)
                fetchComments(albumId)
            },
            onError = { exception ->
                _error.postValue(exception.message)
                _isLoading.postValue(false)
            }
        )
    }
}
