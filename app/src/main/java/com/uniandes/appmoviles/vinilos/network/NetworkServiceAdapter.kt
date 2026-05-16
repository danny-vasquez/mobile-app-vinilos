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
import com.uniandes.appmoviles.vinilos.model.AlbumRequest
import com.uniandes.appmoviles.vinilos.model.Artist
import com.uniandes.appmoviles.vinilos.model.ArtistDetail
import com.uniandes.appmoviles.vinilos.model.Comment
import com.uniandes.appmoviles.vinilos.model.Track
import com.uniandes.appmoviles.vinilos.model.TrackRequest
import org.json.JSONArray
import org.json.JSONObject

class NetworkServiceAdapter(context: Context) {

    private val requestQueue: RequestQueue by lazy {
        Volley.newRequestQueue(context.applicationContext)
    }

    private val albumDetailCache = HashMap<Int, Album>()
    private val artistDetailCache = HashMap<String, ArtistDetail>()

    companion object {
        private var instance: NetworkServiceAdapter? = null
        private const val BASE_URL = "https://mobile-app-vinilos-a848950b5db4.herokuapp.com"

        fun getInstance(context: Context): NetworkServiceAdapter {
            return instance ?: synchronized(this) {
                instance ?: NetworkServiceAdapter(context).also { instance = it }
            }
        }
    }

    fun postTrack(
        albumId: Int,
        trackRequest: TrackRequest,
        onComplete: (Track) -> Unit,
        onError: (Exception) -> Unit
    ) {
        val url = "$BASE_URL/albums/$albumId/tracks"
        val body = JSONObject().apply {
            put("name", trackRequest.name)
            put("duration", trackRequest.duration)
        }
        EspressoIdlingResource.increment()
        val request = JsonObjectRequest(
            Request.Method.POST, url, body,
            { response ->
                EspressoIdlingResource.decrement()
                try {
                    val track = Track(
                        id = response.optInt("id", 0),
                        name = response.optString("name", ""),
                        duration = response.optString("duration", "")
                    )
                    albumDetailCache.remove(albumId)
                    onComplete(track)
                } catch (e: Exception) {
                    onError(Exception("Error al procesar la respuesta del servidor"))
                }
            },
            { error ->
                EspressoIdlingResource.decrement()
                onError(mapVolleyError(error))
            }
        )
        requestQueue.add(request)
    }

    fun postAlbum(
        albumRequest: AlbumRequest,
        onComplete: (Album) -> Unit,
        onError: (Exception) -> Unit
    ) {
        val url = "$BASE_URL/albums"
        val body = JSONObject().apply {
            put("name", albumRequest.name)
            put("cover", albumRequest.cover)
            put("releaseDate", albumRequest.releaseDate)
            put("description", albumRequest.description)
            put("genre", albumRequest.genre)
            put("recordLabel", albumRequest.recordLabel)
        }
        EspressoIdlingResource.increment()
        val request = JsonObjectRequest(
            Request.Method.POST, url, body,
            { response ->
                EspressoIdlingResource.decrement()
                try {
                    onComplete(parseAlbum(response))
                } catch (e: Exception) {
                    onError(Exception("Error al procesar la respuesta del servidor"))
                }
            },
            { error ->
                EspressoIdlingResource.decrement()
                onError(mapVolleyError(error))
            }
        )
        requestQueue.add(request)
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
        albumDetailCache[albumId]?.let {
            onComplete(it)
            return
        }
        val url = "$BASE_URL/albums/$albumId"
        EspressoIdlingResource.increment()
        val request = JsonObjectRequest(
            Request.Method.GET, url, null,
            { response ->
                EspressoIdlingResource.decrement()
                try {
                    val album = parseAlbum(response)
                    albumDetailCache[albumId] = album
                    onComplete(album)
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

    fun getComments(
        albumId: Int,
        onComplete: (List<Comment>) -> Unit,
        onError: (Exception) -> Unit
    ) {
        val url = "$BASE_URL/albums/$albumId/comments"
        EspressoIdlingResource.increment()
        val request = JsonArrayRequest(
            Request.Method.GET, url, null,
            { response ->
                EspressoIdlingResource.decrement()
                val comments = mutableListOf<Comment>()
                for (i in 0 until response.length()) {
                    val obj = response.getJSONObject(i)
                    val collector = obj.optJSONObject("collector")
                    comments.add(
                        Comment(
                            id = obj.optInt("id"),
                            description = obj.optString("description"),
                            rating = obj.optInt("rating"),
                            collectorId = collector?.optInt("id"),
                            collectorName = collector?.optString("name") ?: ""
                        )
                    )
                }
                onComplete(comments)
            },
            { error ->
                EspressoIdlingResource.decrement()
                onError(mapVolleyError(error))
            }
        )
        requestQueue.add(request)
    }

    fun postCollector(
        name: String,
        telephone: String,
        email: String,
        onComplete: (Int) -> Unit,
        onError: (Exception) -> Unit
    ) {
        val url = "$BASE_URL/collectors"
        val body = JSONObject().apply {
            put("name", name)
            put("telephone", telephone)
            put("email", email)
        }
        EspressoIdlingResource.increment()
        val request = JsonObjectRequest(
            Request.Method.POST, url, body,
            { response ->
                EspressoIdlingResource.decrement()
                val collectorId = response.optInt("id", -1)
                if (collectorId != -1) onComplete(collectorId)
                else onError(Exception("Error al obtener ID del coleccionista"))
            },
            { error ->
                EspressoIdlingResource.decrement()
                onError(mapVolleyError(error))
            }
        )
        requestQueue.add(request)
    }

    fun postComment(
        albumId: Int,
        collectorId: Int,
        description: String,
        rating: Int,
        onComplete: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        val url = "$BASE_URL/albums/$albumId/comments"
        val body = JSONObject().apply {
            put("description", description)
            put("rating", rating)
            put("collector", JSONObject().apply { put("id", collectorId) })
        }
        EspressoIdlingResource.increment()
        val request = JsonObjectRequest(
            Request.Method.POST, url, body,
            { _ ->
                EspressoIdlingResource.decrement()
                onComplete()
            },
            { error ->
                EspressoIdlingResource.decrement()
                onError(mapVolleyError(error))
            }
        )
        requestQueue.add(request)
    }

    fun getCollectors(
        onComplete: (Map<Int, String>) -> Unit,
        onError: (Exception) -> Unit
    ) {
        val url = "$BASE_URL/collectors"
        EspressoIdlingResource.increment()
        val request = JsonArrayRequest(
            Request.Method.GET, url, null,
            { response ->
                EspressoIdlingResource.decrement()
                val collectorsMap = mutableMapOf<Int, String>()
                for (i in 0 until response.length()) {
                    val obj = response.getJSONObject(i)
                    collectorsMap[obj.optInt("id")] = obj.optString("name")
                }
                onComplete(collectorsMap)
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

    fun getArtist(
        artistId: Int,
        onComplete: (Artist) -> Unit,
        onError: (Exception) -> Unit
    ) {
        val url = "$BASE_URL/musicians/$artistId"
        EspressoIdlingResource.increment()
        val request = JsonObjectRequest(
            Request.Method.GET, url, null,
            { response ->
                EspressoIdlingResource.decrement()
                try {
                    onComplete(parseArtist(response))
                } catch (e: Exception) {
                    onError(Exception("Error al procesar los datos del artista"))
                }
            },
            { error ->
                EspressoIdlingResource.decrement()
                onError(mapVolleyError(error))
            }
        )
        requestQueue.add(request)
    }

    fun getArtistDetail(
        artistId: Int,
        artistType: String,
        onComplete: (ArtistDetail) -> Unit,
        onError: (Exception) -> Unit
    ) {
        val cacheKey = "${artistType}_${artistId}"
        artistDetailCache[cacheKey]?.let {
            onComplete(it)
            return
        }
        val endpoint = if (artistType == "band") "bands" else "musicians"
        val url = "$BASE_URL/$endpoint/$artistId"
        EspressoIdlingResource.increment()
        val request = JsonObjectRequest(
            Request.Method.GET, url, null,
            { response ->
                EspressoIdlingResource.decrement()
                try {
                    val detail = parseArtistDetail(response)
                    artistDetailCache[cacheKey] = detail
                    onComplete(detail)
                } catch (e: Exception) {
                    onError(Exception("Error al procesar los datos del artista"))
                }
            },
            { error ->
                EspressoIdlingResource.decrement()
                onError(mapVolleyError(error))
            }
        )
        requestQueue.add(request)
    }

    private fun parseArtistDetail(obj: JSONObject): ArtistDetail {
        val albumsArray = obj.optJSONArray("albums")
        val albums = mutableListOf<Album>()
        if (albumsArray != null) {
            for (i in 0 until albumsArray.length()) {
                try {
                    albums.add(parseAlbum(albumsArray.getJSONObject(i)))
                } catch (_: Exception) { }
            }
        }
        val date = obj.optString("birthDate", "").ifEmpty {
            obj.optString("creationDate", "")
        }
        return ArtistDetail(
            id = obj.optInt("id", 0),
            name = obj.optString("name", ""),
            image = obj.optString("image", ""),
            description = obj.optString("description", ""),
            date = date,
            albums = albums
        )
    }

    private fun parseArtists(response: JSONArray): List<Artist> {
        val artists = mutableListOf<Artist>()
        for (i in 0 until response.length()) {
            try {
                artists.add(parseArtist(response.getJSONObject(i)))
            } catch (_: Exception) { }
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
            } catch (_: Exception) { }
        }
        return albums
    }

    private fun parseAlbum(obj: JSONObject): Album {
        val tracksArray = obj.optJSONArray("tracks")
        val tracks = mutableListOf<Track>()
        if (tracksArray != null) {
            for (i in 0 until tracksArray.length()) {
                val t = tracksArray.getJSONObject(i)
                tracks.add(Track(
                    id = t.optInt("id", 0),
                    name = t.optString("name", ""),
                    duration = t.optString("duration", "")
                ))
            }
        }
        return Album(
            albumId = obj.optInt("id", 0),
            name = obj.optString("name", ""),
            cover = obj.optString("cover", ""),
            releaseDate = obj.optString("releaseDate", ""),
            description = obj.optString("description", ""),
            genre = obj.optString("genre", ""),
            recordLabel = obj.optString("recordLabel", ""),
            tracks = tracks
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
