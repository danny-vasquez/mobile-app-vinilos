package com.uniandes.appmoviles.vinilos.repository.interfaces

import com.uniandes.appmoviles.vinilos.model.Collector

interface CollectorRepository {
    fun getCollectors(onComplete: (List<Collector>) -> Unit, onError: (Exception) -> Unit)
    fun addAlbumToCollector(
        collectorId: Int,
        albumId: Int,
        price: Double,
        status: String,
        onComplete: () -> Unit,
        onError: (Exception) -> Unit
    )
}
