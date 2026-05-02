package com.uniandes.appmoviles.vinilos.network

import android.content.Context
import com.android.volley.NoConnectionError
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.ServerError
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.uniandes.appmoviles.vinilos.model.Album
import com.uniandes.appmoviles.vinilos.model.Artist
import org.json.JSONArray
import org.json.JSONObject

class NetworkServiceAdapter(context: Context) {

    private val requestQueue: RequestQueue by lazy {
        Volley.newRequestQueue(context.applicationContext)
    }

    companion object {
        private var instance: NetworkServiceAdapter? = null
        private const val BASE_URL = "https://mobile-app-vinilos-a848950b5db4.herokuapp.com"

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
        EspressoIdlingResource.increment()
        val request = JsonArrayRequest(
            Request.Method.GET, url, null,
            { response ->
                EspressoIdlingResource.decrement()
                val albums = parseAlbums(response)
                onComplete(albums)
            },
            { error ->
                EspressoIdlingResource.decrement()
                onError(mapVolleyError(error))
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
        EspressoIdlingResource.increment()
        val request = JsonObjectRequest(
            Request.Method.GET, url, null,
            { response ->
                EspressoIdlingResource.decrement()
                try {
                    onComplete(parseAlbum(response))
                } catch (e: Exception) {
                    onError(Exception("Error al procesar los datos del álbum"))
                }
            },
            { error ->
                EspressoIdlingResource.decrement()
                onError(mapVolleyError(error))
            }
        )
        requestQueue.add(request)
    }

    fun getArtists(
        onComplete: (List<Artist>) -> Unit,
        onError: (Exception) -> Unit
    ) {
        val url = "$BASE_URL/musicians"
        EspressoIdlingResource.increment()
        val request = JsonArrayRequest(
            Request.Method.GET, url, null,
            { response ->
                EspressoIdlingResource.decrement()
                onComplete(parseArtists(response))
            },
            { error ->
                EspressoIdlingResource.decrement()
                onError(mapVolleyError(error))
            }
        )
        requestQueue.add(request)
    }

    private fun parseArtists(response: JSONArray): List<Artist> {
        val artists = mutableListOf<Artist>()
        for (i in 0 until response.length()) {
            try {
                artists.add(parseArtist(response.getJSONObject(i)))
            } catch (_: Exception) {
                // skip malformed items
            }
        }
        return artists
    }

    private fun parseArtist(obj: JSONObject): Artist {
        return Artist(
            artistId = obj.optInt("id", 0),
            name = obj.optString("name", ""),
            image = obj.optString("image", ""),
            description = obj.optString("description", ""),
            birthDate = obj.optString("birthDate", "")
        )
    }

    private fun parseAlbums(response: JSONArray): List<Album> {
        val albums = mutableListOf<Album>()
        for (i in 0 until response.length()) {
            try {
                albums.add(parseAlbum(response.getJSONObject(i)))
            } catch (_: Exception) {
                // skip malformed items
            }
        }
        return albums
    }

    private fun parseAlbum(obj: JSONObject): Album {
        return Album(
            albumId = obj.optInt("id", 0),
            name = obj.optString("name", ""),
            cover = obj.optString("cover", ""),
            releaseDate = obj.optString("releaseDate", ""),
            description = obj.optString("description", ""),
            genre = obj.optString("genre", ""),
            recordLabel = obj.optString("recordLabel", "")
        )
    }

    private fun mapVolleyError(error: com.android.volley.VolleyError): Exception {
        return when {
            error is NoConnectionError -> Exception("Sin conexión a internet")
            error is ServerError && error.networkResponse != null -> {
                val code = error.networkResponse.statusCode
                when {
                    code == 404 -> Exception("Recurso no encontrado (404)")
                    code >= 500 -> Exception("Error del servidor ($code)")
                    else -> Exception("Error HTTP $code")
                }
            }
            else -> Exception(error.message ?: "Error desconocido")
        }
    }
}
