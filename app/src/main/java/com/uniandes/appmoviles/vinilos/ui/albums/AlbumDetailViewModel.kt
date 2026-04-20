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

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    fun fetchAlbum(albumId: Int) {
        _isLoading.value = true
        repository.getAlbum(
            albumId = albumId,
            onComplete = { album ->
                _album.postValue(album)
                _isLoading.postValue(false)
            },
            onError = { exception ->
                _error.postValue(exception.message)
                _isLoading.postValue(false)
            }
        )
    }
}