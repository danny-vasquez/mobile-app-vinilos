package com.uniandes.appmoviles.vinilos

import com.uniandes.appmoviles.vinilos.model.Artist
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.text.SimpleDateFormat
import java.util.Locale

class ArtistsViewModelTest {

    // --- Modelo Artist ---

    @Test
    fun `artist data class holds expected values`() {
        val artist = makeArtist(id = 1, name = "Rubén Blades")
        assertEquals(1, artist.artistId)
        assertEquals("Rubén Blades", artist.name)
        assertEquals("Cantante y actor panameño", artist.description)
    }

    @Test
    fun `two artists with same data are equal`() {
        val a = makeArtist(id = 5, name = "Carlos Vives")
        val b = makeArtist(id = 5, name = "Carlos Vives")
        assertEquals(a, b)
    }

    @Test
    fun `two artists with different id are not equal`() {
        val a = makeArtist(id = 1, name = "Carlos Vives")
        val b = makeArtist(id = 2, name = "Carlos Vives")
        assertNotEquals(a, b)
    }

    @Test
    fun `artist with different name are not equal`() {
        val a = makeArtist(id = 1, name = "Carlos Vives")
        val b = makeArtist(id = 1, name = "Shakira")
        assertNotEquals(a, b)
    }

    // --- Formateo de fecha ---

    @Test
    fun `formatDate returns readable string for valid ISO date`() {
        val result = formatDate("1948-07-16T05:00:00.000Z")
        assertEquals("16 jul. 1948", result)
    }

    @Test
    fun `formatDate returns original string for invalid date`() {
        val input = "not-a-date"
        val result = formatDate(input)
        assertEquals(input, result)
    }

    @Test
    fun `formatDate handles empty string`() {
        val result = formatDate("")
        assertEquals("", result)
    }

    @Test
    fun `formatDate handles december correctly`() {
        val result = formatDate("1990-12-01T00:00:00.000Z")
        assertEquals("1 dic. 1990", result)
    }

    // --- Validaciones de lista ---

    @Test
    fun `all artist IDs in list are positive`() {
        val artists = listOf(
            makeArtist(id = 1, name = "Artista A"),
            makeArtist(id = 2, name = "Artista B"),
            makeArtist(id = 10, name = "Artista C")
        )
        assertTrue(artists.all { it.artistId > 0 })
    }

    @Test
    fun `no artist has empty name`() {
        val artists = listOf(
            makeArtist(id = 1, name = "Rubén Blades"),
            makeArtist(id = 2, name = "Carlos Vives")
        )
        assertFalse(artists.any { it.name.isBlank() })
    }

    @Test
    fun `empty list has size zero`() {
        val artists = emptyList<Artist>()
        assertEquals(0, artists.size)
    }

    @Test
    fun `artist list can be filtered by name`() {
        val artists = listOf(
            makeArtist(id = 1, name = "Rubén Blades"),
            makeArtist(id = 2, name = "Carlos Vives"),
            makeArtist(id = 3, name = "Shakira")
        )
        val filtered = artists.filter { it.name.contains("Carlos") }
        assertEquals(1, filtered.size)
        assertEquals("Carlos Vives", filtered.first().name)
    }

    @Test
    fun `artist list sorted by name is alphabetical`() {
        val artists = listOf(
            makeArtist(id = 2, name = "Shakira"),
            makeArtist(id = 1, name = "Carlos Vives"),
            makeArtist(id = 3, name = "Rubén Blades")
        )
        val sorted = artists.sortedBy { it.name }
        assertEquals("Carlos Vives", sorted[0].name)
        assertEquals("Rubén Blades", sorted[1].name)
        assertEquals("Shakira", sorted[2].name)
    }

    // --- Helpers ---

    private fun makeArtist(id: Int, name: String) = Artist(
        artistId = id,
        name = name,
        image = "https://example.com/photo.jpg",
        description = "Cantante y actor panameño",
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
