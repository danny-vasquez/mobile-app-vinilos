package com.uniandes.appmoviles.vinilos.network

import android.content.Context
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.uniandes.appmoviles.vinilos.model.Album
import org.json.JSONArray
import org.json.JSONObject

class NetworkServiceAdapter(context: Context) {

    private val requestQueue: RequestQueue by lazy {
        Volley.newRequestQueue(context.applicationContext)
    }

    companion object {
        private var instance: NetworkServiceAdapter? = null
        private const val BASE_URL = "https://backvynils-q6yc.onrender.com"

        fun getInstance(context: Context): NetworkServiceAdapter {
            return instance ?: synchronized(this) {
                instance ?: NetworkServiceAdapter(context).also { instance = it }
            }
        }
    }

    fun getAlbums(
        onComplete: (List<Album>) -> Unit,
        onError: (Exception) -> Unit
    ) {
        val url = "$BASE_URL/albums"
        val request = JsonArrayRequest(
            Request.Method.GET, url, null,
            { response ->
                val albums = parseAlbums(response)
                onComplete(albums)
            },
            { error ->
                onError(Exception(error.message))
            }
        )
        requestQueue.add(request)
    }

    fun getAlbum(
        albumId: Int,
        onComplete: (Album) -> Unit,
        onError: (Exception) -> Unit
    ) {
        val url = "$BASE_URL/albums/$albumId"
        val request = JsonObjectRequest(
            Request.Method.GET, url, null,
            { response ->
                val album = parseAlbum(response)
                onComplete(album)
            },
            { error ->
                onError(Exception(error.message))
            }
        )
        requestQueue.add(request)
    }

    private fun parseAlbums(response: JSONArray): List<Album> {
        val albums = mutableListOf<Album>()
        for (i in 0 until response.length()) {
            albums.add(parseAlbum(response.getJSONObject(i)))
        }
        return albums
    }

    private fun parseAlbum(obj: JSONObject): Album {
        return Album(
            albumId = obj.getInt("id"),
            name = obj.getString("name"),
            cover = obj.getString("cover"),
            releaseDate = obj.getString("releaseDate"),
            description = obj.getString("description"),
            genre = obj.getString("genre"),
            recordLabel = obj.getString("recordLabel")
        )
    }
}