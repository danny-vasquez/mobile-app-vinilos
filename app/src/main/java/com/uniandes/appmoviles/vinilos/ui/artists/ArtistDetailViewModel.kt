package com.uniandes.appmoviles.vinilos.ui.artists

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.uniandes.appmoviles.vinilos.model.Artist
import com.uniandes.appmoviles.vinilos.repository.impl.ArtistRepositoryImpl

class ArtistDetailViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = ArtistRepositoryImpl(application)

    private val _artist = MutableLiveData<Artist>()
    val artist: LiveData<Artist> get() = _artist

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> get() = _error

    fun fetchArtist(artistId: Int) {
        if (_isLoading.value == true) return
        _error.value = null
        _isLoading.value = true
        repository.getArtist(
            artistId = artistId,
            onComplete = { artist ->
                _artist.postValue(artist)
                _isLoading.postValue(false)
            },
            onError = { exception ->
                _error.postValue(exception.message)
                _isLoading.postValue(false)
            }
        )
    }
}
