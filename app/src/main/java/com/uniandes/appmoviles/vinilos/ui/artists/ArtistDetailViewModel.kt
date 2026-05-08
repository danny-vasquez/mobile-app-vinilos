package com.uniandes.appmoviles.vinilos.ui.artists

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.uniandes.appmoviles.vinilos.model.ArtistDetail
import com.uniandes.appmoviles.vinilos.repository.impl.ArtistDetailRepository

class ArtistDetailViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = ArtistDetailRepository(application)

    private val _artistDetail = MutableLiveData<ArtistDetail>()
    val artistDetail: LiveData<ArtistDetail> get() = _artistDetail

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> get() = _error

    fun fetchArtist(artistId: Int, artistType: String = "musician") {
        if (_isLoading.value == true) return
        _error.value = null
        _isLoading.value = true
        repository.getArtistDetail(
            artistId = artistId,
            artistType = artistType,
            onComplete = { detail ->
                _artistDetail.postValue(detail)
                _isLoading.postValue(false)
            },
            onError = { exception ->
                _error.postValue(exception.message)
                _isLoading.postValue(false)
            }
        )
    }
}
