package com.uniandes.appmoviles.vinilos.ui.albums

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.uniandes.appmoviles.vinilos.model.Track
import com.uniandes.appmoviles.vinilos.model.TrackRequest
import com.uniandes.appmoviles.vinilos.repository.impl.AlbumRepositoryImpl
import com.uniandes.appmoviles.vinilos.repository.interfaces.AlbumRepository

sealed class AddTrackUiState {
    object Idle : AddTrackUiState()
    object Loading : AddTrackUiState()
    data class Success(val track: Track) : AddTrackUiState()
    data class Error(val message: String, val field: String? = null) : AddTrackUiState()
}

class AddTrackViewModel(
    private val albumId: Int,
    private val repository: AlbumRepository
) : ViewModel() {

    private val _uiState = MutableLiveData<AddTrackUiState>(AddTrackUiState.Idle)
    val uiState: LiveData<AddTrackUiState> get() = _uiState

    private val durationRegex = Regex("""^\d{1,2}:\d{2}(:\d{2})?$""")

    fun addTrack(name: String, duration: String) {
        if (_uiState.value is AddTrackUiState.Loading) return

        val validationError = validate(name, duration)
        if (validationError != null) {
            _uiState.value = validationError
            return
        }

        _uiState.value = AddTrackUiState.Loading
        repository.addTrack(
            albumId = albumId,
            trackRequest = TrackRequest(name.trim(), duration.trim()),
            onSuccess = { track ->
                _uiState.postValue(AddTrackUiState.Success(track))
            },
            onError = { exception ->
                _uiState.postValue(AddTrackUiState.Error(exception.message ?: "Error desconocido"))
            }
        )
    }

    private fun validate(name: String, duration: String): AddTrackUiState.Error? {
        if (name.isBlank()) {
            return AddTrackUiState.Error("El nombre es requerido", "name")
        }
        if (duration.isBlank()) {
            return AddTrackUiState.Error("La duración es requerida", "duration")
        }
        if (!durationRegex.matches(duration.trim())) {
            return AddTrackUiState.Error("Formato inválido. Use MM:SS o HH:MM:SS", "duration")
        }
        return null
    }

    fun resetState() {
        _uiState.value = AddTrackUiState.Idle
    }

    class Factory(private val albumId: Int, private val context: Context) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return AddTrackViewModel(albumId, AlbumRepositoryImpl(context)) as T
        }
    }
}
