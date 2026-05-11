package com.uniandes.appmoviles.vinilos.ui.artists

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.uniandes.appmoviles.vinilos.model.Artist
import com.uniandes.appmoviles.vinilos.repository.impl.ArtistRepositoryImpl

class ArtistsViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = ArtistRepositoryImpl(application)

    private val _artists = MutableLiveData<List<Artist>>(emptyList())
    val artists: LiveData<List<Artist>> get() = _artists

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> get() = _error

    fun fetchArtists(forceRefresh: Boolean = false) {
        if (!forceRefresh && _artists.value?.isNotEmpty() == true) return
        if (_isLoading.value == true) return
        _error.value = null
        _isLoading.value = true
        repository.getArtists(
            onComplete = { artists ->
                _artists.postValue(artists)
                _isLoading.postValue(false)
            },
            onError = { exception ->
                _error.postValue(exception.message)
                _isLoading.postValue(false)
            }
        )
    }
}
