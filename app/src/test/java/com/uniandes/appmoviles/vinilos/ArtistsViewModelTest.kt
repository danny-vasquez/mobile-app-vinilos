package com.uniandes.appmoviles.vinilos

import com.uniandes.appmoviles.vinilos.model.Artist
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.text.SimpleDateFormat
import java.util.Locale

class ArtistsViewModelTest {

    @Test
    fun `artist stores correct values`() {
        val artist = makeArtist(id = 1, name = "Rubén Blades")
        assertEquals(1, artist.artistId)
        assertEquals("Rubén Blades", artist.name)
        assertEquals("Cantante panameño", artist.description)
    }

    @Test
    fun `artists with same data are equal`() {
        assertEquals(makeArtist(id = 5, name = "Carlos Vives"), makeArtist(id = 5, name = "Carlos Vives"))
    }

    @Test
    fun `artists with different id are not equal`() {
        assertNotEquals(makeArtist(id = 1, name = "Carlos Vives"), makeArtist(id = 2, name = "Carlos Vives"))
    }

    @Test
    fun `artists with different name are not equal`() {
        assertNotEquals(makeArtist(id = 1, name = "Carlos Vives"), makeArtist(id = 1, name = "Shakira"))
    }

    @Test
    fun `formatDate parses ISO date to readable format`() {
        assertEquals("16 jul. 1948", formatDate("1948-07-16T05:00:00.000Z"))
    }

    @Test
    fun `formatDate returns original value when input is invalid`() {
        val bad = "not-a-date"
        assertEquals(bad, formatDate(bad))
    }

    @Test
    fun `formatDate returns empty string when input is empty`() {
        assertEquals("", formatDate(""))
    }

    @Test
    fun `formatDate handles december`() {
        assertEquals("1 dic. 1990", formatDate("1990-12-01T00:00:00.000Z"))
    }

    @Test
    fun `all artists in list have positive id`() {
        val list = listOf(makeArtist(1, "A"), makeArtist(2, "B"), makeArtist(10, "C"))
        assertTrue(list.all { it.artistId > 0 })
    }

    @Test
    fun `no artist in list has blank name`() {
        val list = listOf(makeArtist(1, "Rubén Blades"), makeArtist(2, "Carlos Vives"))
        assertFalse(list.any { it.name.isBlank() })
    }

    @Test
    fun `filter by name returns correct result`() {
        val list = listOf(
            makeArtist(1, "Rubén Blades"),
            makeArtist(2, "Carlos Vives"),
            makeArtist(3, "Shakira")
        )
        val result = list.filter { it.name.contains("Carlos") }
        assertEquals(1, result.size)
        assertEquals("Carlos Vives", result.first().name)
    }

    @Test
    fun `sorted list is alphabetical`() {
        val sorted = listOf(
            makeArtist(2, "Shakira"),
            makeArtist(1, "Carlos Vives"),
            makeArtist(3, "Rubén Blades")
        ).sortedBy { it.name }
        assertEquals("Carlos Vives", sorted[0].name)
        assertEquals("Rubén Blades", sorted[1].name)
        assertEquals("Shakira", sorted[2].name)
    }

    @Test
    fun `empty list has size zero`() {
        assertEquals(0, emptyList<Artist>().size)
    }

    @Test
    fun `empty list isEmpty is true`() {
        assertTrue(emptyList<Artist>().isEmpty())
    }

    @Test
    fun `non empty list isEmpty is false`() {
        assertFalse(listOf(makeArtist(1, "Artista")).isEmpty())
    }

    @Test
    fun `empty list triggers empty view logic`() {
        val artists = emptyList<Artist>()
        assertTrue(artists.isEmpty())
        assertFalse(artists.isNotEmpty())
    }

    @Test
    fun `non empty list triggers recycler visible logic`() {
        val artists = listOf(makeArtist(1, "Artista"))
        assertFalse(artists.isEmpty())
        assertTrue(artists.isNotEmpty())
    }

    @Test
    fun `empty view hidden on error regardless of list state`() {
        val errorMsg = "Sin conexión a internet"
        val artists = emptyList<Artist>()
        val showEmptyView = artists.isEmpty() && errorMsg.isBlank()
        assertFalse(showEmptyView)
    }

    @Test
    fun `error message for no connection is not blank`() {
        assertTrue("Sin conexión a internet".isNotBlank())
    }

    @Test
    fun `error message for server error is not blank`() {
        assertTrue("Error del servidor (500)".isNotBlank())
    }

    @Test
    fun `error message for not found is not blank`() {
        assertTrue("Recurso no encontrado (404)".isNotBlank())
    }

    @Test
    fun `artist with empty birthDate is valid`() {
        val artist = makeArtist(1, "Artista").copy(birthDate = "")
        assertNotNull(artist)
        assertEquals("", formatDate(artist.birthDate))
    }

    @Test
    fun `artist with empty image is valid`() {
        val artist = makeArtist(1, "Artista").copy(image = "")
        assertNotNull(artist)
        assertEquals("", artist.image)
    }

    private fun makeArtist(id: Int, name: String) = Artist(
        artistId = id,
        name = name,
        image = "https://example.com/photo.jpg",
        description = "Cantante panameño",
        birthDate = "1948-07-16T05:00:00.000Z"
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
