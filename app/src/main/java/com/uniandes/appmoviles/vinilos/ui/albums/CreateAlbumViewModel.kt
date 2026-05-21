package com.uniandes.appmoviles.vinilos.ui.albums

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.uniandes.appmoviles.vinilos.model.Album
import com.uniandes.appmoviles.vinilos.model.AlbumRequest
import com.uniandes.appmoviles.vinilos.repository.impl.AlbumRepositoryImpl
import com.uniandes.appmoviles.vinilos.repository.interfaces.AlbumRepository

sealed class CreateAlbumUiState {
    object Idle : CreateAlbumUiState()
    object Loading : CreateAlbumUiState()
    data class Success(val album: Album) : CreateAlbumUiState()
    data class Error(val message: String, val field: String? = null) : CreateAlbumUiState()
}

class CreateAlbumViewModel(private val repository: AlbumRepository) : ViewModel() {

    private val _uiState = MutableLiveData<CreateAlbumUiState>(CreateAlbumUiState.Idle)
    val uiState: LiveData<CreateAlbumUiState> get() = _uiState

    fun createAlbum(
        name: String,
        cover: String,
        releaseDate: String,
        description: String,
        genre: String,
        recordLabel: String
    ) {
        if (_uiState.value is CreateAlbumUiState.Loading) return

        val validationError = validate(name, cover, releaseDate, description, genre, recordLabel)
        if (validationError != null) {
            _uiState.value = validationError
            return
        }

        _uiState.value = CreateAlbumUiState.Loading
        repository.createAlbum(
            AlbumRequest(name, cover, releaseDate, description, genre, recordLabel),
            onComplete = { album ->
                _uiState.postValue(CreateAlbumUiState.Success(album))
            },
            onError = { exception ->
                _uiState.postValue(
                    CreateAlbumUiState.Error(exception.message ?: "Error desconocido")
                )
            }
        )
    }

    private fun validate(
        name: String,
        cover: String,
        releaseDate: String,
        description: String,
        genre: String,
        recordLabel: String
    ): CreateAlbumUiState.Error? {
        if (name.isBlank()) {
            return CreateAlbumUiState.Error("El nombre es requerido", "name")
        }
        if (cover.isBlank()) {
            return CreateAlbumUiState.Error("La URL de la portada es requerida", "cover")
        }
        if (!cover.startsWith("http://") && !cover.startsWith("https://")) {
            return CreateAlbumUiState.Error("La URL debe comenzar con http:// o https://", "cover")
        }
        if (releaseDate.isBlank()) {
            return CreateAlbumUiState.Error("La fecha de lanzamiento es requerida", "releaseDate")
        }
        if (description.isBlank()) {
            return CreateAlbumUiState.Error("La descripción es requerida", "description")
        }
        if (genre.isBlank()) {
            return CreateAlbumUiState.Error("Debes seleccionar un género", "genre")
        }
        if (recordLabel.isBlank()) {
            return CreateAlbumUiState.Error("Debes seleccionar un sello discográfico", "recordLabel")
        }
        return null
    }

    fun resetState() {
        _uiState.value = CreateAlbumUiState.Idle
    }

    class Factory(private val context: Context) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return CreateAlbumViewModel(AlbumRepositoryImpl(context)) as T
        }
    }
}
