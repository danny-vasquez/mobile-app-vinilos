package com.uniandes.appmoviles.vinilos

import com.uniandes.appmoviles.vinilos.model.Album
import com.uniandes.appmoviles.vinilos.model.ArtistDetail
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.text.SimpleDateFormat
import java.util.Locale

class ArtistDetailModelTest {

    // ─── Modelo ArtistDetail ─────────────────────────────────────────────────

    @Test
    fun `artistDetail holds expected values`() {
        val detail = makeArtistDetail()
        assertEquals(1, detail.id)
        assertEquals("Rubén Blades", detail.name)
        assertEquals("Cantante y compositor panameño", detail.description)
        assertEquals("https://example.com/ruben.jpg", detail.image)
    }

    @Test
    fun `two artistDetails with same data are equal`() {
        assertEquals(makeArtistDetail(), makeArtistDetail())
    }

    @Test
    fun `two artistDetails with different id are not equal`() {
        assertNotEquals(
            makeArtistDetail(id = 1),
            makeArtistDetail(id = 2)
        )
    }

    @Test
    fun `two artistDetails with different name are not equal`() {
        assertNotEquals(
            makeArtistDetail(name = "Rubén Blades"),
            makeArtistDetail(name = "Carlos Vives")
        )
    }

    // ─── Fecha del artista ────────────────────────────────────────────────────

    @Test
    fun `date field holds birthDate for musicians`() {
        val detail = makeArtistDetail(date = "1948-07-16T05:00:00.000Z")
        assertEquals("16 jul. 1948", formatDate(detail.date))
    }

    @Test
    fun `date field holds creationDate for bands`() {
        val detail = makeArtistDetail(date = "1960-01-01T00:00:00.000Z")
        assertEquals("1 ene. 1960", formatDate(detail.date))
    }

    @Test
    fun `formatDate returns original string on invalid date`() {
        val bad = "no-date"
        assertEquals(bad, formatDate(bad))
    }

    @Test
    fun `formatDate handles empty date gracefully`() {
        val detail = makeArtistDetail(date = "")
        assertEquals("", formatDate(detail.date))
    }

    // ─── Discografía ─────────────────────────────────────────────────────────

    @Test
    fun `artistDetail with empty albums has empty discography`() {
        val detail = makeArtistDetail(albums = emptyList())
        assertTrue(detail.albums.isEmpty())
    }

    @Test
    fun `artistDetail with albums stores all entries`() {
        val albums = listOf(makeAlbum(id = 1, name = "Buscando América"), makeAlbum(id = 2, name = "Siembra"))
        val detail = makeArtistDetail(albums = albums)
        assertEquals(2, detail.albums.size)
    }

    @Test
    fun `artistDetail albums list contains correct names`() {
        val albums = listOf(makeAlbum(id = 1, name = "Buscando América"), makeAlbum(id = 2, name = "Siembra"))
        val detail = makeArtistDetail(albums = albums)
        assertTrue(detail.albums.any { it.name == "Siembra" })
        assertTrue(detail.albums.any { it.name == "Buscando América" })
    }

    @Test
    fun `no album in discography has blank name`() {
        val albums = listOf(makeAlbum(id = 1, name = "Buscando América"), makeAlbum(id = 2, name = "Siembra"))
        assertFalse(makeArtistDetail(albums = albums).albums.any { it.name.isBlank() })
    }

    @Test
    fun `discography preserves insertion order`() {
        val albums = listOf(makeAlbum(id = 1, name = "Primero"), makeAlbum(id = 2, name = "Segundo"))
        val detail = makeArtistDetail(albums = albums)
        assertEquals("Primero", detail.albums[0].name)
        assertEquals("Segundo", detail.albums[1].name)
    }

    // ─── Helpers ─────────────────────────────────────────────────────────────

    private fun makeArtistDetail(
        id: Int = 1,
        name: String = "Rubén Blades",
        date: String = "1948-07-16T05:00:00.000Z",
        albums: List<Album> = emptyList()
    ) = ArtistDetail(
        id = id,
        name = name,
        image = "https://example.com/ruben.jpg",
        description = "Cantante y compositor panameño",
        date = date,
        albums = albums
    )

    private fun makeAlbum(id: Int, name: String) = Album(
        albumId = id,
        name = name,
        cover = "https://example.com/cover.jpg",
        releaseDate = "1984-01-01T00:00:00.000Z",
        description = "Un gran álbum",
        genre = "Salsa",
        recordLabel = "Elektra"
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
