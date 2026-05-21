package com.uniandes.appmoviles.vinilos

import com.uniandes.appmoviles.vinilos.model.Album
import com.uniandes.appmoviles.vinilos.model.Track
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.text.SimpleDateFormat
import java.util.Locale

class AlbumDetailModelTest {

    // ─── Track ───────────────────────────────────────────────────────────────

    @Test
    fun `track holds expected values`() {
        val track = Track(id = 1, name = "Come Together", duration = "4:19")
        assertEquals(1, track.id)
        assertEquals("Come Together", track.name)
        assertEquals("4:19", track.duration)
    }

    @Test
    fun `two tracks with same data are equal`() {
        val t1 = Track(id = 5, name = "Something", duration = "3:03")
        val t2 = Track(id = 5, name = "Something", duration = "3:03")
        assertEquals(t1, t2)
    }

    @Test
    fun `two tracks with different id are not equal`() {
        val t1 = Track(id = 1, name = "Something", duration = "3:03")
        val t2 = Track(id = 2, name = "Something", duration = "3:03")
        assertNotEquals(t1, t2)
    }

    @Test
    fun `two tracks with different duration are not equal`() {
        val t1 = Track(id = 1, name = "Something", duration = "3:03")
        val t2 = Track(id = 1, name = "Something", duration = "4:00")
        assertNotEquals(t1, t2)
    }

    // ─── Álbum con tracks ─────────────────────────────────────────────────────

    @Test
    fun `album with tracks stores all tracks`() {
        val tracks = listOf(
            Track(id = 1, name = "Come Together", duration = "4:19"),
            Track(id = 2, name = "Something", duration = "3:03"),
            Track(id = 3, name = "Here Comes the Sun", duration = "3:05")
        )
        val album = makeAlbum(tracks = tracks)
        assertEquals(3, album.tracks.size)
    }

    @Test
    fun `album with no tracks has empty list`() {
        val album = makeAlbum(tracks = emptyList())
        assertTrue(album.tracks.isEmpty())
    }

    @Test
    fun `album tracks can be found by name`() {
        val tracks = listOf(
            Track(id = 1, name = "Come Together", duration = "4:19"),
            Track(id = 2, name = "Something", duration = "3:03")
        )
        val album = makeAlbum(tracks = tracks)
        val found = album.tracks.find { it.name == "Something" }
        assertEquals("3:03", found?.duration)
    }

    @Test
    fun `album tracks list preserves insertion order`() {
        val tracks = listOf(
            Track(id = 1, name = "First", duration = "3:00"),
            Track(id = 2, name = "Second", duration = "4:00"),
            Track(id = 3, name = "Third", duration = "2:00")
        )
        val album = makeAlbum(tracks = tracks)
        assertEquals("First", album.tracks[0].name)
        assertEquals("Third", album.tracks[2].name)
    }

    @Test
    fun `all tracks in album have positive id`() {
        val tracks = listOf(
            Track(id = 1, name = "T1", duration = "3:00"),
            Track(id = 2, name = "T2", duration = "4:00")
        )
        assertTrue(makeAlbum(tracks = tracks).tracks.all { it.id > 0 })
    }

    @Test
    fun `no track in album has blank name`() {
        val tracks = listOf(
            Track(id = 1, name = "Come Together", duration = "4:19"),
            Track(id = 2, name = "Something", duration = "3:03")
        )
        assertFalse(makeAlbum(tracks = tracks).tracks.any { it.name.isBlank() })
    }

    // ─── Formato de fecha para detalle ───────────────────────────────────────

    @Test
    fun `formatDate converts ISO date to readable Spanish`() {
        val result = formatDate("1969-09-26T00:00:00.000Z")
        assertTrue("Expected day 26 in result but was: $result", result.startsWith("26"))
        assertTrue("Expected year 1969 in result but was: $result", result.endsWith("1969"))
        assertFalse("Expected date to be transformed", result == "1969-09-26T00:00:00.000Z")
    }

    @Test
    fun `formatDate returns original string on invalid input`() {
        val bad = "not-a-date"
        assertEquals(bad, formatDate(bad))
    }

    // ─── Helpers ─────────────────────────────────────────────────────────────

    private fun makeAlbum(tracks: List<Track> = emptyList()) = Album(
        albumId = 1,
        name = "Abbey Road",
        cover = "https://example.com/cover.jpg",
        releaseDate = "1969-09-26T00:00:00.000Z",
        description = "Iconic album by The Beatles",
        genre = "Rock",
        recordLabel = "EMI",
        tracks = tracks
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
