package com.uniandes.appmoviles.vinilos.ui.albums

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.uniandes.appmoviles.vinilos.model.Album
import com.uniandes.appmoviles.vinilos.repository.impl.AlbumRepositoryImpl

class AlbumsViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AlbumRepositoryImpl(application)

    private val _albums = MutableLiveData<List<Album>>(emptyList())
    val albums: LiveData<List<Album>> get() = _albums

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> get() = _error

    fun fetchAlbums(forceRefresh: Boolean = false) {
        if (!forceRefresh && _albums.value?.isNotEmpty() == true) return
        if (_isLoading.value == true) return
        _error.value = null
        _isLoading.value = true
        repository.getAlbums(
            onComplete = { albums ->
                _albums.postValue(albums)
                _isLoading.postValue(false)
            },
            onError = { exception ->
                _error.postValue(exception.message)
                _isLoading.postValue(false)
            }
        )
    }
}
