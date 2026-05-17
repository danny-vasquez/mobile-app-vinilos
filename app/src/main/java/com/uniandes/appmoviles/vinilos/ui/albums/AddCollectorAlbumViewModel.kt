package com.uniandes.appmoviles.vinilos.ui.albums

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.uniandes.appmoviles.vinilos.model.Collector
import com.uniandes.appmoviles.vinilos.repository.impl.CollectorRepositoryImpl
import com.uniandes.appmoviles.vinilos.repository.interfaces.CollectorRepository

sealed class AddCollectorAlbumUiState {
    object LoadingCollectors : AddCollectorAlbumUiState()
    data class Ready(val collectors: List<Collector>) : AddCollectorAlbumUiState()
    object Saving : AddCollectorAlbumUiState()
    object Success : AddCollectorAlbumUiState()
    data class Error(val message: String, val field: String? = null) : AddCollectorAlbumUiState()
}

class AddCollectorAlbumViewModel(
    private val albumId: Int,
    private val repository: CollectorRepository
) : ViewModel() {

    private val _uiState = MutableLiveData<AddCollectorAlbumUiState>(AddCollectorAlbumUiState.LoadingCollectors)
    val uiState: LiveData<AddCollectorAlbumUiState> get() = _uiState

    init {
        loadCollectors()
    }

    private fun loadCollectors() {
        _uiState.value = AddCollectorAlbumUiState.LoadingCollectors
        repository.getCollectors(
            onComplete = { collectors ->
                _uiState.postValue(AddCollectorAlbumUiState.Ready(collectors))
            },
            onError = { exception ->
                _uiState.postValue(AddCollectorAlbumUiState.Error(exception.message ?: "Error al cargar coleccionistas"))
            }
        )
    }

    fun addAlbumToCollector(collectorId: Int, priceText: String, status: String) {
        val validationError = validate(collectorId, priceText, status)
        if (validationError != null) {
            _uiState.value = validationError
            return
        }

        val price = priceText.trim().toDouble()
        _uiState.value = AddCollectorAlbumUiState.Saving
        repository.addAlbumToCollector(
            collectorId = collectorId,
            albumId = albumId,
            price = price,
            status = status,
            onComplete = {
                _uiState.postValue(AddCollectorAlbumUiState.Success)
            },
            onError = { exception ->
                _uiState.postValue(AddCollectorAlbumUiState.Error(exception.message ?: "Error desconocido"))
            }
        )
    }

    private fun validate(collectorId: Int, priceText: String, status: String): AddCollectorAlbumUiState.Error? {
        if (collectorId <= 0) {
            return AddCollectorAlbumUiState.Error("Selecciona un coleccionista", "collector")
        }
        if (priceText.isBlank()) {
            return AddCollectorAlbumUiState.Error("El precio es requerido", "price")
        }
        if (priceText.trim().toDoubleOrNull() == null) {
            return AddCollectorAlbumUiState.Error("Ingresa un precio válido", "price")
        }
        if (status.isBlank()) {
            return AddCollectorAlbumUiState.Error("Selecciona un estado", "status")
        }
        return null
    }

    fun resetToReady() {
        val current = _uiState.value
        if (current is AddCollectorAlbumUiState.Ready) return
        loadCollectors()
    }

    class Factory(private val albumId: Int, private val context: Context) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return AddCollectorAlbumViewModel(albumId, CollectorRepositoryImpl(context)) as T
        }
    }
}
