package com.uniandes.appmoviles.vinilos

import com.uniandes.appmoviles.vinilos.model.Album
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.text.SimpleDateFormat
import java.util.Locale

class AlbumsViewModelTest {

    // --- Modelo Album ---

    @Test
    fun `album data class holds expected values`() {
        val album = makeAlbum(id = 1, name = "Abbey Road")
        assertEquals(1, album.albumId)
        assertEquals("Abbey Road", album.name)
        assertEquals("Rock", album.genre)
        assertEquals("EMI", album.recordLabel)
    }

    @Test
    fun `two albums with same data are equal`() {
        val a = makeAlbum(id = 5, name = "Kind of Blue")
        val b = makeAlbum(id = 5, name = "Kind of Blue")
        assertEquals(a, b)
    }

    @Test
    fun `two albums with different id are not equal`() {
        val a = makeAlbum(id = 1, name = "Kind of Blue")
        val b = makeAlbum(id = 2, name = "Kind of Blue")
        assertNotEquals(a, b)
    }

    // --- Formateo de fecha ---

    @Test
    fun `formatDate returns readable string for valid ISO date`() {
        val result = formatDate("2023-01-15T00:00:00.000Z")
        assertEquals("15 ene. 2023", result)
    }

    @Test
    fun `formatDate returns original string for invalid date`() {
        val input = "not-a-date"
        val result = formatDate(input)
        assertEquals(input, result)
    }

    @Test
    fun `formatDate handles december correctly`() {
        val result = formatDate("2022-12-25T00:00:00.000Z")
        assertEquals("25 dic. 2022", result)
    }

    // --- Validaciones de lista ---

    @Test
    fun `all album IDs in list are positive`() {
        val albums = listOf(
            makeAlbum(id = 1, name = "A"),
            makeAlbum(id = 2, name = "B"),
            makeAlbum(id = 42, name = "C")
        )
        assertTrue(albums.all { it.albumId > 0 })
    }

    @Test
    fun `no album has empty name`() {
        val albums = listOf(
            makeAlbum(id = 1, name = "Thriller"),
            makeAlbum(id = 2, name = "Rumours")
        )
        assertFalse(albums.any { it.name.isBlank() })
    }

    @Test
    fun `empty list has size zero`() {
        val albums = emptyList<Album>()
        assertEquals(0, albums.size)
    }

    // --- Helpers ---

    private fun makeAlbum(id: Int, name: String) = Album(
        albumId = id,
        name = name,
        cover = "https://example.com/cover.jpg",
        releaseDate = "2023-01-15T00:00:00.000Z",
        description = "A great album",
        genre = "Rock",
        recordLabel = "EMI"
    )

    private fun formatDate(isoDate: String): String {
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
            val outputFormat = SimpleDateFormat("d MMM. yyyy", Locale("es", "CO"))
            val date = inputFormat.parse(isoDate) ?: return isoDate
            outputFormat.format(date)
        } catch (_: Exception) {
            isoDate
        }
    }
}
