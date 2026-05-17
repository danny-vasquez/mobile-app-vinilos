package com.uniandes.appmoviles.vinilos.repository.impl

import android.content.Context
import com.uniandes.appmoviles.vinilos.model.Collector
import com.uniandes.appmoviles.vinilos.network.NetworkServiceAdapter
import com.uniandes.appmoviles.vinilos.repository.interfaces.CollectorRepository

class CollectorRepositoryImpl(private val context: Context) : CollectorRepository {

    private val networkAdapter = NetworkServiceAdapter.getInstance(context)

    override fun getCollectors(onComplete: (List<Collector>) -> Unit, onError: (Exception) -> Unit) {
        networkAdapter.getCollectorsList(onComplete, onError)
    }

    override fun addAlbumToCollector(
        collectorId: Int,
        albumId: Int,
        price: Double,
        status: String,
        onComplete: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        networkAdapter.postCollectorAlbum(collectorId, albumId, price, status, onComplete, onError)
    }
}
